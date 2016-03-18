package collaborative_filter;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;

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
            return rest <= 0.01 ? absD : d;
        }
        return rest >= 0.01 ? (absD+1) : d;

    }

    public  static double softround(double prediction){
        if(prediction>5.0){
            return 5.0;
        }
        if(prediction<1.0){
            return 1.0;
        }
        else return prediction;
    }
    public static  void main(String[] args) throws IOException, ParseException {
        URL url = new URL("http://www.omdbapi.com/?t=Frozen&y=&plot=short&r=json");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            builder.append(inputLine);
        in.close();
        String string = builder.toString();
        System.out.println(string);
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(string);
        System.out.println((String)object.get("Title"));
        }//Test codes here
    }
