package multi_layer_perceptron;

import Reader.*;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ravishivam on 28-2-16.
 */
public class Input {

    static Map<Integer, Movie> moviemap = new HashMap<>();
    static Map<Integer, User> usersmap = new HashMap<>();
    static Map<Integer, Rating> rating = new HashMap<>();

    static MovieList movieslist  = new MovieList();
    static UserList userlist    = new UserList();

    public static void InitInput(){
        movieslist.readFile("data/movies.csv");
        for (int i = 0; i <movieslist.size(); i++) {
            moviemap.put(i,movieslist.get(i));
        }
        userlist.readFile("data/users.csv");
        for (int i = 0; i <userlist.size(); i++) {
            usersmap.put(i,userlist.get(i));
        }
    }

    public static List<FeatureVector> getTrainingData() throws IOException {
        List<FeatureVector> trainingdata = new ArrayList<>();
    BufferedReader br = null;
    String line;
        br = new BufferedReader(new FileReader("data/perceptrontrain.csv"));
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] ratingData = line.split(";");
            List<Double> actualrating = new ArrayList<>();
            int rat = (int) Double.parseDouble(ratingData[ratingData.length-1]);
            for (int i = 0; i < 5; i++) {
                if(rat==i+1){
                    actualrating.add(1.0);
                }
                else actualrating.add(0.0);
            }
//            FeatureVector vector = new FeatureVector(actualrating);
            FeatureVector vector = new FeatureVector(2.0);
            for (int i = 0; i < ratingData.length-1; i++) {
                vector.add(Double.parseDouble(ratingData[i]));
            }
            for (int i = 0; i < ratingData.length-1; i++) {
                vector.add(Double.parseDouble(ratingData[i]));
            }
            trainingdata.add(vector);
        }
        br.close();
        return trainingdata;
    }
    public static List<FeatureVector> getPredictionData() {
        RatingList userbased = new RatingList();
        RatingList itembased = new RatingList();
        RatingList latenFactorModel = new RatingList();
        userbased.readPredictedFile("submissions/userBasedPredictions.csv", userlist,movieslist);
        itembased.readPredictedFile("submissions/itemBasedPredictions.csv", userlist,movieslist);
        latenFactorModel.readPredictedFile("submissions/latentFactorPredictionsSecond.csv", userlist, movieslist);
        List<FeatureVector> predictionData = new ArrayList<>();
        for (int i = 0; i < userbased.size(); i++) {
            FeatureVector vector = new FeatureVector(0);
            vector.add(itembased.get(i).getRating());
            vector.add(userbased.get(i).getRating());
            vector.add(latenFactorModel.get(i).getRating());
            vector.add(itembased.get(i).getRating());
            vector.add(userbased.get(i).getRating());
            vector.add(latenFactorModel.get(i).getRating());
            predictionData.add(vector);
        }
        return predictionData;
    }

    public static MovieList getMovieslist() {
        return movieslist;
    }

    public static UserList getUserlist() {
        return userlist;
    }

}
