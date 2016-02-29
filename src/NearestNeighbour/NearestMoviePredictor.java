package NearestNeighbour;

import mlp.FeatureVector;
import mlp.Input;
import ti2736c.RatingList;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by ravishivam on 29-2-16.
 */
public class NearestMoviePredictor {
public  static  void main(String[] args){
    //Starting predictions
    NearestNeighbour nearestNeighbour = new NearestNeighbour();
    nearestNeighbour.initDataSet();
    nearestNeighbour.setUpDataSet();
    RatingList predictions = new RatingList();
    predictions.readFile("data/predictions.csv", nearestNeighbour.getUserlist(),nearestNeighbour.getMovieslist());
    List<FeatureVector> predictionVector = nearestNeighbour.getPredictionData(predictions);
    System.out.println(predictionVector.size());
    for(int i=0; i<predictionVector.size();i++){
        double pred = nearestNeighbour.predict(predictionVector.get(i),7);
        predictions.get(i).setRating(pred);
        System.out.println(i);
    }
    //End predictions
    predictions.writeResultsFile("submission.csv");
}
}
