package CollaborativeFiltering;

import java.io.*;

/**
 * Created by ravishivam on 4-3-16.
 */
public class Computations {

    public static void serializeItem(Object object, String infile) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(infile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(object);
        out.close();
        fileOut.close();
        System.out.println("Object saved succesfully!");
    }

    public static Object getSerializedItem(String infile) throws IOException, ClassNotFoundException {
        Object e = null;
        FileInputStream fileIn = new FileInputStream(infile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = in.readObject();
        in.close();
        fileIn.close();
        return e;
    }

    public static double round(double d) {
        double absD = (int) d;
        double rest = Math.abs(absD-d);
        if(rest<0.5) {
            return rest <= 0.1 ? absD : d;
        }
        return rest >= 0.1 ? (absD+1) : d;

    }
    public static  void main(String[] args){
        //Test codes here.
    }
}
