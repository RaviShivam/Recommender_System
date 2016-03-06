package CollaborativeFiltering;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ravishivam on 5-3-16.
 */
public class ItemBased {
    Database database;

    public ItemBased() throws IOException, ClassNotFoundException {
        database = new Database();
    }

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

    public double predictRating(int user, int movie){
        Map<Integer, Double> moviesRatedByUser = database.getUserToMovieMap().get(user);
        double upper = 0.0;
        double lower = 0.0;
        for (Integer secondMovie :
                moviesRatedByUser.keySet()) {
            double simij = getCorrelationCoefficentPearson(movie, secondMovie);
            double upperprod = (moviesRatedByUser.get(secondMovie)-database.getBaseLine(user,secondMovie)) *simij;
            upper +=upperprod;
            lower += simij;
        }
        if(lower==0.0||upper==0.0){
            return database.getUsersMean().get(user);
        }
        return database.getBaseLine(user, movie)-(upper/lower);
    }

    public double getCorrelationCoefficentPearson(int activemovie, int secondmovie) {
        //Retrieve intersection of users.
        if(database.getMovieToUserMap().get(activemovie).isEmpty()||database.getMovieToUserMap().get(secondmovie).isEmpty()){
            return 0.0;
        }
        HashMap<Integer, Double> activeMovieMap = database.getMovieToUserMap().get(activemovie);
        HashMap<Integer, Double> secondMovieMap = database.getMovieToUserMap().get(secondmovie);
        Set<Integer> userIntersection = new HashSet<Integer>(activeMovieMap.keySet());
        userIntersection.retainAll(secondMovieMap.keySet());

        //Init variables needed for calculations
        double activeMovieMean = database.getMovieMean().get(activemovie);
        double secondMovieMean = database.getMovieMean().get(secondmovie);
        double upper = 0.0;
        double loweractiveDif = 0.0;
        double lowersecondDif = 0.0;
        if (userIntersection.size() == 0) {
            return 0.0;
        }
        //=======
        for (Integer user: userIntersection) {
            double difactive = database.getMovieToUserMap().get(activemovie).get(user) - activeMovieMean;
            double difuseri = database.getMovieToUserMap().get(secondmovie).get(user) - secondMovieMean;
            upper += (difactive) * (difuseri);
            loweractiveDif += Math.pow(difactive, 2);
            lowersecondDif += Math.pow(difuseri, 2);
        }
        if (loweractiveDif == 0 || lowersecondDif == 0) {
            return 0.0;
        }
        double res = upper / Math.sqrt(loweractiveDif * lowersecondDif);
        return res;
    }
}
