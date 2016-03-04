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

    public static int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }
}
