package CollaborativeFiltering;

import Reader.MovieList;
import Reader.RatingList;
import Reader.User;
import Reader.UserList;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by ravishivam on 3-3-16.
 */
public class UserBasedCollab {
    UserList userList = new UserList();
    MovieList movieList = new MovieList();
    RatingList ratingList = new RatingList();
    Map<Integer, Double> usersMean = new HashMap<>();
    Map<Integer, ArrayList<Integer>> KNearestNeighbours = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> userToMovieMap = new HashMap<>();
    Map<Integer, HashMap<Integer, Double>> movieToUserMap = new HashMap<>();

    public UserBasedCollab() throws IOException, ClassNotFoundException {
        userList.readFile("data/users.csv");
        movieList.readFile("data/movies.csv");
        ratingList.readFile("data/ratings.csv", userList, movieList);
        userToMovieMap = ratingList.getUserToMovieHashMap();
        getAllUserMean();
//        KNearestNeighbours = (HashMap<Integer, ArrayList<Integer>>) Computations.getSerializedItem("objects/NearestNeighbours.ser");
        initKNearestNeighbours(1000);
        Computations.serializeItem(KNearestNeighbours, "objects/NearestNeighbours.ser");
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
        if(userToMovieMap.containsKey(user)&& userToMovieMap.get(user).containsKey(movie)){
            System.out.println("already here");
            return userToMovieMap.get(user).get(movie);
        }
        double adder = 0.0;
        int normalizer = 0;
        ArrayList<Integer> secondUsers = KNearestNeighbours.get(user);
        for (Integer seconduser : secondUsers) {
            if (userToMovieMap.get(seconduser).containsKey(movie)) {
                double corr = getCorrelationCoefficentPearson(user, seconduser);
                double mult = (userToMovieMap.get(seconduser).get(movie) - usersMean.get(seconduser));
                adder += corr * mult;
                normalizer++;
            }
        }
        if(normalizer==0){
            return usersMean.get(user);
        }    
        double prediction = usersMean.get(user) + (adder/normalizer);
        return Computations.round(prediction);
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
    public ArrayList<Integer> getKNearestNeighbours(int user, int k){
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
            if (Math.abs(curr.getAge()-second.getAge()) < 3){
                correlation++;
            }
            if((curr.isMale()&&second.isMale()) || (!curr.isMale()&& !second.isMale())){
                correlation++;
            }
            if (curr.getProfession()==second.getProfession()){
                correlation+=2;
            }
            correlation += movieIntersection.size();
            correlation = correlation/(movieUnion.size()+3);
            secondUsers.put(correlation,second.getIndex());
        }
        List<Integer> nearestNeighboursList = new ArrayList<>(secondUsers.values());
        ArrayList<Integer> knearest = new ArrayList<>();
        if(nearestNeighboursList.size()<k){
            k = nearestNeighboursList.size();
        }
        for (int i = 0; i < k; i++) {
           knearest.add(nearestNeighboursList.get(i));
        }
        return knearest;
    }

    public void initKNearestNeighbours(int k){
        int cores = 4;
        System.out.println(cores);
        ForkJoinPool fork =  new ForkJoinPool(cores);
        List<User> parallelList = Collections.synchronizedList(userList);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(user -> {
                                KNearestNeighbours.put(user.getIndex(), getKNearestNeighbours(user.getIndex()-1,k));
                            })).get();
        }

        catch (Exception e){
            e.printStackTrace();
        }
//        for (User user: userList) {
//            System.out.print("\r" + (double)user.getIndex()*100/(double)userList.size() + "%");
//            ArrayList<Integer> list = getKNearestNeighbours(user.getIndex(), k);
//            KNearestNeighbours.put(user.getIndex(), list);
//        }
    }

    //Getter methods.
    //*******************
    //*******************
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

    public UserList getUserList() {
        return userList;
    }

}


















//    public void initMovieInterSections() {
//        HashMap<Integer, HashMap<Integer, HashSet<Integer>>> movieIntersections = new HashMap<>();
//        for (int i = 1; i < userList.size(); i++) {
//            System.out.println(i);
//            HashMap<Integer,HashSet<Integer>> set = new HashMap<Integer, HashSet<Integer>>();
//            HashMap<Integer,HashSet<Integer>> unionset = new HashMap<Integer, HashSet<Integer>>();
//            for (int j = i+1; j < userList.size(); j++) {
//                HashMap<Integer, Double> currUserMap = userToMovieMap.get(i);
//                HashMap<Integer, Double> secondUserMap = userToMovieMap.get(j);
//                HashSet<Integer> intersection = new HashSet<>(currUserMap.keySet());
//                intersection.retainAll(secondUserMap.keySet());
////                HashSet<Integer> union = new HashSet<>(currUserMap.keySet());
////                union.addAll(secondUserMap.keySet());
//                set.put(j, intersection);
//            }
//            movieIntersections.put(i,set);
//        }
//        this.allMovieInterSections = movieIntersections;
//    }
