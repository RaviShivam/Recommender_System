//package latent_factor_model;
//
//import Jama.Matrix;
//import org.apache.commons.math3.linear.Array2DRowRealMatrix;
//import org.apache.commons.math3.linear.SingularMatrixException;
//import org.apache.commons.math3.linear.SingularValueDecomposition;
//
///**
// * Created by ravishivam on 19-3-16.
// */
//public class Test_svd {
//    public static void main(String[] args) {
//        Matrix matrix = new Matrix(7,5);
//        matrix.set(0,0,1);
//        matrix.set(0,1,1);
//        matrix.set(0,2,1);
//        matrix.set(0,3,0);
//        matrix.set(0,4,0);
//
//        matrix.set(1,0,3);
//        matrix.set(1,1,3);
//        matrix.set(1,2,3);
//        matrix.set(1,3,0);
//        matrix.set(1,4,0);
//
//        matrix.set(2,0,4);
//        matrix.set(2,1,4);
//        matrix.set(2,2,4);
//        matrix.set(2,3,0);
//        matrix.set(2,4,0);
//
//        matrix.set(3,0,5);
//        matrix.set(3,1,5);
//        matrix.set(3,2,5);
//        matrix.set(3,3,0);
//        matrix.set(3,4,0);
//
//        matrix.set(4,0,0);
//        matrix.set(4,1,2);
//        matrix.set(4,2,0);
//        matrix.set(4,3,4);
//        matrix.set(4,4,4);
//
//        matrix.set(5,0,0);
//        matrix.set(5,1,0);
//        matrix.set(5,2,0);
//        matrix.set(5,3,5);
//        matrix.set(5,4,5);
//
//        matrix.set(6,0,0);
//        matrix.set(6,1,1);
//        matrix.set(6,2,0);
//        matrix.set(6,3,2);
//        matrix.set(6,4,2);
//
//        Jama.SingularValueDecomposition decomposition = new Jama.SingularValueDecomposition(matrix);
//        System.out.println(decomposition.getU().getColumnDimension());
//        System.out.println(decomposition.getU().getRowDimension());
//        System.out.println(decomposition.getS().getColumnDimension());
//        System.out.println(decomposition.getS().getRowDimension());
//        System.out.println(decomposition.getV().getColumnDimension());
//        System.out.println(decomposition.getV().getRowDimension());
//        System.out.println(decomposition.getS().get(0,0));
//    }
//}
