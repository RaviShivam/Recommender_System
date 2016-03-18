package multi_layer_perceptron;

import java.util.*;

/**
 * Created by ravishivam on 24-2-16.
 */
public class MLPPredictor {
    int layers, inputs, output;
    double learningRate = 0.4;
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
        FeatureVector aHiddenOutput = new FeatureVector(null);
        List<Map<Integer, List<Double>>> clonedWeights = new ArrayList<>(weights);
        FeatureVector fv = (FeatureVector) feature.clone();
        fv.add(-1.0);

        //actual outputs in hidden layer
        for (int i = 0; i < weights.get(0).size(); i++) {
            double avalue = Functions.activate(fv.product(weights.get(0).get(i)));
            aHiddenOutput.add(avalue);
        }
        aHiddenOutput.add(-1.0);
        //end

        //actual outputs in output neurons
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < this.output; i++) {
            double aOutput = Functions.activate(aHiddenOutput.product(weights.get(1).get(i)));
            outputs.add(aOutput);
        }
        //end

        List<Double> outputerrorgradients = new ArrayList<>();
        //update output layer weights.
        for (int j = 0; j < outputs.size(); j++) {
            double error = fv.getLabel().get(j) - outputs.get(j);
            double errorgradient = outputs.get(j) * (1 - outputs.get(j)) * error;
            outputerrorgradients.add(errorgradient);
            for (int i = 0; i < aHiddenOutput.size(); i++) {
                double deltaw = learningRate * aHiddenOutput.get(i) * errorgradient;
                double correctedWeight = deltaw + weights.get(1).get(j).get(i);
                weights.get(1).get(j).set(i, correctedWeight);
            }
        }
        //end
        List<Double> weightedGradientSumList = new ArrayList<>();
        for (int j = 0; j < hidden.get(0); j++) {
            double sumMult = 0;
            for (int i = 0; i < outputs.size(); i++) {
                double mult = outputerrorgradients.get(i) * clonedWeights.get(1).get(i).get(j);
                sumMult += mult;
            }
            weightedGradientSumList.add(sumMult);
        }
        //update hidden layer weights.
        for (int i = 0; i < weights.get(0).size(); i++) {
            double herrorgradient = aHiddenOutput.get(i) * (1 - aHiddenOutput.get(i)) * weightedGradientSumList.get(i) * weights.get(1).get(0).get(i);
            List<Double> currenthiddennode = weights.get(0).get(i);
            for (int j = 0; j < currenthiddennode.size(); j++) {
                double hdeltaw = learningRate * fv.get(j) * herrorgradient;
                double hcorrectedweight = currenthiddennode.get(j) + hdeltaw;
                weights.get(0).get(i).set(j, hcorrectedweight);
            }
        }
        //end
    }

    public double predict(FeatureVector featureVector) {
        FeatureVector aHiddenOutput = new FeatureVector(null);
        FeatureVector fv = (FeatureVector) featureVector.clone();
        fv.add(-1.0);
        Map<Integer, List<Double>> map = weights.get(0);
        for (int i = 0; i < map.size(); i++) {
            double avalue = Functions.activate(fv.product(map.get(i)));
            aHiddenOutput.add(avalue);
        }
        aHiddenOutput.add(-1.0);
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < weights.get(1).size(); i++) {
            double output = Functions.activate(aHiddenOutput.product(weights.get(1).get(i)));
            outputs.add(output);
        }
        double index = 1;
        double max = outputs.get(0);
        for (int i = 1; i < outputs.size(); i++) {
            if(outputs.get(i)>max){
                max = outputs.get(i);
                index = i+1;
            }
        }
        return index;
    }

    public List<Map<Integer, List<Double>>> getWeights() {
        return weights;
    }

    public void setWeights(List<Map<Integer, List<Double>>> weights) {
        this.weights = weights;
    }
}

class Functions {
    public static List<Map<Integer, List<Double>>> initializeWeigts(int inputs, List<Integer> hidden, int output) {
        List<Map<Integer, List<Double>>> weights = new ArrayList<>(hidden.size() + 1);
        List<Integer> numNeuronsPerLayers = new ArrayList<>(2 + hidden.size());

        //initialize num neurons per layer.
        numNeuronsPerLayers.add(inputs);
        for (int i = 0; i < hidden.size(); i++) {
            numNeuronsPerLayers.add(hidden.get(i));
        }
        numNeuronsPerLayers.add(output);
        //end

        //initialize weights of neurons.
        for (int i = 1; i < numNeuronsPerLayers.size(); i++) {
            int numneurons = numNeuronsPerLayers.get(i);
            HashMap<Integer, List<Double>> weightvectors = new HashMap<>(numneurons);
            for (int j = 0; j < numneurons; j++) {
                List<Double> wvector = new ArrayList<>();
                int size = numNeuronsPerLayers.get(i - 1);
                for (int k = 0; k < size; k++) {
                    double randomnum = (-2.4 / inputs) + (Math.random() * (4.8 / inputs));
                    wvector.add(randomnum);
                }
                double randomthreshold = -1.0 + (Math.random() * 2.0);
                wvector.add(randomthreshold);
                weightvectors.put(j, wvector);
            }
            weights.add(weightvectors);
        }
        //end
        return weights;
    }

    public static double activate(double num) {
        double sigmoid = 1 / (1 + Math.exp(-num));
        return sigmoid;
    }
}
