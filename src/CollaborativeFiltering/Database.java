package CollaborativeFiltering;

import Reader.MovieList;
import Reader.RatingList;
import Reader.User;
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
    Map<Integer, Map<Integer,Double>> baselineEstimate = new HashMap<>();
    Map<Integer, ArrayList<Integer>> KNearestNeighbours = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> userToMovieMap = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> movieToUserMap = new HashMap<>();

    public Map<Integer, Map<Integer, Double>> getBaselineEstimate() {
        return baselineEstimate;
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


//        KNearestNeighbours = (HashMap<Integer, ArrayList<Integer>>) Computations.getSerializedItem("objects/NearestNeighbours.ser");
//        initKNearestNeighbours(300);
//        Computations.serializeItem(KNearestNeighbours, "objects/NearestNeighbours.ser");
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
            baselineEstimate.put(userList.get(i).getIndex(), bucket);
        }
    }

    public double getBaseLine(int user, int movie){
        if(baselineEstimate.get(user).containsKey(movie)){
            return baselineEstimate.get(user).get(movie);
        }
        else {
            double bx = usersMean.get(user) - meanRating;
            double bi = movieMean.get(movie) - meanRating;
            double bias = bx + bi + meanRating;
            baselineEstimate.get(user).put(movie, bias);
            return bias;
        }
    }

//    public double getBaseLine(int user, int movie){
//        if(baselineEstimate.get(user).containsKey(movie)){
//            return baselineEstimate.get(user).get(movie);
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
//            baselineEstimate.get(user).put(movie, bias);
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

    public Map<Integer, ArrayList<Integer>> getKNearestNeighbours() {
        return KNearestNeighbours;
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
    public ArrayList<Integer> getKNearestNeighbours(int user, int k) {
        Map<Double, Integer> secondUsers = new TreeMap<>();
        UserList allUserClone = (UserList) userList.clone();
        User curr = allUserClone.remove(user);
        for (User second : allUserClone) {
            HashMap<Integer, Double> currUserMap = userToMovieMap.get(curr.getIndex());
            HashMap<Integer, Double> secondUserMap = userToMovieMap.get(second.getIndex());
            Set<Integer> movieIntersection = new HashSet<Integer>(currUserMap.keySet());
            movieIntersection.retainAll(secondUserMap.keySet());
            Set<Integer> movieUnion = new HashSet<>(currUserMap.keySet());
            movieUnion.addAll(secondUserMap.keySet());
            double correlation = 0.0;
            if (Math.abs(curr.getAge() - second.getAge()) < 3) {
                correlation++;
            }
            if ((curr.isMale() && second.isMale()) || (!curr.isMale() && !second.isMale())) {
                correlation++;
            }
            if (curr.getProfession() == second.getProfession()) {
                correlation += 2;
            }
            correlation += movieIntersection.size();
            correlation = correlation / (movieUnion.size() + 3);
            secondUsers.put(correlation, second.getIndex());
        }
        List<Integer> nearestNeighboursList = new ArrayList<>(secondUsers.values());
        ArrayList<Integer> knearest = new ArrayList<>();
        if (nearestNeighboursList.size() < k) {
            k = nearestNeighboursList.size();
        }
        for (int i = 0; i < k; i++) {
            knearest.add(nearestNeighboursList.get(i));
        }
        return knearest;
    }

    public void initKNearestNeighbours(int k) {
//        int cores = 4;
//        System.out.println(cores);
//        ForkJoinPool fork =  new ForkJoinPool(cores);
//        List<User> parallelList = Collections.synchronizedList(userList);
//        try {
//            fork.submit(
//                    () -> parallelList.parallelStream()
//                            .forEach(user -> {
//                                KNearestNeighbours.put(user.getIndex(), getKNearestNeighbours(user.getIndex()-1,k));
//                            })).get();
//        }
//
//        catch (Exception e){
//            e.printStackTrace();
//        }
        System.out.println(userList.size());
        for (User user : userList) {
            System.out.print("\r" + (double) user.getIndex() * 100 / (double) userList.size() + "%");
            ArrayList<Integer> list = getKNearestNeighbours(user.getIndex() - 1, k);
            KNearestNeighbours.put(user.getIndex(), list);
        }
        System.out.println("NearestNeighbours Size: " + KNearestNeighbours.size());
    }

}

