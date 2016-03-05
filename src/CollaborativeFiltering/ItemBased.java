package CollaborativeFiltering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravishivam on 5-3-16.
 */
public class ItemBased {
    Database database;

    public ItemBased() throws IOException, ClassNotFoundException {
        database = new Database();
    }

    public double predictRating(int user, int movie){
        Map<Integer,HashMap<Integer, Double>> rate = database.getMovieToUserMap();
        HashMap<Integer, Double> ratings = rate.get(movie);
        if(rate == null){
            System.out.println("reached");
            return 3.0;
        }
        double sum = 0;
        for (Double d :
                ratings.values()) {
            sum += d;
        }
        if(sum == 0){
            return 2.0;
        }
        return Computations.round(sum/ratings.size());
    }
}
