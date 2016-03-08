package CollaborativeFiltering;

import Reader.UserList;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 3-3-16.
 */
public class UserBased {
    int k = 18;
    Database database;
    Map<Integer, TreeMap<Double, Integer>> similarities = new HashMap<>();

    public UserBased(Database database) throws IOException, ClassNotFoundException {
        this.database = database;
    }

    public double predictRating(int user, int movie){
        Map<Integer, Double> ratedByUsers = database.getMovieToUserMap().get(movie);
        double upper = 0.0;
        double lower = 0.0;
        TreeMap<Double, Integer> sims;
        if(similarities.containsKey(user)){
            sims = similarities.get(user);
        }
        else {
            sims = new TreeMap<>(Collections.reverseOrder());
            UserList users = database.getUserList();
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i).getIndex()==user){
                    continue;
                }
                double coeffient = getCorrelationCoefficentPearson(user, users.get(i).getIndex());
                if(coeffient>0){
                    sims.put(coeffient, users.get(i).getIndex());
                }
            }
            similarities.put(user, sims);
        }
        int reached = 0;
        for (Map.Entry<Double, Integer> entry : sims.entrySet()) {
            if(reached==k){
                break;
            }
            if(!ratedByUsers.containsKey(entry.getValue())){
                continue;
            }
            upper += entry.getKey() * (ratedByUsers.get(entry.getValue())-database.getBaseLine(entry.getValue(),movie));
            lower += entry.getKey();
            reached++;
        }
        if(database.getUserToMovieMap().get(user).size()==0){
            return database.getMovieMean().get(movie);
        }
        if(database.getMovieToUserMap().get(movie).size()==0){
            return database.getUsersMean().get(user);
        }
        if(lower==0.0||upper==0.0){
            return database.getUsersMean().get(user);
        }
        double prediction =database.getBaseLine(user,movie) + (upper/lower);
        if(prediction>5.0){
            return 5.0;
        }
        if(prediction<1.0){
            return 1.0;
        }
        return prediction;
    }

    public double getCorrelationCoefficentPearson(int active, int second){
        double firstUserMean = database.getUsersMean().get(active);
        double secondUserMean = database.getUsersMean().get(second);
        HashMap<Integer,Double> moviesRated1 = (HashMap<Integer, Double>) database.getUserToMovieMap().get(active).clone();
        HashMap<Integer,Double> moviesRated2 = (HashMap<Integer, Double>) database.getUserToMovieMap().get(second).clone();
        Set<Integer> movieInterSection = new HashSet<Integer>(moviesRated1.keySet());
        movieInterSection.retainAll(moviesRated2.keySet());

        if(moviesRated1.size()==0||moviesRated2.size()==0){
            return -1;
        }
        if(movieInterSection.isEmpty()){
            return 0.0;
        }
        double sum = 0.0;
        double asq = 0.0;
        double bsq = 0.0;
        for (Double d:moviesRated1.values()) {
            asq += Math.pow(d-firstUserMean,2);
        }
        for (Double d : moviesRated2.values()) {
            bsq += Math.pow(d-secondUserMean,2);
        }
        for (Integer similairMovie : movieInterSection) {
            double diff1 = moviesRated1.get(similairMovie) - firstUserMean;
            double diff2 = moviesRated2.get(similairMovie) - secondUserMean;
            sum+= diff1*diff2;
        }
        if(sum==0||asq==0||bsq==0){
            return 0;
        }
        return sum/Math.sqrt(asq*bsq);
    }





















    //                  Code Graveyard
    //********************************************************
    //********************************************************
    //********************************************************

    public double predictDualRating(int user, int movie) {
        double pearsonAdder = 0.0;
        double cosineAdder = 0.0;
        int normalizer = 0;
        for (Integer seconduser : database.getUserToMovieMap().keySet()) {
            if (database.getUserToMovieMap().get(seconduser).containsKey(movie)) {
                double pearsoncorr = getCorrelationCoefficientCosine(user, seconduser);
                double cosinecorr = getCorrelationCoefficentPearson(user, seconduser);
                double mult = (database.getUserToMovieMap().get(seconduser).get(movie) - database.getUsersMean().get(seconduser));
                pearsonAdder += pearsoncorr * mult;
                cosineAdder += cosinecorr * mult;
                normalizer++;
            }
        }
        if (normalizer == 0) {
            return database.getUsersMean().get(user);
        }
        double pearsonPrediction = database.getUsersMean().get(user) + (pearsonAdder / normalizer);
        double cosinePrediction = database.getUsersMean().get(user) + (cosineAdder / normalizer);
        double avg = (pearsonPrediction + cosinePrediction) / 2;
        return avg;
    }

    public double getCorrelationCoefficientCosine(int activeuser, int useri) {
        HashMap<Integer, Double> activeUserMap = database.getUserToMovieMap().get(activeuser);
        HashMap<Integer, Double> secondUserMap = database.getUserToMovieMap().get(useri);
        Set<Integer> movieIntersection = new HashSet<Integer>(activeUserMap.keySet());
        movieIntersection.retainAll(secondUserMap.keySet());
        if (activeUserMap.size() == 0 || secondUserMap.size() == 0 || movieIntersection.size() == 0) {
            return 0.0;
        }
        double loweractiveRoot = 0.0;
        double lowersecondRoot = 0.0;
        for (Double num : activeUserMap.values()) {
            loweractiveRoot += Math.pow(num, 2);
        }
        loweractiveRoot = Math.sqrt(loweractiveRoot);

        for (Double num : secondUserMap.values()) {
            lowersecondRoot += Math.pow(num, 2);
        }
        lowersecondRoot = Math.sqrt(lowersecondRoot);
        double product = loweractiveRoot * lowersecondRoot;
        if (product == 0) {
            return 0.0;
        }
        double res = 0.0;
        for (Integer movie : movieIntersection) {
            res += (database.getUserToMovieMap().get(activeuser).get(movie) * database.getUserToMovieMap().get(useri).get(movie)) / product;
        }
        return res;
    }
}

