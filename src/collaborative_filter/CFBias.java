package collaborative_filter;

import Reader.MovieList;
import Reader.UserList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravishivam on 26-3-16.
 */


public class CFBias {
    double learningRate = 0.002;
    Map<Integer,HashMap<Integer,Double>> utilityMatrix = new HashMap();
    Map<Integer,HashMap<Integer,Double>> rowToCol = new HashMap();
    Map<Integer,HashMap<Integer,Double>> biases = new HashMap();
    boolean itembased;
    Database database;

    public CFBias(Database database,Map<Integer,
                    HashMap<Integer, Double>> utilityMatrix,
                     Map<Integer, HashMap<Integer, Double>> rowToCol,
                        boolean itembased) throws IOException, ClassNotFoundException {
        this.database = database;
        this.itembased = itembased;
        this.utilityMatrix = utilityMatrix;
        this.rowToCol = rowToCol;
        normalizeUtility();
        initBiases();
    }

    public double predictRating(int user, int movie){
        double prediction = database.getBaseLine(user, movie);
        HashMap<Integer, Double> similairitems = database.getUserToMovieMap().get(user);
        double secondterm = 0.0;
        for (Integer i :
                similairitems.keySet()) {
            double diff = utilityMatrix.get(user).get(i);
            prediction += biases.get(movie).get(i) * diff;
        }
        return prediction;
    }

    public void trainCombination(int user, int movie, double desiredrating){
        double actualrating = predictRating(user, movie);
        double error = -2 * (desiredrating - actualrating);
        HashMap<Integer, Double> sims  = utilityMatrix.get(user);
        for (Integer i :
                sims.keySet()) {
            double gradient =  error * utilityMatrix.get(user).get(i);
            double correction = biases.get(movie).get(i) - learningRate * gradient;
            biases.get(movie).put(i, correction);
            biases.get(i).put(movie, correction);
        }

    }

    public void normalizeUtility() {
        System.out.println("Normilizing utility...");
        for (Integer i : utilityMatrix.keySet()) {
            System.out.print("\r" + String.format("%.2f", (i+1)*100.0/(double)utilityMatrix.keySet().size()) + "% completed");
            for (Integer j : utilityMatrix.get(i).keySet()) {
                double oldval = utilityMatrix.get(i).get(j);
                double base;
                if (itembased) {
                    base = database.getBaseLine(i, j);
                } else {
                    base = database.getBaseLine(j, i);
                }
                utilityMatrix.get(i).put(j, oldval - base);
            }
        }
        System.out.println();
    }
    public void initBiases(){
        System.out.println("Initializing biases...");
        if(itembased) {
            MovieList list = database.getMovieList();
            for (int i = 0; i < list.size(); i++) {
                System.out.print("\r" + String.format("%.2f",(i+1)*100.0/(double)list.size()) + "% completed");
                biases.put(list.get(i).getIndex(), new HashMap<>());
                for (int j = 0; j < list.size(); j++) {
                    biases.get(list.get(i).getIndex()).put(list.get(j).getIndex(), Math.random());
                }
            }
        }
        else{
            UserList list = database.getUserList();
            for (int i = 0; i < list.size(); i++) {
                System.out.print("\r" + (i+1)*100.0/(double)list.size() + "% completed");
                biases.put(list.get(i).getIndex(), new HashMap<>());
                for (int j = 0; j < list.size(); j++) {
                    biases.get(list.get(i).getIndex()).put(list.get(j).getIndex(), Math.random());
                }
            }
        }
        System.out.println();
    }
}
