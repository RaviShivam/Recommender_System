package Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RatingList extends ArrayList<Rating> {

	// Reads in a file with rating data
	public void readFile(String filename, UserList userList, MovieList movieList) {
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				String[] ratingData = line.split(";");
				if (ratingData.length == 3) {
					add(new Rating(
							userList.get(Integer.parseInt(ratingData[0]) - 1),
							movieList.get(Integer.parseInt(ratingData[1]) - 1),
							Double.parseDouble(ratingData[2])));
				} else {
					add(new Rating(
							userList.get(Integer.parseInt(ratingData[0]) - 1),
							movieList.get(Integer.parseInt(ratingData[1]) - 1),
							0.0));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Writes a result file
	public void writeResultsFile(String filename) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(filename);
			pw.println("Id,Rating");
			for (int i = 0; i < size(); i++) {
				pw.println((i + 1) + "," + get(i).getRating());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Convertes ArrayList of ratings to 2D HashMap so that
	 * outer map key equals to movieId and
	 * inner map key equals to userId
	 * @return rating as hashmap
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getMovieToUserHashMap(){
		HashMap<Integer, HashMap<Integer, Double>> map = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> bucket;
		int userId;
		int movieId;

		for(Rating r: this){
			userId = r.getUser().getIndex();
			movieId = r.getMovie().getIndex();
			bucket = map.get(movieId);
			if(bucket == null)
				bucket = new HashMap<Integer, Double>();
			bucket.put(userId, r.getRating());
			map.put(movieId, bucket);
		}

		return map;
	}
	/**
	 * Convertes ArrayList of ratings to 2D HashMap so that
	 * outer map key equals to userId and
	 * inner map key equals to movieId
	 * @return rating as hashmap
	 */
	public HashMap<Integer, HashMap<Integer, Double>> getUserToMovieHashMap(){
		HashMap<Integer, HashMap<Integer, Double>> map = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> bucket;
		Integer userId;
		for(Rating r: this){
			userId = r.getUser().getIndex();
			bucket = map.get(userId);
			if(bucket == null)
				bucket = new HashMap<Integer, Double>();
			bucket.put(r.getMovie().getIndex(), r.getRating());
			map.put(userId, bucket);
		}

		return map;
	}
}
