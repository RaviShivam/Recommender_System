package multi_layer_perceptron;

import Reader.*;

import java.io.*;
import java.util.*;

/**
 * Created by ravishivam on 24-2-16.
 */
public class PredictionRunner {
    static MLPPredictor predictor;
    static RatingList predictions = new RatingList();


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Input.InitInput();
        initNetwork();
//        trainNetwork();
//        testNetwork();
        predictRatings(predictor);
    }
    public static void initNetwork(){
        List<Integer> hidden = new ArrayList<>();
        hidden.add(8);
        predictor = new MLPPredictor(3,4,hidden,5);
    }

    public static void predictRatings(MLPPredictor predictor) throws IOException, ClassNotFoundException {
        List<Map<Integer,List<Double>>> e = null;
        FileInputStream fileIn = new FileInputStream("data/MovieWeights2.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (List<Map<Integer,List<Double>>>) in.readObject();
        in.close();
        fileIn.close();

        //Starting predictions
        predictor.setWeights(e);
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
        List<FeatureVector> trainingData = Input.getTrainingData();
        for (int i = 0; i < 20; i++) {
            System.out.println(i);
            for (int j = 0; j < trainingData.size(); j++) {
                predictor.train(trainingData.get(j));
            }
        }
        serializeWeights(predictor.getWeights());
    }
    public static void testNetwork() throws IOException, ClassNotFoundException {
        List<Map<Integer,List<Double>>> e = null;
        FileInputStream fileIn = new FileInputStream("data/MovieWeights2.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (List<Map<Integer,List<Double>>>) in.readObject();
        in.close();
        fileIn.close();

        //Starting predictions
        predictor.setWeights(e);
        int wrong = 0;
        List<FeatureVector> trainingData = Input.getTrainingData();
            for (int j = 50000; j < 80000; j++) {
                double dOutput = trainingData.get(j).getLabel().indexOf(1.0)+1;
               double aOutput = predictor.predict(trainingData.get(j));
                if(dOutput!=aOutput){
                    System.out.println(" reached");
                    wrong++;
                }
                System.out.println("desired output: " + dOutput + " ,actual output: " + aOutput);
        }
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
    public static void serializeWeights(List<Map<Integer,List<Double>>> weights) throws IOException {
        //serialize trained weights
        FileOutputStream fileOut = new FileOutputStream("data/MovieWeights2.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(predictor.getWeights());
        out.close();
        fileOut.close();
        System.out.println("Weights saved succesfully");
        //end
    }
}
