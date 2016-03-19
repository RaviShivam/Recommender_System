package latent_factor_model;

import Reader.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravishivam on 19-3-16.
 */
public class LatentFactorDatabase {
    UserList userlist;
    MovieList movielist;
    RatingList ratinglist;
    Array2DRowRealMatrix trainUtility;
    Array2DRowRealMatrix completeUtility;

    public LatentFactorDatabase() throws IOException, ClassNotFoundException {
        userlist = new UserList();
        userlist.readFile("data/users.csv");
        movielist = new MovieList();
        movielist.readFile("data/movies.csv");
        ratinglist = new RatingList();
        ratinglist.readFile("data/ratings.csv", userlist, movielist);
        trainUtility = new Array2DRowRealMatrix(userlist.size(), movielist.size());
        trainUtility = contructUtility(trainUtility, true);
        completeUtility = new Array2DRowRealMatrix(userlist.size(), movielist.size());
        completeUtility = contructUtility(completeUtility, false);
    }

    public Array2DRowRealMatrix contructUtility(Array2DRowRealMatrix matrix, boolean train){
        Map<Integer,HashMap<Integer,Double>> map;
        if (train){
             map = ratinglist.getUserToMovieSublist(0, ratinglist.size()*(3/4));
        }
        else {
            map = ratinglist.getUserToMovieSublist(ratinglist.size()*(3/4), ratinglist.size()-1);
            matrix = (Array2DRowRealMatrix) trainUtility.copy();
        }
        for (User user : userlist) {
            HashMap<Integer, Double> moviebuckets = map.get(user.getIndex());
            for (Movie movie : movielist) {
                if (!moviebuckets.containsKey(movie.getIndex())){
                    matrix.setEntry(user.getIndex()-1, movie.getIndex()-1,0);
                }
                else {
                    double rating = moviebuckets.get(movie.getIndex());
                    matrix.setEntry(user.getIndex()-1, movie.getIndex()-1,rating);
                }
            }
        }
        return matrix;
    }

    public Array2DRowRealMatrix getTrainUtility() {
        return trainUtility;
    }
    public Array2DRowRealMatrix getCompleteUtility() {
        return completeUtility;
    }

    public UserList getUserlist() {
        return userlist;
    }

    public MovieList getMovielist() {
        return movielist;
    }

    public RatingList getRatinglist() {
        return ratinglist;
    }


}
