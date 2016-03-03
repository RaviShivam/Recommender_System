package CollaborativeFiltering;

import Reader.MovieList;
import Reader.RatingList;
import Reader.UserList;

import java.util.*;

/**
 * Created by ravishivam on 3-3-16.
 */
public class UserBasedCollab {
    UserList userList = new UserList();
    MovieList movieList = new MovieList();
    RatingList ratingList = new RatingList();
    Map<Integer, Double> usersMean = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> userToMovieMap = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> movieToUserMap = new HashMap<>();

    public void initData(){
        userList.readFile("data/users.csv");
        movieList.readFile("data/movies.csv");
        ratingList.readFile("data/ratings.csv", userList, movieList);
        userToMovieMap = ratingList.getUserToMovieHashMap();
        getAllUserMean();
    }

    public double getMeanVote(int user){
        //get sum of all ratings of user i.
        double ratingsum = (new ArrayList<Double>(userToMovieMap.get(user).values())).stream().mapToDouble(Double::doubleValue).sum();
        return ratingsum/ userToMovieMap.get(user).size();
    }
    public void getAllUserMean(){
        for (int i = 0; i < userList.size(); i++) {
            int user = userList.get(i).getIndex();
            usersMean.put(user, getMeanVote(user));
        }
    }
    public double getCorrelationCoefficentPearson(int activeuser, int useri){
        HashMap<Integer, Double> activeUserMap = userToMovieMap.get(activeuser);
        HashMap<Integer, Double> secondUserMap = userToMovieMap.get(useri);
        Set<Integer> movieIntersection = new HashSet<Integer>(activeUserMap.keySet());
        movieIntersection.retainAll(secondUserMap.keySet());
        double activeUserMean = usersMean.get(activeuser);//getMeanVote(activeuser);
        double secondUserMean = usersMean.get(useri);//getMeanVote(useri);
        double upper = 0.0;
        double loweractiveDif = 0.0;
        double lowersecondDif = 0.0;
        if(movieIntersection.size()==0){
            return 0.0;
        }
        for (Integer movie: movieIntersection) {
            double difactive =  userToMovieMap.get(activeuser).get(movie) - activeUserMean;
            double difuseri =   userToMovieMap.get(useri).get(movie) - secondUserMean;
            upper += (difactive) * (difuseri);
            loweractiveDif += Math.pow(difactive,2);
            lowersecondDif += Math.pow(difuseri,2);
        }
        if(loweractiveDif==0||lowersecondDif==0){
            return 0;
        }
        double res = upper/Math.sqrt(loweractiveDif*lowersecondDif);
        return res;
    }

    public double getCorrelationCoefficientCosine(int activeuser, int useri){
        HashMap<Integer, Double> activeUserMap = userToMovieMap.get(activeuser);
        HashMap<Integer, Double> secondUserMap = userToMovieMap.get(useri);
        Set<Integer> movieIntersection = new HashSet<Integer>(activeUserMap.keySet());
        movieIntersection.retainAll(secondUserMap.keySet());
        if(activeUserMap.size()==0||secondUserMap.size()==0||movieIntersection.size()==0){
            return 0.0;
        }
        double loweractiveRoot = 0.0;
        double lowersecondRoot = 0.0;
        for (Double num : activeUserMap.values()){
            loweractiveRoot += Math.pow(num,2);
        }
        loweractiveRoot = Math.sqrt(loweractiveRoot);

        for (Double num : secondUserMap.values()){
            lowersecondRoot += Math.pow(num,2);
        }
        lowersecondRoot = Math.sqrt(lowersecondRoot);
        double product = loweractiveRoot*lowersecondRoot;
        if (product == 0) {
            return 0.0;
        }
        double res = 0.0;
        for (Integer movie: movieIntersection) {
            res += (userToMovieMap.get(activeuser).get(movie)*userToMovieMap.get(useri).get(movie))/product;
        }
        return res;
    }

    public double predictRating(int user, int movie){
        double adder = 0.0;
        int normalizer = 0;
        for (Integer seconduser : userToMovieMap.keySet()) {
            if (userToMovieMap.get(seconduser).containsKey(movie)) {
                double corr = getCorrelationCoefficientCosine(user, seconduser);
                double mult = (userToMovieMap.get(seconduser).get(movie) - usersMean.get(seconduser));
                adder += corr * mult;
                normalizer++;
            }
        }
        if(normalizer==0){
            return usersMean.get(user);
        }
        double prediction = usersMean.get(user) + (adder/normalizer);
        return prediction;
    }

    public double predictDualRating(int user, int movie){
        double pearsonAdder = 0.0;
        double cosineAdder = 0.0;
        int normalizer = 0;
        for (Integer seconduser : userToMovieMap.keySet()) {
            if (userToMovieMap.get(seconduser).containsKey(movie)) {
                double pearsoncorr = getCorrelationCoefficientCosine(user, seconduser);
                double cosinecorr = getCorrelationCoefficentPearson(user,seconduser);
                double mult = (userToMovieMap.get(seconduser).get(movie) - usersMean.get(seconduser));
                pearsonAdder += pearsoncorr * mult;
                cosineAdder += cosinecorr*mult;
                normalizer++;
            }
        }
        if(normalizer==0){
            return usersMean.get(user);
        }
        double pearsonPrediction = usersMean.get(user) + (pearsonAdder/normalizer);
        double cosinePrediction = usersMean.get(user) + (cosineAdder/normalizer);
        double avg = (pearsonPrediction+cosinePrediction)/2;
        return avg;
    }

}
