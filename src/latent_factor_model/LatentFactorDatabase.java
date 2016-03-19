package latent_factor_model;

import Reader.Movie;
import Reader.User;
import collaborative_filter.Database;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravishivam on 19-3-16.
 */
public class LatentFactorDatabase {
    Database database;
    Array2DRowRealMatrix utility;

    public LatentFactorDatabase() throws IOException, ClassNotFoundException {
        database = new Database();
        utility = new Array2DRowRealMatrix(database.getUserList().size(), database.getMovieList().size());
        contructUtility();
    }

    public void contructUtility(){
        Map<Integer,HashMap<Integer,Double>> map = database.getUserToMovieMap();
        for (User user : database.getUserList()) {
            HashMap<Integer, Double> moviebuckets = map.get(user.getIndex());
            for (Movie movie : database.getMovieList()) {
                if (!moviebuckets.containsKey(movie.getIndex())){
                    utility.setEntry(user.getIndex()-1, movie.getIndex()-1,0);
                }
                else {
                    double rating = moviebuckets.get(movie.getIndex());
                    utility.setEntry(user.getIndex()-1, movie.getIndex()-1,rating);
                }
            }
        }
    }
    public Database getDatabase() {
        return database;
    }

    public Array2DRowRealMatrix getUtility() {
        return utility;
    }

}
