package FilterRevised;

import CollaborativeFiltering.Database;
import Reader.Rating;
import Reader.RatingList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by ravishivam on 17-3-16.
 */
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Database database = new Database();
//        predictHybridRating();
//        predictItemBased();
//        predictUserBased();

        RatingList userbased = new RatingList();
        RatingList itembased = new RatingList();
        userbased.readFile("submissions/userBasedPrediction.csv", database.getUserList(),database.getMovieList());
        itembased.readFile("submissions/itemBasedPrediction.csv", database.getUserList(),database.getMovieList());


    }

    static void predictItemBased() throws IOException, ClassNotFoundException {
        //Init multithreading
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool fork =  new ForkJoinPool(cores);

        //Init data and predictors.
        Database database = new Database();
        Predictor itemBased = new Predictor(database.getUserToMovieMap(),database.getMovieToUserMap(),database.getMovieMean(), database.getUsersMean());
        System.out.println("Database is ready. Starting item-based predictions now.....");

        RatingList predRatings = new RatingList();
        itemBased.setDatabase(database);
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();
//        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double predictionitem = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
                                rating.setRating(predictionitem);
                            })).get();
            finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submissions/itemBasedPrediction.csv");
        System.out.println("Item-based predictions completed successfully.");
        //=======================================================
    }
    static void predictUserBased() throws IOException, ClassNotFoundException {
        //Init multithreading
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool fork =  new ForkJoinPool(cores);

        //Init data and predictors.
        Database database = new Database();
        Predictor userBased = new Predictor(database.getMovieToUserMap(),database.getUserToMovieMap(),database.getUsersMean(), database.getMovieMean());
        userBased.setDatabase(database);
        userBased.setItemBased(false);
        System.out.println("Database is ready. Starting user-based predictions now.....");

        RatingList predRatings = new RatingList();
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();
//        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double predictionuser = userBased.predictRating(rating.getMovie().getIndex(), rating.getUser().getIndex());
                                rating.setRating(predictionuser);
                            })).get();
            finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submissions/userBasedPredictions.csv");
        System.out.println("User-based predictions completed successfully.");
        //=======================================================
    }
    static void predictHybridRating() throws IOException, ClassNotFoundException {
        //Init multithreading
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool fork =  new ForkJoinPool(cores);

        //Init data and predictors.
        Database database = new Database();
        Predictor itemBased = new Predictor(database.getUserToMovieMap(),database.getMovieToUserMap(),database.getMovieMean(), database.getUsersMean());
        itemBased.setDatabase(database);
        Predictor userBased = new Predictor(database.getMovieToUserMap(),database.getUserToMovieMap(),database.getUsersMean(), database.getMovieMean());
        userBased.setDatabase(database);
        userBased.setItemBased(false);
        System.out.println("Database is ready. Starting hybrid predictions now.....");

        RatingList predRatings = new RatingList();
        itemBased.setDatabase(database);
        predRatings.readFile("data/predictions.csv", database.getUserList(), database.getMovieList());
        RatingList finalList = new RatingList();
//        //==================MultiThreads===================
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {
                                double predictionitem = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
                                double predictionuser = userBased.predictRating(rating.getMovie().getIndex(), rating.getUser().getIndex());
                                double prediction = (0.75*predictionitem) + (0.25*predictionuser);
                                rating.setRating(prediction);
                            })).get();
            finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submissions/hybridPrediction.csv");
        //=======================================================
    }
}
