package NearestNeighbour;

import mlp.FeatureVector;
import Reader.*;

import java.util.*;


public class NearestNeighbour {
    List<FeatureVector> dataset = new ArrayList<>();
    MovieList movieslist  = new MovieList();
    UserList userlist    = new UserList();

    static Map<Integer, Movie> moviemap = new HashMap<>();
    static Map<Integer, User> usersmap = new HashMap<>();

    public MovieList getMovieslist() {
        return movieslist;
    }

    public UserList getUserlist() {
        return userlist;
    }

    public void initDataSet(){

        movieslist.readFile("data/movies.csv");
        for (int i = 0; i <movieslist.size(); i++) {
            moviemap.put(i,movieslist.get(i));
        }
        userlist.readFile("data/users.csv");
        for (int i = 0; i <userlist.size(); i++) {
            usersmap.put(i,userlist.get(i));
        }
    }
	/**
	 * Classifies a query. Finds the k nearest neighbours and scales them if necessary.
	 * @param features The query features.
	 * @param k The number of neighbours to select.
	 * @return Returns the label assigned to the query.
	 */
	public double predict(List<Double> features, int k) {
        List<Measurement> distances = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            Measurement measure  = new Measurement(dataset.get(i),dataset.get(i).distance(features));
            distances.add(measure);
        }
        Collections.sort(distances);
        double value = 0;
        for (int i = 0; i < k; i++) {
//            value += distances.get(i).getFeatureVector().getLabel();
        }
        return value/ (double) k;
	}

    public void setUpDataSet(){
        List<FeatureVector> featureVectorList = new ArrayList<>();
        RatingList ratingList = new RatingList();
        ratingList.readFile("data/ratings.csv",userlist,movieslist);
        for (int i = 0; i < ratingList.size(); i++) {
            User user = ratingList.get(i).getUser();
            Movie movie = ratingList.get(i).getMovie();
            FeatureVector vector = new FeatureVector(null);
            vector.add((double) user.getAge());
            vector.add((double) user.getProfession());
            if(user.isMale()){
                vector.add(1.0);
            }
            else vector.add(0.0);
            vector.add((double) movie.getIndex());
            vector.add((double) movie.getYear());
            vector.add((double) movie.getTitle().hashCode());
            featureVectorList.add(vector);
        }
        this.dataset = featureVectorList;
    }

    public List<FeatureVector> getPredictionData(RatingList predictions) {
        List<FeatureVector> featureVectorList = new ArrayList<>();
        for (int i = 0; i < predictions.size(); i++) {
            User user = predictions.get(i).getUser();
            Movie movie = predictions.get(i).getMovie();
            FeatureVector vector = new FeatureVector(null);
            vector.add((double) user.getAge());
            vector.add((double) user.getProfession());
            if(user.isMale()){
                vector.add(1.0);
            }
            else vector.add(0.0);
            vector.add((double) movie.getIndex());
            vector.add((double) movie.getYear());
            vector.add((double) movie.getTitle().hashCode());
            featureVectorList.add(vector);
        }
        return featureVectorList;
    }

}
