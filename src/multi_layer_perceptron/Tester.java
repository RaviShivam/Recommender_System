package multi_layer_perceptron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by ravishivam on 29-2-16.
 */
public class Tester {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner featureScan = new Scanner(new FileInputStream(new File("data/features.txt")));
        Scanner targetScan = new Scanner(new FileInputStream(new File("data/target.txt")));
        List<FeatureVector> trainFeatures = new ArrayList<>();
        List<FeatureVector> testFeatures = new ArrayList<>();
        List<Double> targets = new ArrayList<>();
        for (int j = 0; j < 7000; j++) {
            String[] strings = featureScan.next().split(",");
            List<Double> outputList = new ArrayList<>();
            double rating = Double.parseDouble(targetScan.next());
            for (int i = 1; i < 8; i++) {
                if(rating == i){
                    outputList.add(1.0);
                }
                else{
                    outputList.add(0.0);
                }
            }
            FeatureVector featureVector = new FeatureVector(outputList);
            for (int i = 0; i < strings.length; i++) {
                double feature = Double.parseDouble(strings[i]);
                featureVector.add(feature);
            }
            trainFeatures.add(featureVector);
        }
        while(featureScan.hasNext()){
            String[] strings = featureScan.next().split(",");
            List<Double> outputList = new ArrayList<>();
            double rating = Double.parseDouble(targetScan.next());
            for (int i = 1; i < 8; i++) {
                if(rating == i){
                    outputList.add(1.0);
                }
                else{
                    outputList.add(0.0);
                }
            }
            FeatureVector testVector = new FeatureVector(outputList);
            for (int i = 0; i < strings.length; i++) {
                double feature = Double.parseDouble(strings[i]);
                testVector.add(feature);
            }
            testFeatures.add(testVector);
        }
        List<Integer> hidden = new ArrayList<>();
        hidden.add(8);
        MLPPredictor predictor = new MLPPredictor(3,10,hidden,7);
        System.out.println(predictor.getWeights());
        System.out.println(trainFeatures.size());
        for (int j = 0; j < 3000; j++) {
            for (int i = 0; i < trainFeatures.size(); i++) {
                predictor.train(trainFeatures.get(i));
            }
            System.out.println(j);
        }
        System.out.println(predictor.getWeights());
        // calculate RMSE
        double sum = 0;
        for (int i = 0; i < testFeatures.size(); i++) {

            double dOut = testFeatures.get(i).getLabel().indexOf(1.0);
            double aOut = predictor.predict(testFeatures.get(i));
//            System.out.println("desired out: " + dOut + " ,actual out: " + aOut);
//            double error = Math.pow((dOut-aOut),2);
//            sum += error;
            if(dOut!=aOut){
                sum++;
            }
        }
        sum = sum/testFeatures.size();
        System.out.println(sum);
    }
}

