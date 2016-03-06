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
    int k = 200;

    public ItemBased(Database database) throws IOException, ClassNotFoundException {
        this.database = database;
    }

    public double predictRating(int user, int movie){
        Map<Integer, Double> moviesRatedByUser = database.getUserToMovieMap().get(user);
        double upper = 0.0;
        double lower = 0.0;
        if (!nearest.containsKey(movie)){
            TreeMap<Double, Integer> sims = new TreeMap<>(Collections.reverseOrder());
            ArrayList<Movie> movies  = database.getMovieList();
            for (int i = 0; i < movies.size(); i++) {
                sims.put(getCorrelationCoefficentPearson(movie, movies.get(i).getIndex()), movies.get(i).getIndex());
            }
            nearest.put(movie,sims);
        }
        TreeMap<Double,Integer> sims = nearest.get(movie);
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

//    public double getCorrelationCoefficentPearson(int activemovie, int secondmovie) {
//        //Retrieve intersection of users.
//        if(database.getMovieToUserMap().get(activemovie).isEmpty()||database.getMovieToUserMap().get(secondmovie).isEmpty()){
//            return 0.0;
//        }
//        HashMap<Integer, Double> activeMovieMap = database.getMovieToUserMap().get(activemovie);
//        HashMap<Integer, Double> secondMovieMap = database.getMovieToUserMap().get(secondmovie);
//        Set<Integer> userIntersection = new HashSet<Integer>(activeMovieMap.keySet());
//        userIntersection.retainAll(secondMovieMap.keySet());
//
//        //Init variables needed for calculations
//        double activeMovieMean = database.getMovieMean().get(activemovie);
//        double secondMovieMean = database.getMovieMean().get(secondmovie);
//        double upper = 0.0;
//        double loweractiveDif = 0.0;
//        double lowersecondDif = 0.0;
//        if (userIntersection.size() == 0) {
//            return 0.0;
//        }
//        //=======
//        for (Integer user: userIntersection) {
//            double difactive = database.getMovieToUserMap().get(activemovie).get(user) - activeMovieMean;
//            double difuseri = database.getMovieToUserMap().get(secondmovie).get(user) - secondMovieMean;
//            upper += (difactive) * (difuseri);
//            loweractiveDif += Math.pow(difactive, 2);
//            lowersecondDif += Math.pow(difuseri, 2);
//        }
//        if (loweractiveDif == 0 || lowersecondDif == 0) {
//            return 0.0;
//        }
//        double res = upper / (Math.sqrt(loweractiveDif) * Math.sqrt(lowersecondDif));
//        return res;
//    }

    public double getCorrelationCoefficentPearson(int active, int second){
        double firstMovieMean = database.getMovieMean().get(active);
        double secondMovieMean = database.getMovieMean().get(second);
        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        HashMap<Integer,Double> moviesRated1 = database.getMovieToUserMap().get(active);
        HashMap<Integer,Double> moviesRated2 = database.getMovieToUserMap().get(second);
        ArrayList<User> allusers = database.getUserList();

        for (int i = 0; i < allusers.size(); i++) {
           if(moviesRated1.containsKey(allusers.get(i).getIndex())) {
               list1.add(moviesRated1.get(allusers.get(i).getIndex()) - firstMovieMean);
           }
           else{
               list1.add(0.0);
           }
           if(moviesRated2.containsKey(allusers.get(i).getIndex())){
               list2.add(moviesRated2.get(allusers.get(i).getIndex()) - secondMovieMean);
           }
           else {
               list2.add(0.0);
           }

        }
        return computeCosine(list1,list2);
    }

    public double computeCosine(ArrayList<Double> list1, ArrayList<Double> list2){
        double sum = 0.0;
        double asq = 0.0;
        double bsq = 0.0;

        for (int i = 0; i < list1.size(); i++) {
            asq += Math.pow(list1.get(i),2);
            bsq += Math.pow(list2.get(i),2);
            sum += list1.get(i)*list2.get(i);
        }
        sum = sum/Math.sqrt(asq*bsq);
        return sum;
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
