package Reader;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;


public class MovieList extends ArrayList<Movie> {
    
    // Reads in a file with movies data
    public void readFile(String filename) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filename));
            while((line = br.readLine()) != null) {
                String[] movieData = line.split(";");
                add(Integer.parseInt(movieData[0]) - 1,
                    new Movie(Integer.parseInt(movieData[0]),
                              Integer.parseInt(movieData[1]),
                              movieData[2]));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // Reads in a file with movies data
    public void readFileDataBaseExtended(String filename) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filename));
            while((line = br.readLine()) != null) {
                String[] movieData = line.split("::");
                add(Integer.parseInt(movieData[0]) - 1,
                        new Movie(Integer.parseInt(movieData[0]),
                                20,
                                movieData[2]));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

