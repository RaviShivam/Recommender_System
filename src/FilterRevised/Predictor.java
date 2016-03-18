package FilterRevised;

import CollaborativeFiltering.Database;

import java.io.IOException;
import java.util.*;

/**
 * Created by ravishivam on 16-3-16.
 */
public class Predictor {
    int k = 18;//20;//13;//25;//50;
    Database database = null;
    Map<Integer, TreeMap<Double, Integer>> similarities = new HashMap<>();
    Map<Integer,HashMap<Integer,Double>> utilityMatrix = new HashMap();
    Map<Integer,HashMap<Integer,Double>> rowToCol = new HashMap();
    Map<Integer, Double> rowmeans = new HashMap<>();
    Map<Integer, Double> colmeans = new HashMap<>();
    int totalNumOfRows = utilityMatrix.size();
    boolean itemBased = true;

    public Predictor(Map<Integer, HashMap<Integer, Double>> utilityMatrix,
                     Map<Integer, HashMap<Integer, Double>> rowToCol,
                     Map<Integer, Double> rowmeans,
                     Map<Integer, Double> colmeans
                    ) throws IOException, ClassNotFoundException {
        this.utilityMatrix = utilityMatrix;
        this.rowToCol = rowToCol;
        this.rowmeans = rowmeans;
        this.colmeans = colmeans;
        totalNumOfRows = rowToCol.size();
    }

    public double predictRating(int col, int row){
        Map<Integer, Double> rowsRated = utilityMatrix.get(col);
        double upper = 0.0;
        double lower = 0.0;
        TreeMap<Double, Integer> sims;
        if(similarities.containsKey(row)){
            sims = similarities.get(row);
        }
        else {
            sims = new TreeMap<>(Collections.reverseOrder());
            for (int currentRow = 1; currentRow < totalNumOfRows+1; currentRow++) {
                if(currentRow==row){
                    continue;
                }
                double coeffient = getCorrelationCoefficentPearson(row, currentRow);

                if(coeffient>=-0.02){
                    sims.put(coeffient, currentRow);
                }
            }
            similarities.put(row, getSublist(sims));
        }
        int reached = 0;
        for (Map.Entry<Double, Integer> entry : sims.entrySet()) {
            if(reached==k) break;
            if(rowsRated.containsKey(entry.getValue())) {
                if(itemBased) {
                    upper += entry.getKey() * (rowsRated.get(entry.getValue()) - database.getBaseLine(col, entry.getValue()));
                }
                else upper += entry.getKey() * (rowsRated.get(entry.getValue()) - database.getBaseLine(entry.getValue(),col));
                lower += entry.getKey();
                reached++;
            }
        }
        if(rowToCol.get(row).size()==0){
            return colmeans.get(col);
        }
        if(utilityMatrix.get(col).size()==0){
            return rowmeans.get(row);
        }
        double prediction;
        if(lower==0){
            prediction = colmeans.get(col);
        }
        else {
            if(itemBased)
            prediction = database.getBaseLine(col,row) + (upper/lower);
            else prediction = database.getBaseLine(row,col) + (upper/lower);
        }
        if(prediction>5.0){
            return 5.0;
        }
        if(prediction<1.0){
            return 1.0;
        }

        return prediction;
    }

    public double getCorrelationCoefficentPearson(int firstrow, int secondrow){
        double firstRowMean = rowmeans.get(firstrow);
        double secondRowMean = rowmeans.get(secondrow);
        HashMap<Integer,Double> rowRatings1 = (HashMap<Integer, Double>) rowToCol.get(firstrow).clone();
        HashMap<Integer,Double> rowRatings2 = (HashMap<Integer, Double>) rowToCol.get(secondrow).clone();
        Set<Integer> ratingsIntersection = new HashSet<Integer>(rowRatings1.keySet());
        ratingsIntersection.retainAll(rowRatings2.keySet());
        if(ratingsIntersection.isEmpty()){
            return 0.0;
        }
        double sum = 0.0;
        double asq = 0.0;
        double bsq = 0.0;
        for (Double d:rowRatings1.values()) {
            asq += Math.pow(d-firstRowMean,2);
        }
        for (Double d : rowRatings2.values()) {
            bsq += Math.pow(d-secondRowMean,2);
        }
        for (Integer similairRating : ratingsIntersection) {
            double diff1;
            double diff2;
            diff1 = rowRatings1.get(similairRating) - firstRowMean;
            diff2 = rowRatings2.get(similairRating) - secondRowMean;
            sum+= diff1*diff2;
        }
        if(sum==0||asq==0||bsq==0){
            return 0;
        }
        return sum/Math.sqrt(asq*bsq);
    }

    public TreeMap<Double, Integer> getSublist(TreeMap<Double, Integer> map){
        TreeMap<Double, Integer> ret = new TreeMap<>(Collections.reverseOrder());
        int runs = 1000;
        int count =0;
        for (Map.Entry<Double,Integer> entry : map.entrySet()) {
            ret.put(entry.getKey(),entry.getValue());
            count++;
            if(count==runs){
                break;
            }
        }
        return ret;
    }

    public void setItemBased(boolean itemBased) {
        this.itemBased = itemBased;
    }
    public void setDatabase(Database database) {
        this.database = database;
    }

}
