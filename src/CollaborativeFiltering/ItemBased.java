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
        if(!rate.containsKey(movie)){
            return database.getUsersMean().get(user);
        }
        double sum = 0;
        for (Double d :
                rate.get(movie).values()) {
            sum += d;
        }
        return Computations.round(sum/(rate.get(movie).size()));
    }
}
