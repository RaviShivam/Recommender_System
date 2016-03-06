package CollaborativeFiltering;

import Reader.Rating;
import Reader.RatingList;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int cores = 8;
        ForkJoinPool fork =  new ForkJoinPool(cores);

        Database database = new Database();
        UserBased userBased = new UserBased(database);
        ItemBased itemBased = new ItemBased(database);
        RatingList predRatings = new RatingList();
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();

        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
        long start = System.currentTimeMillis()/1000;
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double prediction = userBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
                                rating.setRating(prediction);
                            })).get();
        finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Algorithm runtime: " + Float.toString(System.currentTimeMillis()/1000-start));
        finalList.writeResultsFile("submissions/submission.csv");
        //=======================================================

        //=======================Test Code=======================
        System.out.println(database.getMovieToUserMap().get(45));
        System.out.println(database.getMovieToUserMap().get(56));

        System.out.println(itemBased.getCorrelationCoefficentPearson(45,56));

        //=======================================================
        //====================Single Threads==================
//        for (int i = 0; i < predRatings.size(); i++) {
//            System.out.print("\r" + i*100/predRatings.size() + "%");
//            double rating = itemBased.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
//            predRatings.get(i).setRating(rating);
//        }
//        predRatings.writeResultsFile("submission.csv");
    }

}
