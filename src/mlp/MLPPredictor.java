package mlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ravishivam on 24-2-16.
 *
 */
public class MLPPredictor {
    int layers, inputs, output;
    double learningRate = 0.1;
    List<Integer> hidden;
    List<Map<Integer, List<Double>>> weights;


    public MLPPredictor(int layers, int inputs, List<Integer> hidden, int output) {
        this.layers = layers;
        this.inputs = inputs;
        this.output = output;
        this.hidden = hidden;
        weights = Functions.initializeWeigts(inputs, hidden, output);
    }

    /**
     * Trainer method
     *
     * @return
     */
    public void train(FeatureVector feature) {
        FeatureVector aHiddenOutput = new FeatureVector(1);
        FeatureVector fv = (FeatureVector) feature.clone();
        fv.add(-1.0);
        Map<Integer, List<Double>> map = weights.get(0);
        for (int i = 0; i < map.size(); i++) {
            double avalue = Functions.activate(fv.product(map.get(i)));
            aHiddenOutput.add(avalue);
        }
        aHiddenOutput.add(-1.0);
        double aOutput = Functions.activate(aHiddenOutput.product(weights.get(1).get(0)));
        double error = fv.getLabel() - aOutput;
        double errorgradient = aOutput * (1 - aOutput) * error;
        for (int i = 0; i < aHiddenOutput.size(); i++) {
            double deltaw = learningRate * aHiddenOutput.get(i) * errorgradient;
            double correctedWeight = deltaw + weights.get(1).get(0).get(i);
            weights.get(1).get(0).set(i, correctedWeight);
        }
        for (int i = 0; i < weights.get(0).size(); i++) {
            double herrorgradient = aHiddenOutput.get(i) * (1 - aHiddenOutput.get(i)) * errorgradient * weights.get(1).get(0).get(i);
            List<Double> currenthiddennode = weights.get(0).get(i);
            for (int j = 0; j < currenthiddennode.size(); j++) {
                double hdeltaw = learningRate * fv.get(j) * herrorgradient;
                double hcorrectedweight = currenthiddennode.get(j) + hdeltaw;
                weights.get(0).get(i).set(j, hcorrectedweight);
            }
        }
    }

    public double predict(FeatureVector featureVector) {
        FeatureVector aHiddenOutput = new FeatureVector(1);
        FeatureVector fv = (FeatureVector) featureVector.clone();
        fv.add(-1.0);
        int numh = weights.get(0).size();
        Map<Integer, List<Double>> map = weights.get(0);
        for (int i = 0; i < map.size(); i++) {
            double avalue = Functions.activate(fv.product(map.get(i)));
            aHiddenOutput.add(avalue);
        }
        aHiddenOutput.add(-1.0);
        double output = Functions.activate(aHiddenOutput.product(weights.get(1).get(0)));
        return output;
    }

    public void setWeights(List<Map<Integer, List<Double>>> weights) {
        this.weights = weights;
    }
    public List<Map<Integer, List<Double>>> getWeights() {return weights;}
}

class Functions {
    public static List<Map<Integer,List<Double>>> initializeWeigts(int inputs, List<Integer> hidden, int output){
        List<Map<Integer,List<Double>>> weights = new ArrayList<>(hidden.size()+1);
        List<Integer> numNeuronsPerLayers = new ArrayList<>(2+hidden.size());
        numNeuronsPerLayers.add(inputs);
        for (int i = 0; i < hidden.size(); i++) {
            numNeuronsPerLayers.add(hidden.get(i));
        }
        numNeuronsPerLayers.add(output);
        for (int i = 1; i < numNeuronsPerLayers.size(); i++) {
            int numneurons = numNeuronsPerLayers.get(i);
            HashMap<Integer, List<Double>> weightvectors = new HashMap<>(numneurons);
            for (int j = 0; j < numneurons; j++) {
               List<Double> wvector = new ArrayList<>();
                int size = numNeuronsPerLayers.get(i-1);
                for (int k = 0; k < size; k++) {
                    double randomnum = (-2.4/inputs) + (Math.random() * (4.8/inputs));
                    wvector.add(randomnum);
                }
                double randomthreshold = -1.0 + (Math.random() * 2.0);
                wvector.add(randomthreshold);
                weightvectors.put(j,wvector);
            }
            weights.add(weightvectors);
        }
        return weights;
    }

    public static double activate(double num) {
        double sigmoid = 1 / (1 + Math.exp(-num));
        return sigmoid;
    }
}
