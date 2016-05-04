package collaborative_filter;

import Reader.Rating;
import Reader.RatingList;
import latent_factor_model.Predictor_latentFactor;
import multi_layer_perceptron.FeatureVector;
import org.apache.commons.math3.linear.BlockRealMatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by ravishivam on 17-3-16.
 */
public class Main {
    static  long time = 0;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        startTimer();

//==================================
//        predictHybridRating();
//        predictItemBased();
//        predictUserBased();
//        ensembler();
//        overfit();
//        getPerceptronTrain();
//==================================

        endTimer();

    }

    static void ensembler() throws IOException, ClassNotFoundException {
        Database database = new Database();
        RatingList collabList = new RatingList();
        RatingList userbased = new RatingList();
        RatingList itembased = new RatingList();
        RatingList latenFactorModel = new RatingList();
        userbased.readPredictedFile("submissions/userBasedPredictions.csv", database.getUserList(),database.getMovieList());
        itembased.readPredictedFile("submissions/itemBasedPredictions.csv", database.getUserList(),database.getMovieList());
        latenFactorModel.readPredictedFile("submissions/latentFactorPredictions.csv", database.getUserList(), database.getMovieList());
        double c1 = 0.88;
        double c2 = 0.11;
        double c3 = 0.11;
        for (int i = 0; i < itembased.size(); i++) {
            double itemp = itembased.get(i).getRating();
            double userp = userbased.get(i).getRating();
            double latentp = latenFactorModel.get(i).getRating();
            double prediction = (c1 * itemp) + (c2 * userp) + (c3 * latentp);
            collabList.add(new Rating(itembased.get(i).getUser(), itembased.get(i).getMovie(), prediction));
        }
        collabList.writeResultsFile("submissions/ensembledPredictions.csv");
    }

    static void predictItemBased() throws IOException, ClassNotFoundException {
        //Init multithreading
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool fork =  new ForkJoinPool(cores);

        //Init data and predictors.
        Database database = new Database();
        Predictor itemBased = new Predictor(database.getUserToMovieMap(),database.getMovieToUserMap(),database.getMovieMean(), database.getUsersMean());
        System.out.println("Database is ready. Starting item-based predictions on " + cores + " cores now.....");

        RatingList predRatings = new RatingList();
        itemBased.setDatabase(database);
        itemBased.setK(15);
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
        finalList.writeResultsFile("submissions/itemBasedPredictions.csv");
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
        userBased.setK(16);
        System.out.println("Database is ready. Starting user-based predictions on " + cores + " cores now.....");

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
        itemBased.setK(15);
        userBased.setK(16);
        System.out.println("Database is ready. Starting hybrid predictions on " + cores + " cores now.....");

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
                                double prediction = ((0.8+0.043)*predictionitem) + ((0.2-0.043)*predictionuser);
                                rating.setRating(Computations.softround(prediction));
                            })).get();
            finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submissions/softRounded.csv");
        System.out.println("Hybrid prediction completed succesfully.");
        //=======================================================
    }
    static void getGradientTrainer() throws IOException, ClassNotFoundException {
        PrintWriter printer = new PrintWriter("data/perceptrontrain.csv");
        printer.println("item;user;latent;actual");
        Database database = new Database();
        RatingList ratinglist= new RatingList();
        ratinglist.readFile("data/ratings.csv", database.getUserList(), database.getMovieList());
        System.out.println(ratinglist.size());
        List<Rating> ratings = ratinglist.subList(500000, ratinglist.size()-1);
        System.out.println(ratings.size());

        Predictor itemBased = new Predictor(database.getUserToMovieMap(),database.getMovieToUserMap(),database.getMovieMean(), database.getUsersMean());
        itemBased.setDatabase(database);
        itemBased.setK(15);

        Predictor userBased = new Predictor(database.getMovieToUserMap(),database.getUserToMovieMap(),database.getUsersMean(), database.getMovieMean());
        userBased.setDatabase(database);
        userBased.setItemBased(false);
        userBased.setK(16);

        System.out.println("training latentFactor");
        Predictor_latentFactor latentFactor = new Predictor_latentFactor();
        latentFactor.trainLatenFactorModel();
        BlockRealMatrix blockRealMatrix = latentFactor.getP().multiply(latentFactor.getQ());

        System.out.println("Starting predictions now");
        for (int i = 0; i < ratings.size(); i++) {
            System.out.print("\r" + (i+1)*100/ratings.size() + "% completed");
            Rating rating = ratings.get(i);
            double ib = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
            double ub = userBased.predictRating(rating.getMovie().getIndex(), rating.getUser().getIndex());
            double lf = blockRealMatrix.getEntry(rating.getUser().getIndex()-1, rating.getMovie().getIndex()-1);
            printer.println(ib + ";" + ub + ";" + lf + ";" + rating.getRating());
        }
        System.out.println(
        );
        System.out.println("Done!");
        printer.close();
    }
    static void startTimer(){
        time = System.currentTimeMillis();
    }
    static void endTimer(){
        time = System.currentTimeMillis()-time;
        System.out.println();
        System.out.println("===============================");
        System.out.println("Code runtime= " + (time/1000.0)/60.0 + " minutes");
    }

    /**
     * Used to get data for MLP
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static void overfit() throws IOException, ClassNotFoundException {
        Database database = new Database();
        List<FeatureVector> trainingdata = new ArrayList<>();
        BufferedReader br = null;
        String line;
        br = new BufferedReader(new FileReader("data/perceptrontrain.csv"));
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] ratingData = line.split(";");
            int rat = (int) Double.parseDouble(ratingData[ratingData.length-1]);
            FeatureVector vector = new FeatureVector(rat);
            for (int i = 0; i < ratingData.length-1; i++) {
                vector.add(Double.parseDouble(ratingData[i]));
            }
            for (int i = 0; i < ratingData.length-1; i++) {
                vector.add(Double.parseDouble(ratingData[i]));
            }
            trainingdata.add(vector);
        }
        br.close();
        double learn = 0.0002;
        double w1 = Math.random();
        double w2 = Math.random();
        double w3 = Math.random();
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            System.out.print("\r" + (i+1)*100/runs + "% completed");
            for (int j = 0; j < trainingdata.size(); j++) {
                double arating = w1 * trainingdata.get(j).get(0)+ w2 * trainingdata.get(j).get(1) + w3 * trainingdata.get(j).get(2) ;
                double error = trainingdata.get(j).getLabel() - arating;
                w1 = w1 - (learn * (-2 * trainingdata.get(j).get(0)*error));
                w2 = w2 - (learn * (-2 * trainingdata.get(j).get(1) * error));
                w3 = w3 - (learn * (-2 * trainingdata.get(j).get(2) * error));
            }
        }
        System.out.println();
        System.out.println("first weight = " + w1);
        System.out.println("second weight = " + w2);
        System.out.println("third weight = " + w3);
    }
}
