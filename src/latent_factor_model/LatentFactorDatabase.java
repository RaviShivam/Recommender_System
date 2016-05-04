package latent_factor_model;

import Reader.*;

import java.io.IOException;

/**
 * Created by ravishivam on 19-3-16.
 */
public class LatentFactorDatabase {
    UserList userlist;
    MovieList movielist;
    RatingList ratinglist;

    public LatentFactorDatabase() throws IOException, ClassNotFoundException {
        userlist = new UserList();
        userlist.readFile("data/users.csv");
        movielist = new MovieList();
        movielist.readFile("data/movies.csv");
        ratinglist = new RatingList();
        ratinglist.readFile("data/ratings.csv", userlist, movielist);
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
