package collaborative_filter;

import Reader.RatingList;

import java.util.HashMap;

/**
 * Created by ravishivam on 18-3-16.
 */
public class MainExtended {
    public static void main(String[] args) {
        HashMap<Integer, Integer> val = new HashMap<>();
        val.put(1,2);
        val.put(3,2);
        val.put(1,3);
        System.out.println(val);
        RatingList list = new RatingList();
//        list.readFileDatabaseExtended();
    }
}
