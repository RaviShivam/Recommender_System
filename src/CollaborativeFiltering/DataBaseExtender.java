package CollaborativeFiltering;

import Reader.Movie;
import Reader.MovieList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by shivam on 5-3-16.
 */
public class DataBaseExtender {
    Database database;
    public DataBaseExtender() throws IOException, ClassNotFoundException {
        database = new Database();
    }
    public void undef() throws IOException, ParseException {
        MovieList movielist= database.getMovieList();
//        System.out.println(movielist.get(18).getTitle());
//        extendMovie(movielist.get(18));
        for (int i = 0; i < movielist.size(); i++) {
            movielist.set(i,extendMovie(movielist.get(i)));
            System.out.println(movielist.get(i));
        }
        Computations.serializeItem(movielist, "objects/movies.ser");

    }
    public String getURLTitle(String string){
        StringBuilder builder = new StringBuilder();
        String[] title = string.split("_");
        for (int i = 0; i < title.length; i++) {
            if(title[i].charAt(0)== '(' && title[i].charAt(title[i].length()-1)== ')'){
                if(isInteger(title[i].substring(1,title[i].length()-2))){
                    continue;
                }
            }
            builder.append(title[i] + "+");
        }
        return builder.toString().substring(0,builder.toString().length()-1);
    }

    ArrayList<Movie> wrong = new ArrayList<>();

    public Movie extendMovie(Movie movie) throws IOException, ParseException {
        String title = getURLTitle(movie.getTitle());
        URL url = new URL(createURL(title));
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
        JSONObject movieObject = (JSONObject) parser.parse(line);
        if((String)movieObject.get("Error")!=null){
            System.out.println("movie not found: " + movie.getIndex()+ " " + movie.getTitle());
            return movie;
        }
        movie.setTitle((String)movieObject.get("Title"));
        movie.setYear(Integer.parseInt((String) movieObject.get("Year")));
        String content = (String) movieObject.get("Rated");
        if(content.equals("N/A")){
            wrong.add(movie);
            return movie;
        }
        movie.setContent(content);
        Scanner scan = new Scanner((String) movieObject.get("Runtime"));
        String time = scan.next();
        if(!isInteger(time)){
            wrong.add(movie);
            return movie;
        }
        movie.setRuntime(Integer.parseInt(time));
        scan.close();
        String[] gstrings = ((String) movieObject.get("Genre")).split(", ");
        if(gstrings[0].equals("N/A")){
            wrong.add(movie);
            return movie;
        }
        ArrayList<String> genre = new ArrayList<>(Arrays.asList(gstrings));
        movie.setGenre(genre);
        String[] dstrings = ((String) movieObject.get("Director")).split(", ");
        if(dstrings[0].equals("N/A")){
            wrong.add(movie);
            return movie;
        }
        ArrayList<String> directors = new ArrayList<>(Arrays.asList(dstrings));
        movie.setDirector(directors);
        String[] astrings = ((String) movieObject.get("Actors")).split(", ");
        if(astrings[0].equals("N/A")){
            wrong.add(movie);
            return movie;
        }
        ArrayList<String> actors =new ArrayList<>(Arrays.asList(astrings));
        movie.setActors(actors);
        return movie;
    }

    public static  void main(String[] args) throws IOException, ParseException, ClassNotFoundException {
        //Test codes here
        DataBaseExtender extender = new DataBaseExtender();
        extender.undef();
    }


    public static String createURL(String name){
        StringBuilder builder = new StringBuilder();
        builder.append("http://www.omdbapi.com/?t=");
        builder.append(name);
        builder.append("&y=&plot=short&r=json");
        return builder.toString();
    }

    public boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
