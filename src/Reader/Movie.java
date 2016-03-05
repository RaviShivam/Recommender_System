package Reader;


import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
    
    int index, year, runtime;
    String title, content;
    ArrayList<String> genre, director, actors;

    public void setYear(int year) {
        this.year = year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "index=" + index +
                ", year=" + year +
                ", runtime=" + runtime +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", genre=" + genre +
                ", director=" + director +
                ", actors=" + actors +
                '}';
    }

    public void setIndex(int index) {

        this.index = index;
    }

    public Movie(int _index, int _year, String _title) {
        this.index = _index;
        this.year  = _year;
        this.title = _title;
        this.runtime = 0;
        this.content ="";
        this.genre = null;
        this.director = null;
        this.actors = null;
    }

    public void setActors(ArrayList<String> actors) {
        this.actors = actors;
    }

    public void setDirector(ArrayList<String> director) {
        this.director = director;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }
    
    public int getYear() {
        return year;
    }
    
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public ArrayList<String> getDirector() {
        return director;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public int getRuntime() {

        return runtime;
    }

    public void setRuntime(int runtime) {

        this.runtime = runtime;
    }
}

