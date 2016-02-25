package mlp;


import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.util.Pair;
import ti2736c.*;

import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by ravishivam on 24-2-16.
 *
 */
public class MLPPredictor {
    int layers, inputs, output;
    List<Integer> hidden;
    Map<Integer,List<List<Double>>> weights;
    public MLPPredictor(int layers, int inputs, ArrayList<Integer> hidden,int output) {
        this.layers = layers;
        this.inputs = inputs;
        this.output = output;
        this.hidden = hidden;
       Functions.initializeWeigts(inputs,hidden,output);
    }

    /**
     * Trainer method
     * @return
     */
    public void train(FeatureVector fv){
        List<Double> aHiddenOutput = new ArrayList<>();
        for (int i = 0; i < fv.size(); i++) {

        }
    }
    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    public int getInputs() {
        return inputs;
    }

    public void setInputs(int inputs) {
        this.inputs = inputs;
    }

    public int getOutput() {
        return this.output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public List<Integer> getHidden() {
        return this.hidden;
    }

    public void setHidden(List<Integer> hidden) {
        this.hidden = hidden;
    }
}

class Functions {
    public static HashMap<Integer,List<List<Double>>> initializeWeigts(int inputs, ArrayList<Integer> hidden, int output){
        HashMap<Integer,List<List<Double>>> weights = new HashMap<>(hidden.size()+1);
        List<Integer> numneurons = new ArrayList<>(2+hidden.size());
        numneurons.add(inputs);
        for (int i = 0; i < hidden.size(); i++) {
            numneurons.add(hidden.get(i));
        }
        for (int i = 0; i < numneurons.size()-1; i++) {
            List<List<Double>> wvectorlist = new ArrayList<>();
            for (int j = 0; j < numneurons.get(i); j++) {
                List<Double> wvector = new ArrayList<>(numneurons.get(i+1));
                for (int k = 0; k < wvector.size(); k++) {
                    double randomnum = (-2.4/inputs) + (Math.random() * (2.4/inputs));
                    wvector.add(randomnum);
                }
                wvectorlist.add(wvector);
            }
            weights.put(i+1,wvectorlist);
        }
        return weights;
    }
    public static double activate(double num) {
        double sigmoid = 1 / (1 + Math.exp(num));
        return sigmoid;
    }
}
