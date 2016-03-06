package CollaborativeFiltering;

import Reader.Movie;
import Reader.User;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 5-3-16.
 */
public class ItemBased {
    Database database;
    Map<Integer,TreeMap<Double,Integer>> nearest = new HashMap<>();
    int k = 2;

    public ItemBased(Database database) throws IOException, ClassNotFoundException {
        this.database = database;
    }

    public double predictRating(int user, int movie){
        Map<Integer, Double> moviesRatedByUser = database.getUserToMovieMap().get(user);
        double upper = 0.0;
        double lower = 0.0;
        TreeMap<Double, Integer> sims = new TreeMap<>(Collections.reverseOrder());
        ArrayList<Movie> movies  = database.getMovieList();
        for (int i = 0; i < movies.size(); i++) {
            sims.put(getCorrelationCoefficentPearson(movie, movies.get(i).getIndex()), movies.get(i).getIndex());
        }
        nearest.put(movie,sims);
        int reached = 0;
        for (Map.Entry<Double, Integer> entry : sims.entrySet()) {
            if(reached == k){
                break;
            }
            if(!moviesRatedByUser.containsKey(entry.getValue())){
                continue;
            }
            upper += entry.getKey() * moviesRatedByUser.get(entry.getValue());
            lower += entry.getKey();
            reached++;
        }
        if(lower==0.0||upper==0.0){
            return database.getUsersMean().get(user);
        }
        return upper/lower;
    }

    public double getCorrelationCoefficentPearson(int active, int second){
        double firstMovieMean = database.getMovieMean().get(active);
        double secondMovieMean = database.getMovieMean().get(second);
        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        HashMap<Integer,Double> moviesRated1 = database.getMovieToUserMap().get(active);
        HashMap<Integer,Double> moviesRated2 = database.getMovieToUserMap().get(second);
        Set<Integer> userIntersection = new HashSet<Integer>(moviesRated1.keySet());
        userIntersection.retainAll(moviesRated2.keySet());
        if(userIntersection.isEmpty()){
            return 0.0;
        }
        ArrayList<Integer> allusers = new ArrayList<>(userIntersection);
        double sum = 0.0;
        double asq = 0.0;
        double bsq = 0.0;
        for (Double d :
                moviesRated1.values()) {
            asq += Math.pow(d-firstMovieMean,2);
        }
        for (Double d :
                moviesRated2.values()) {
            bsq += Math.pow(d-secondMovieMean,2);
        }
        for (int i = 0; i < allusers.size(); i++) {
            double diff1;
            double diff2;
            diff1 = moviesRated1.get(allusers.get(i)) - firstMovieMean;
            diff2 = moviesRated2.get(allusers.get(i)) - secondMovieMean;
            sum+= diff1*diff2;
        }
        if(sum==0||asq==0||bsq==0){
            return 0;
        }

        return sum/Math.sqrt(asq*bsq) ;
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
