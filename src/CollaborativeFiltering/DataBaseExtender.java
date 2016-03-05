package CollaborativeFiltering;

import Reader.Movie;
import Reader.MovieList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
//        System.out.println(movielist.get(2131).getTitle());
//        extendMovie(movielist.get(2131));
        for (int i = 0; i < movielist.size(); i++) {
            movielist.set(i,extendMovie(movielist.get(i)));
            System.out.println(movielist.get(i));
        }
        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(out);
        System.out.println("Mismatch: " + wrong.size());
        Computations.serializeItem(wrong, "objects/mismatch.ser");
        Computations.serializeItem(movielist, "objects/movies.ser");

    }
    public String getURLTitle(String string) throws IOException, ParseException {
        StringBuilder builder = new StringBuilder();
        boolean openb = false;
        if(string.contains("")){
            string = replaceWeirdCharacter(string);
        }
        String[] title = string.split("_");
        for (int i = 0; i < title.length; i++) {
            if(title[i].length()==0){
                continue;
            }
            if(title[i].contains("")){
                continue;
            }
            if(title[i].charAt(0)== '(' && title[i].charAt(title[i].length()-1)== ')'){
//                if(isInteger(title[i].substring(1,title[i].length()-2))){
                    continue;
//                }
            }
            if(openb && title[i].charAt(title[i].length()-1)== ')'){
                openb = false;
                continue;
            }
            if(openb) {
                continue;
            }
            if(title[i].charAt(0)== '(' && !(title[i].charAt(title[i].length()-1)== ')')) {
                openb = true;
                continue;
            }
            if(title[i].charAt(title[i].length()-1) == ','){
                builder.append(title[i].substring(0,title[i].length()-1));
                return builder.toString();
            }
            builder.append(title[i] + "+");
        }
        return builder.toString().substring(0,builder.toString().length()-1);
    }

    HashSet<Movie> wrong = new HashSet<>();

    public String replaceWeirdCharacter(String string) throws IOException, ParseException {
        String[] strings = {"","e","y","u","o","i","a"};
        for (int i = 0; i < strings.length; i++) {
            String stringer = new String(string);
            stringer = stringer.replaceAll("", strings[i]);
            String title = getURLTitle(stringer);
            URL url = new URL(createURL(title));
            JSONParser parser = new JSONParser();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            JSONObject movieObject = (JSONObject) parser.parse(line);
            if((String)movieObject.get("Error")==null){
                return stringer;
            }
        }
        return string;
    }
    public Movie extendMovie(Movie movie) throws IOException, ParseException {
        boolean notRight = false;
        String title = movie.getTitle();
        URL url = new URL(createURL(getURLTitle(title)));
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
        JSONObject movieObject = (JSONObject) parser.parse(line);
        if((String)movieObject.get("Error")!=null){
            wrong.add(movie);
            System.out.println("movie not found: " + movie.getIndex()+ " " + movie.getTitle());
            return movie;
        }
        movie.setTitle((String)movieObject.get("Title"));
        String str = ((String) movieObject.get("Year")).replaceAll("[^0-9]+", "");
        movie.setYear(Integer.parseInt(str));
        String content = (String) movieObject.get("Rated");
        if(content.equals("N/A")){
            notRight = true;
        }
        movie.setContent(content);
        Scanner scan = new Scanner((String) movieObject.get("Runtime"));
        String time = scan.next();
        if(!isInteger(time)){
            movie.setRuntime(0000);
            notRight = true;
        }
        else {
            movie.setRuntime(Integer.parseInt(time));
        }
        scan.close();
        String[] gstrings = ((String) movieObject.get("Genre")).split(", ");
        if(gstrings[0].equals("N/A")){
            notRight =true;
            return movie;
        }
        else{
            ArrayList<String> genre = new ArrayList<>(Arrays.asList(gstrings));
            movie.setGenre(genre);
        }
        String[] dstrings = ((String) movieObject.get("Director")).split(", ");
        if(dstrings[0].equals("N/A")){
            notRight=true;
            return movie;
        }
        else{
            ArrayList<String> directors = new ArrayList<>(Arrays.asList(dstrings));
            movie.setDirector(directors);
        }
        String[] astrings = ((String) movieObject.get("Actors")).split(", ");
        if(astrings[0].equals("N/A")){
            notRight = true;
            return movie;
        }
        else{
            ArrayList<String> actors =new ArrayList<>(Arrays.asList(astrings));
            movie.setActors(actors);
        }
        if(notRight){
            wrong.add(movie);
        }
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
