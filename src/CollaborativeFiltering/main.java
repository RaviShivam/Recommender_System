package CollaborativeFiltering;

import Reader.Rating;
import Reader.RatingList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool fork =  new ForkJoinPool(cores);

        Database database = new Database();
        UserBased userBased = new UserBased(database);
        ItemBased itemBased = new ItemBased(database);
        RatingList predRatings = new RatingList();
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();
        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double predictionitem = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
//                                double predictionuser = userBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
//                                double prediction = (predictionitem+predictionuser)/2;
                                rating.setRating(predictionitem);
                            })).get();
        finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submissions/submission.csv");
        //=======================================================

        //=======================Test Code=======================
//        System.out.println(Computations.round(3.001));
//        System.out.println(itemBased.getCorrelationCoefficentPearson(1,6));
//        System.out.println(itemBased.predictRating(5,1));

        //=======================================================
        //====================Single Threads==================
//        float start = System.currentTimeMillis();
//        for (int i = 0; i < 100; i++) {
//            System.out.print("\r" + i*100/100 + "%");
//            double predictionitem = itemBased.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
//            double predictionuser = userBased.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
//            double prediction = (predictionitem+predictionuser)/2;
//            predRatings.get(i).setRating(prediction);
//        }
//        System.out.println("Runtime: " + Float.toString(System.currentTimeMillis()-start));
//        predRatings.writeResultsFile("submission.csv");
    }

}
