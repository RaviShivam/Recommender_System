package mlp;

import Reader.*;

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
        double f = 0;
        double s = 0;
        double t = 0;
        double fo = 0;
        double fi = 0;
        for (int i = 0; i < ratingList.size(); i++) {
            User user = ratingList.get(i).getUser();
            Movie movie = ratingList.get(i).getMovie();
            List<Double> outputList = new ArrayList<>();
            double rating = ratingList.get(i).getRating();
            for (int j = 1; j < 6; j++) {
                if(rating == j){
                    outputList.add(1.0);
                }
                else{
                    outputList.add(0.0);
                }
            }
            FeatureVector vector = new FeatureVector(outputList);



            vector.add(((double) movie.getIndex()+1250)/(movieslist.size()*10));
            s += ((double) movie.getIndex()+1250)/(movieslist.size()*10);

            vector.add(((double) user.getAge()+51)/100.0);
            t += ((double) user.getAge()+51)/100.0;

//            vector.add(1 - ((double)movie.getYear()-7)/2016.0);
//            f += (1 - ((double)movie.getYear()-7)/2016.0);

            vector.add((double)user.getProfession()/100.0);
            fo += (double)user.getProfession()/100.0;

            if(user.isMale()){
                vector.add(1.1);
                fi += 1.1;
            }
            else vector.add(0.0);

//            vector.add((double) movie.getTitle().hashCode()/(Math.pow(10,10)));
            featureVectorList.add(vector);
        }
        System.out.println(f + "    " + s + "    " + t + "    "+fo + "    " + fi);
        return featureVectorList;
    }
    public static List<FeatureVector> getPredictionData(RatingList predictions) {
        List<FeatureVector> featureVectorList = new ArrayList<>();
        for (int i = 0; i < predictions.size(); i++) {
            User user = predictions.get(i).getUser();
            Movie movie = predictions.get(i).getMovie();

            FeatureVector vector = new FeatureVector(null);
            vector.add(((double) movie.getIndex()+1250)/(movieslist.size()*10));

            vector.add(((double) user.getAge()+51)/100.0);

//            vector.add(1 - ((double)movie.getYear()-7)/2016.0);

            vector.add((double)user.getProfession()/100.0);

            if(user.isMale()){
                vector.add(1.1);
            }
            else vector.add(0.0);

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
