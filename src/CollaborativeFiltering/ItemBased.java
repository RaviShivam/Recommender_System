package CollaborativeFiltering;

import Reader.MovieList;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 5-3-16.
 */
public class ItemBased {
    int k = 18;//20;//13;//25;//50;
    Database database;
    Map<Integer, TreeMap<Double, Integer>> similarities = new HashMap<>();

    public ItemBased(Database database) throws IOException, ClassNotFoundException {
        this.database = database;
    }

    public double predictRating(int user, int movie){
        Map<Integer, Double> moviesRatedByUser = database.getUserToMovieMap().get(user);
        double upper = 0.0;
        double lower = 0.0;
        TreeMap<Double, Integer> sims;
        if(similarities.containsKey(movie)){
           sims = similarities.get(movie);
        }
        else {
            sims = new TreeMap<>(Collections.reverseOrder());
            MovieList movies = database.getMovieList();
            for (int i = 0; i < movies.size(); i++) {
                if(movies.get(i).getIndex()==movie){
                    continue;
                }
                double coeffient = getCorrelationCoefficentPearson(movie, movies.get(i).getIndex());

                if(coeffient>0){
                    sims.put(coeffient, movies.get(i).getIndex());
                }
            }
            similarities.put(movie, sims);
        }
        int reached = 0;
        for (Map.Entry<Double, Integer> entry : sims.entrySet()) {
            if(reached==k){
                break;
            }
            if(!moviesRatedByUser.containsKey(entry.getValue())){
                continue;
            }
            upper += entry.getKey() * (moviesRatedByUser.get(entry.getValue())-database.getBaseLine(user,entry.getValue()));
            lower += entry.getKey();
            reached++;
        }
        if(database.getUserToMovieMap().get(user).size()==0){
            return database.getMovieMean().get(movie);
        }
        if(database.getMovieToUserMap().get(movie).size()==0){
            return database.getUsersMean().get(user);
        }
        double prediction;
        if(lower==0){
            prediction = database.getUsersMean().get(user);
        }
         else {
            prediction = database.getBaseLine(user,movie) + (upper/lower);
        }
        if(prediction>5.0){
            return 5.0;
        }
        if(prediction<1.0){
            return 1.0;
        }
        return prediction;
    }

    public double getCorrelationCoefficentPearson(int active, int second){
        double firstMovieMean = database.getMovieMean().get(active);
        double secondMovieMean = database.getMovieMean().get(second);
        HashMap<Integer,Double> moviesRated1 = (HashMap<Integer, Double>) database.getMovieToUserMap().get(active).clone();
        HashMap<Integer,Double> moviesRated2 = (HashMap<Integer, Double>) database.getMovieToUserMap().get(second).clone();
        Set<Integer> userIntersection = new HashSet<Integer>(moviesRated1.keySet());
        userIntersection.retainAll(moviesRated2.keySet());
        if(moviesRated1.size()==0||moviesRated2.size()==0){
            return -1;
        }
        if(userIntersection.isEmpty()){
            return 0.0;
        }
        double sum = 0.0;
        double asq = 0.0;
        double bsq = 0.0;
        for (Double d:moviesRated1.values()) {
            asq += Math.pow(d-firstMovieMean,2);
        }
        for (Double d : moviesRated2.values()) {
            bsq += Math.pow(d-secondMovieMean,2);
        }
        for (Integer similairuser : userIntersection) {
            double diff1;
            double diff2;
            diff1 = moviesRated1.get(similairuser) - firstMovieMean;
            diff2 = moviesRated2.get(similairuser) - secondMovieMean;
            sum+= diff1*diff2;
        }
        if(sum==0||asq==0||bsq==0){
            return 0;
        }
        return sum/Math.sqrt(asq*bsq);
    }

    //================================================================================================
    //================================================================================================
    //================================================================================================
    //================================================================================================
    //================================================================================================
    //================================================================================================

    public double naivePredictRating(int user, int movie){
        Map<Integer,HashMap<Integer, Double>> rate = database.getMovieToUserMap();
        if(!rate.containsKey(movie)){
            return database.getUsersMean().get(user);
        }
        double sum = 0;
        for (Double d :
                rate.get(movie).values()) {
            sum += d;
        }
        return Computations.round(sum/(rate.get(movie).size()));
    }

}
