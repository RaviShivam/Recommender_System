//package NearestNeighbour;
//
//import Reader.*;
//
//import java.util.*;
//
///**
// * Created by ravishivam on 29-2-16.
// */
//public class NearestMoviePredictor {
//    private static final int K = 100;
//
//    private static int predict(HashMap<Integer, HashMap<Integer, Double>> ratings, UserList  nearestUsers, int movieId){
//        double mean = 0.0;
//        int total = 0;
//        List<Double> rates = new ArrayList<>();
//        for(User user: nearestUsers){
//            if(ratings.get(user.getIndex()).get(movieId) != null) {
//                rates.add(ratings.get(user.getIndex()).get(movieId));
//                total++;
//            }
//        }
////        return nearestUsers.stream().mapToDouble(user -> ratings.get(user.getIndex()).get(movieId)).sum();
//        for (Double r: rates)
//            mean += r;
//
//        int result = total==0 ? 0: (int)mean/total;
//        return result;
//    }
//
//
//    public static RatingList predictRatings(UserList userList, RatingList realRatings, RatingList predRatings) {
//        int rate;
//        UserList nearest;
//        HashMap<Integer, HashMap<Integer, Double>> hashedRatings = realRatings.getUserToMovieHashMap();
//        int i = 0;
//        for (Rating rating: predRatings) {
//            i++;
//            System.out.print("\r" + i*100/predRatings.size() + "%");
//            nearest = userList.getNeighbours(userList.get(rating.getUser().getIndex()-1),NearestMoviePredictor.K);
//            rate = predict(hashedRatings, nearest, rating.getMovie().getIndex());
//            rating.setRating(rate);
////            if(i == 10)
////                break;
//        }
//
//        // Return predictions
//        return predRatings;
//    }
//
//public  static  void main(String[] args){
//    Locale.setDefault(Locale.US);
//    // Create user list, movie list, and list of ratings
//    UserList userList = new UserList();
//    userList.readFile("data/users.csv");
//    MovieList movieList = new MovieList();
//    movieList.readFile("data/movies.csv");
//    RatingList ratings = new RatingList();
//    ratings.readFile("data/ratings.csv", userList, movieList);
//
//    // Read list of ratings we need to predict
//    RatingList predRatings = new RatingList();
//    predRatings.readFile("data/predictions.csv", userList, movieList);
//
////        System.out.println(ratings.getUserToMovieHashMap().get(1367).size());
////        System.out.println(userList.getNeighbours(userList.get(6040-1), 100));
//    NearestMoviePredictor.predictRatings(userList,ratings,predRatings).writeResultsFile("submission.csv");
//
//    }
//}
