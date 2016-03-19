package latent_factor_model;

import collaborative_filter.Computations;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.io.IOException;

/**
 * Created by ravishivam on 19-3-16.
 */
public class Predictor_latentFactor {
    double learningRate = 0.05;
    LatentFactorDatabase latentFactorDatabase;
    Array2DRowRealMatrix completeUtility;
    SingularValueDecomposition svd;
    BlockRealMatrix Q;
    BlockRealMatrix P;


    public Predictor_latentFactor() throws IOException, ClassNotFoundException {
        latentFactorDatabase = new LatentFactorDatabase();
        completeUtility = latentFactorDatabase.getCompleteUtility();
        svd = new SingularValueDecomposition(latentFactorDatabase.getTrainUtility());
        svd.getSolver().solve(completeUtility);
        Q = (BlockRealMatrix) svd.getU();
        P = (BlockRealMatrix) svd.getS().multiply(svd.getVT());
    }

    public void trainLatenFactorModel(){
        BlockRealMatrix product = Q.multiply(P);
        for (int i = 0; i < product.getRowDimension(); i++) {
            for (int j = 0; j < product.getColumnDimension(); j++) {
              double ratingdiff = completeUtility.getEntry(i,j) - product.getEntry(i,j);
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Predictor_latentFactor pr = new Predictor_latentFactor();
        System.out.println(pr.getQ().getRowDimension());
        System.out.println(pr.getQ().getColumnDimension());
        System.out.println(pr.getP().getRowDimension());
        System.out.println(pr.getP().getColumnDimension());

    }

    public BlockRealMatrix getQ() {
        return Q;
    }

    public BlockRealMatrix getP() {
        return P;
    }
}

