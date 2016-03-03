package CollaborativeFiltering;

import Reader.Rating;
import Reader.RatingList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class main {
    public static void main(String[] args) {
        int cores = 8;
        ForkJoinPool fork =  new ForkJoinPool(cores);

        UserBasedCollab userBasedCollab = new UserBasedCollab();
        userBasedCollab.initData();
        RatingList finalList = new RatingList();
        RatingList predRatings = new RatingList();
        predRatings.readFile("data/predictions.csv", userBasedCollab.userList, userBasedCollab.movieList);
        List<Rating> parallelList = Collections.synchronizedList(predRatings);
//        System.out.println(cores);
        try {
            fork.submit(
                    () -> parallelList.parallelStream()
                            .forEach(rating -> {rating
                                    .setRating(userBasedCollab
                                            .predictRating(rating.getUser().getIndex(), rating.getMovie().getIndex() ));})).get();
        finalList.addAll(parallelList);
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finalList.writeResultsFile("submission.csv");

//        for (int i = 0; i < predRatings.size(); i++) {
//            System.out.print("\r" + i*100/predRatings.size() + "%");
//            double rating = userBasedCollab.predictRating(predRatings.get(i).getUser().getIndex(), predRatings.get(i).getMovie().getIndex());
//            predRatings.get(i).setRating(rating);
//        }
//        predRatings.writeResultsFile("submission.csv");

    }

}
