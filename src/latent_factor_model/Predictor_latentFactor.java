package latent_factor_model;

import Reader.Rating;
import Reader.RatingList;
import collaborative_filter.Computations;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by ravishivam on 19-3-16.
 */
public class Predictor_latentFactor {
    int k = 2;
    static long time;
    double learningRate = 0.0002;
    double lambda = 0.02;
    LatentFactorDatabase latentFactorDatabase;
    Map<Integer, HashMap<Integer,Double>> utility;
    BlockRealMatrix Q;
    BlockRealMatrix P;


    public Predictor_latentFactor() throws IOException, ClassNotFoundException {
        latentFactorDatabase = new LatentFactorDatabase();
        utility = latentFactorDatabase.getRatinglist().getUserToMovieSublist(0,500000);
        initQeP();
    }

    public void initQeP(){
        P = new BlockRealMatrix(latentFactorDatabase.getUserlist().size(), k);
        for (int i = 0; i < P.getRowDimension()  ; i++) {
            for (int j = 0; j < P.getColumnDimension(); j++) {
                P.setEntry(i,j,Math.random());
            }
        }
        Q = new BlockRealMatrix(latentFactorDatabase.getMovielist().size(), k);
        for (int i = 0; i < Q.getRowDimension()  ; i++) {
            for (int j = 0; j < Q.getColumnDimension(); j++) {
                Q.setEntry(i,j,Math.random());
            }
        }
    }
    public void trainLatenFactorModel() throws IOException {
        Q=Q.transpose();
        BlockRealMatrix product = P.multiply(Q);
        //========================
        int runs = 4000;
        for (int times = 0; times < runs; times++) {
            System.out.print("\r" + (times+1)*100/runs + "% completed");
            ForkJoinPool pool = new ForkJoinPool(4);

            List<Integer> rows = new ArrayList<>();
            for (int i = 0; i < product.getRowDimension(); i++) {
                rows.add(i);
            }
            List<Integer> parallelList = Collections.synchronizedList(rows);
            try{
                pool.submit(
                        () -> parallelList.parallelStream()
                                .forEach(i -> {
                                            for (int j = 0; j < product.getColumnDimension(); j++) {
                                                if(utility.get(i+1).containsKey(j+1)){
                                                    double error = utility.get(i+1).get(j+1) - P.getRowMatrix(i).multiply(Q.getColumnMatrix(j)).getEntry(0,0);//product.getEntry(i,j);
                                                    //                        P[i][k] = P[i][k] + alpha * (2 * eij * Q[k][j] - beta * P[i][k])
                                                    //                        Q[k][j] = Q[k][j] + alpha * (2 * eij * P[i][k] - beta * Q[k][j])
                                                    for (int k = 0; k < this.k; k++) {
                                                        P.setEntry(i,k,(P.getEntry(i,k) + learningRate * (2*error*Q.getEntry(k,j) - lambda*P.getEntry(i,k))));
                                                        Q.setEntry(k,j,(Q.getEntry(k,j) + learningRate * (2*error*P.getEntry(i,k) - lambda*Q.getEntry(k,j))));
                                                    }
                                                    //updateQ(error,i,j);
                                                    }
                                            }
                                        }
                                )
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
//            for (int i = 0; i < product.getRowDimension(); i++) {
//                for (int j = 0; j < product.getColumnDimension(); j++) {
//                    if(utility.get(i+1).containsKey(j+1)){
//                        double error = utility.get(i+1).get(j+1) - P.getRowMatrix(i).multiply(Q.getColumnMatrix(j)).getEntry(0,0);//product.getEntry(i,j);
////                        P[i][k] = P[i][k] + alpha * (2 * eij * Q[k][j] - beta * P[i][k])
////                        Q[k][j] = Q[k][j] + alpha * (2 * eij * P[i][k] - beta * Q[k][j])
//                        for (int k = 0; k < this.k; k++) {
//                            P.setEntry(i,k,(P.getEntry(i,k) + learningRate * (2*error*Q.getEntry(k,j) - lambda*P.getEntry(i,k))));
//                            Q.setEntry(k,j,(Q.getEntry(k,j) + learningRate * (2*error*P.getEntry(i,k) - lambda*Q.getEntry(k,j))));
//                        }
////                        updateQ(error,i,j);
//                    }
//                }
//            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        startTimer();
        int runs = 10;
        Predictor_latentFactor pr = new Predictor_latentFactor();
        pr.trainLatenFactorModel();
        RatingList predRating = new RatingList();
        predRating.readFile("data/predictions.csv", pr.getLatentFactorDatabase().getUserlist(),pr.getLatentFactorDatabase().getMovielist());
        BlockRealMatrix blockRealMatrix = pr.getP().multiply(pr.getQ());
        for (int i = 0; i < predRating.size(); i++) {
            Rating rating = predRating.get(i);
            int user = rating.getUser().getIndex()-1;
            int movie = rating.getMovie().getIndex()-1;
            rating.setRating(blockRealMatrix.getEntry(user,movie));
            Rating set = predRating.set(i, rating);
        }
        Computations.serializeItem(pr.getP().getData(), "objects/Pmatrx.ser");
        Computations.serializeItem(pr.getQ().getData(), "objects/Qmatrx.ser");
        System.out.println();
        predRating.writeResultsFile("submissions/latentFactorPredictionsSecond.csv");
        System.out.println("Latent factor predictions completed succesfully.");
        endTimer();

    }

    public void setQ(BlockRealMatrix q) {
        this.Q = q;
    }

    public void setP(BlockRealMatrix p) {
        this.P = p;
    }

    public BlockRealMatrix getQ() {
        return Q;
    }

    public BlockRealMatrix getP()
    {
        return P;
    }
    static void startTimer(){
        time = System.currentTimeMillis();
    }

    public LatentFactorDatabase getLatentFactorDatabase() {
        return latentFactorDatabase;
    }

    static void endTimer(){
        time = System.currentTimeMillis()-time;
        System.out.println();
        System.out.println("===============================");
        System.out.println("Code runtime= " + (time/1000.0)/60.0 + " minutes");
    }
//    public void updateQ(double diff, int row, int col){
//        for (int k = 0; k < this.k; k++) {
////            Q[i][k] = Q[i][k] + alpha * (2 * eij * P[k][j] - beta * Q[i][k])
////            P[k][j] = P[k][j] + alpha * (2 * eij * Q[i][k] - beta * P[k][j])
//            double Qupdate = Q.getEntry(row,k) + (learningRate * ((2.0 * diff * P.getEntry(k,col)) - (lambda * Q.getEntry(row,k))));
//            double Pupdate = P.getEntry(k,col) + (learningRate * ((2.0 * diff * Q.getEntry(row,k)) - (lambda * P.getEntry(k,col))));
////            diff = -2.0*diff;
////            double Qupdate = Q.getEntry(row,k) - learningRate*(diff*P.getEntry(k,col) + 2.0*lambda*Q.getEntry(row,k));
////            double Pupdate = P.getEntry(k,col) - (diff*Q.getEntry(row,k) + 2.0*lambda*P.getEntry(k,col));
//            this.Q.setEntry(row,k,Qupdate);
//            this.P.setEntry(k,col,Pupdate);
//
//
//        }
//    }
}

