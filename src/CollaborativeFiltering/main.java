package CollaborativeFiltering;

import FilterRevised.Predictor;
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
//        ItemBased itemBased = new ItemBased(database);
        Predictor itemBased = new Predictor(database.getUserToMovieMap(),database.getMovieToUserMap(),database.getMovieMean(), database.getUsersMean());
        RatingList predRatings = new RatingList();
        itemBased.setDatabase(database);
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();
//        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);//.subList(0,6);
            try {
                fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double predictionitem = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
//                                double predictionuser = itemBased.naivePredictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
//                                double prediction = (0.8*predictionitem) + (0.2*predictionuser);
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
//        System.out.println(itemBased.getCorrelationCoefficentPearson(53,1));
//        System.out.println(itemBased.predictRating(23,53));

        //=======================================================
        //====================Single Threads==================
//        float start = System.currentTimeMillis();
//        for (int i = 0; i < 100; i++) {
//            System.out.print("\r" + i*100/100 + "%");
//            int user = predRatings.get(i).getUser().getIndex();
//            int movie = predRatings.get(i).getMovie().getIndex();
//            System.out.println(user + " " + movie);
//            double predictionitem = itemBased.predictRating(user, movie);
////            double predictionuser = userBased.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
////            double prediction = (predictionitem+predictionuser)/2;
//            predRatings.get(i).setRating(predictionitem);
//        }
//        System.out.println("Runtime: " + Float.toString(System.currentTimeMillis()-start));
//        predRatings.writeResultsFile("submission.csv");
    }

}
