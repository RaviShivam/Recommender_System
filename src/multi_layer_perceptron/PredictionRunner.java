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
        trainNetwork();
        testNetwork();
//        predictRatings(predictor);
    }
    public static void initNetwork(){
        List<Integer> hidden = new ArrayList<>();
        hidden.add(80);
        predictor = new MLPPredictor(3,6,hidden,5);
    }

    public static void predictRatings(MLPPredictor predictor) throws IOException, ClassNotFoundException {
        List<Map<Integer,List<Double>>> e = null;
        FileInputStream fileIn = new FileInputStream("data/MovieWeights3.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (List<Map<Integer,List<Double>>>) in.readObject();
        in.close();
        fileIn.close();

        //Starting predictions
        predictor.setWeights(e);
        predictions.readFile("data/predictions.csv",Input.getUserlist(),Input.getMovieslist());
        List<FeatureVector> predictionVector = Input.getPredictionData();
        for(int i=0; i<predictionVector.size();i++){
            double pred = predictor.predict(predictionVector.get(i));
            predictions.get(i).setRating(pred);
        }
        //End predictions
        predictions.writeResultsFile("submission.csv");
    }

    public static void trainNetwork() throws IOException {
        List<FeatureVector> trainingData = Input.getTrainingData();
        int epochs = 20;
        for (int i = 0; i < epochs; i++) {
            System.out.print("\r" + (i+1)*100/epochs + "% completed");
            for (int j = 0; j < trainingData.size(); j++) {
                predictor.train(trainingData.get(j));
            }
        }
        System.out.println();
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
            for (int j = 0; j < 1000; j++) {
                double dOutput = trainingData.get(j).getLabel()+1;
                double aOutput = predictor.predict(trainingData.get(j));
                if(dOutput!=aOutput){
                    wrong++;
                }
                System.out.println("desired output: " + dOutput + " ,actual output: " + aOutput);
        }
        System.out.println("Error percentage: " + wrong*100/1000.0 + "%");
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
