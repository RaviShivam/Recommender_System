package mlp;

import ti2736c.*;

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

    public static List<FeatureVector> getTrainingData(){
        List<FeatureVector> featureVectorList = new ArrayList<>();
        RatingList ratingList = new RatingList();
        ratingList.readFile("data/ratings.csv",userlist,movieslist);
        for (int i = 0; i < ratingList.size(); i++) {
            User user = ratingList.get(i).getUser();
            Movie movie = ratingList.get(i).getMovie();
            FeatureVector vector = new FeatureVector(ratingList.get(i).getRating());
            vector.add((double) user.getAge());
            vector.add((double) user.getProfession());
            if(user.isMale()){
                vector.add(1.0);
            }
            else vector.add(0.0);
            vector.add((double) movie.getIndex());
            vector.add((double) movie.getYear());
            vector.add((double) movie.getTitle().hashCode());
            featureVectorList.add(vector);
        }
        return featureVectorList;
    }
    public static List<FeatureVector> getPredictionData(RatingList predictions) {
        List<FeatureVector> featureVectorList = new ArrayList<>();

        for (int i = 0; i < predictions.size(); i++) {
            User user = predictions.get(i).getUser();
            Movie movie = predictions.get(i).getMovie();
            FeatureVector vector = new FeatureVector(0);
            vector.add((double) user.getAge());
            vector.add((double) user.getProfession());
            if(user.isMale()){
                vector.add(1.0);
            }
            else vector.add(0.0);
            vector.add((double) movie.getIndex());
            vector.add((double) movie.getYear());
            vector.add((double) movie.getTitle().hashCode());
            featureVectorList.add(vector);
        }
        return featureVectorList;
    }

    public static MovieList getMovieslist() {
        return movieslist;
    }

    public static UserList getUserlist() {
        return userlist;
    }

}
