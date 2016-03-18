package CollaborativeFiltering;

import Reader.MovieList;
import Reader.RatingList;
import Reader.UserList;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 5-3-16.
 */
public class Database {
    double meanRating = 0;
    UserList userList = new UserList();
    MovieList movieList = new MovieList();
    RatingList ratingList = new RatingList();
    Map<Integer, Double> usersMean = new HashMap<>();
    Map<Integer, Double> movieMean = new HashMap<>();
    Map<Integer, Map<Integer,Double>> userToMovieBaseline = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> userToMovieMap = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> movieToUserMap = new HashMap<>();

    public Map<Integer, Map<Integer, Double>> getBaselineEstimate() {
        return userToMovieBaseline;
    }

    public Database() throws IOException, ClassNotFoundException {
        userList.readFile("data/users.csv");
        movieList.readFile("data/movies.csv");
        ratingList.readFile("data/ratings.csv", userList, movieList);
        meanRating = initMeanRating();
        userToMovieMap = ratingList.getUserToMovieHashMap();
        movieToUserMap = ratingList.getMovieToUserHashMap();
        initAllUsersMeanVote();
        initAllMovieMeanVote();
        initBaseLineEstimates();
    }

    public double initMeanRating(){
        double sum = 0.0;
        for (int i = 0; i < ratingList.size(); i++) {
            sum += ratingList.get(i).getRating();
        }
        return sum/ratingList.size();
    }

    public double getUserMeanVote(int user){
        //get sum of all ratings of user i.
        if(userToMovieMap.get(user).isEmpty()){
            return  0.0;
        }
        double ratingsum = (new ArrayList<Double>(userToMovieMap.get(user).values())).stream().mapToDouble(Double::doubleValue).sum();
        return ratingsum/ userToMovieMap.get(user).size();
    }

    public void initAllUsersMeanVote(){
        for (int i = 0; i < userList.size(); i++) {
            int user = userList.get(i).getIndex();
            usersMean.put(user, getUserMeanVote(user));
        }
    }

    public double getMovieMeanVote(int movie){
        //get sum of all ratings of movie.
        if(movieToUserMap.get(movie).isEmpty()){
            return 0.0;
        }
        double ra = (new ArrayList<Double>(movieToUserMap.get(movie).values())).stream().mapToDouble(Double::doubleValue).sum()/ movieToUserMap.get(movie).size();
        return ra;
    }

    public void initAllMovieMeanVote(){
        for (int i = 0; i < movieList.size(); i++) {
            int movie = movieList.get(i).getIndex();
            movieMean.put(movie, getMovieMeanVote(movie));
        }
    }

    public void initBaseLineEstimates(){
        for (int i = 0; i < userList.size(); i++) {
            Map<Integer, Double> bucket = new HashMap<>();
            userToMovieBaseline.put(userList.get(i).getIndex(), bucket);
        }
    }

    public double getBaseLine(int user, int movie){
        if(userToMovieBaseline.get(user).containsKey(movie)){
            return userToMovieBaseline.get(user).get(movie);
        }
        else {
            double bx = usersMean.get(user) - meanRating;
            double bi = movieMean.get(movie) - meanRating;
            double bias = bx + bi + meanRating;
            userToMovieBaseline.get(user).put(movie, bias);
            return bias;
        }
    }

//    public double getBaseLine(int user, int movie){
//        if(userToMovieBaseline.get(user).containsKey(movie)){
//            return userToMovieBaseline.get(user).get(movie);
//        }
//        else {
//            double sumbx = 0;
//            double sumbi = 0;
//            for (Double d :
//                    movieToUserMap.get(movie).values()) {
//                sumbi += d-meanRating;
//            }
//            double bi = sumbi/(25 + movieToUserMap.get(movie).size());
//
//            for (Double d :
//                    userToMovieMap.get(user).values()) {
//                sumbx += d-meanRating-bi;
//            }
//            double bx = sumbx/(10+userToMovieMap.get(user).size());
//            double bias = bx + bi + meanRating;
//            userToMovieBaseline.get(user).put(movie, bias);
//            return bias;
//        }
//    }
                                        //Getter methods.
    //**********************************************************************************************
    //**********************************************************************************************
    public double getMeanRating() {
        return meanRating;
    }

    public Map<Integer, HashMap<Integer, Double>> getMovieToUserMap() {
        return movieToUserMap;
    }

    public Map<Integer, HashMap<Integer, Double>> getUserToMovieMap() {
        return userToMovieMap;
    }

    public Map<Integer, Double> getUsersMean() {
        return usersMean;
    }

    public RatingList getRatingList() {
        return ratingList;
    }

    public MovieList getMovieList() {
        return movieList;
    }

    public Map<Integer, Double> getMovieMean() {
        return movieMean;
    }

    public UserList getUserList() {
        return userList;
    }
}

