package CollaborativeFiltering;

import Reader.User;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 3-3-16.
 */
public class UserBased {
    Database database;

    public UserBased() throws IOException, ClassNotFoundException {
        database = new Database();
    }

    public double getCorrelationCoefficentPearson(int activeuser, int useri) {
        HashMap<Integer, Double> activeUserMap = database.getUserToMovieMap().get(activeuser);
        HashMap<Integer, Double> secondUserMap = database.getUserToMovieMap().get(useri);
        Set<Integer> movieIntersection = new HashSet<Integer>(activeUserMap.keySet());
        movieIntersection.retainAll(secondUserMap.keySet());
        double activeUserMean = database.getUsersMean().get(activeuser);//getMeanVote(activeuser);
        double secondUserMean = database.getUsersMean().get(useri);//getMeanVote(useri);
        double upper = 0.0;
        double loweractiveDif = 0.0;
        double lowersecondDif = 0.0;
        if (movieIntersection.size() == 0) {
            return 0.0;
        }
        for (Integer movie : movieIntersection) {
            double difactive = database.getUserToMovieMap().get(activeuser).get(movie) - activeUserMean;
            double difuseri = database.getUserToMovieMap().get(useri).get(movie) - secondUserMean;
            upper += (difactive) * (difuseri);
            loweractiveDif += Math.pow(difactive, 2);
            lowersecondDif += Math.pow(difuseri, 2);
        }
        if (loweractiveDif == 0 || lowersecondDif == 0) {
            return 0;
        }
        double res = upper / Math.sqrt(loweractiveDif * lowersecondDif);
        return res;
    }


    public double predictRating(int user, int movie) {
        double adder = 0.0;
        int normalizer = 0;
        for (User userer : database.getUserList()) {
            int seconduser = userer.getIndex();
            if (database.getUserToMovieMap().get(seconduser).containsKey(movie)) {
                double corr = getCorrelationCoefficentPearson(user, seconduser);
                double mult = (database.getUserToMovieMap().get(seconduser).get(movie) - database.getUsersMean().get(seconduser));
                adder += corr * mult;
                normalizer++;
            }
        }
        if (normalizer == 0) {
            return database.getUsersMean().get(user);
        }
        double prediction = database.getUsersMean().get(user) + (adder / normalizer);
        return Computations.round(prediction);
    }

    public Database getDatabase() {
        return database;
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

