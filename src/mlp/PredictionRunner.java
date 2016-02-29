package mlp;

import ti2736c.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ravishivam on 24-2-16.
 */
public class PredictionRunner {

    static RatingList predictions = new RatingList();


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MLPPredictor predictor;
        Input.InitInput();
        trainNetwork();
//        predictRatings(predictor);
    }

    public static void predictRatings(MLPPredictor predictor) throws IOException, ClassNotFoundException {
        List<Map<Integer,List<Double>>> e = null;
        FileInputStream fileIn = new FileInputStream("data/MovieWeights.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (List<Map<Integer,List<Double>>>) in.readObject();
        in.close();
        fileIn.close();

        //Starting predictions
        predictor.setWeights(e);
        System.out.println(predictor.getWeights());
        predictions.readFile("data/predictions.csv",Input.getUserlist(),Input.getMovieslist());
        List<FeatureVector> predictionVector = Input.getPredictionData(predictions);
        for(int i=0; i<predictionVector.size();i++){
            double pred = predictor.predict(predictionVector.get(i));
            predictions.get(i).setRating(pred);
        }
        //End predictions
        predictions.writeResultsFile("submission.csv");
    }

    public static void trainNetwork() throws IOException {
        List<Integer> hidden = new ArrayList<>();
        hidden.add(5);
        MLPPredictor mlp = new MLPPredictor(3,6,hidden,1);
        System.out.println(mlp.getWeights());
//        List<Map<Integer,List<Double>>> weights = mlp.getWeights();
        List<FeatureVector> trainingData = Input.getTrainingData();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < trainingData.size(); j++) {
                mlp.train(trainingData.get(i));
            }
        }
        System.out.println(mlp.getWeights());
        //serialize trained weights
        FileOutputStream fileOut = new FileOutputStream("data/MovieWeights.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(mlp.getWeights());
        out.close();
        fileOut.close();
    }

    static void updateProgress(double progressPercentage) {
        final int width = 50; // progress bar width in chars

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int)(progressPercentage*width); i++) {
            System.out.print(".");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }
}
