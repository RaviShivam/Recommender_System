package CollaborativeFiltering;

import Reader.Rating;
import Reader.RatingList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int cores = 8;
        ForkJoinPool fork =  new ForkJoinPool(cores);

        UserBased userBased = new UserBased();
        ItemBased itemBased = new ItemBased();
        RatingList predRatings = new RatingList();
        predRatings.readFile("data/predictions.csv", userBased.getDatabase().getUserList(), userBased.getDatabase().getMovieList());
//        RatingList finalList = new RatingList();
//        List<Rating> parallelList = Collections.synchronizedList(predRatings);
//        long start = System.currentTimeMillis()/1000;
//        try {
//            fork.submit(
//                    () -> parallelList.parallelStream()
//                            .forEach(rating -> {
//                                double prediction = itemBased.predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex());
//                                rating.setRating(prediction);
//                            })).get();
//        finalList.addAll(parallelList);
//        }
//
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        System.out.println(System.currentTimeMillis()/1000-start);
//        finalList.writeResultsFile("submissions/submission.csv");

//        for (int i = 0; i < predRatings.size(); i++) {
//            System.out.print("\r" + i*100/predRatings.size() + "%");
//            double rating = itemBased.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
//            predRatings.get(i).setRating(rating);
//        }
//        predRatings.writeResultsFile("submission.csv");
        System.out.println(itemBased.predictRating(32,1569));
    }

}
