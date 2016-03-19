package latent_factor_model;

import collaborative_filter.Database;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.io.IOException;

/**
 * Created by ravishivam on 19-3-16.
 */
public class Predictor_latentFactor {
    Database database;
    LatentFactorDatabase latentFactorDatabase;
    Array2DRowRealMatrix utilityMatrix;
    SingularValueDecomposition svd;

    public Predictor_latentFactor() throws IOException, ClassNotFoundException {
        latentFactorDatabase = new LatentFactorDatabase();
        utilityMatrix = latentFactorDatabase.getUtility();
        svd = new SingularValueDecomposition(utilityMatrix);
    }
    //
}

