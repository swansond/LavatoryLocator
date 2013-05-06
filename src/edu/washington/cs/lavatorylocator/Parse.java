package edu.washington.cs.lavatorylocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class Parse {
    
    /*
     * Prevents instantiation of this class
     */
    private Parse() {
        
    }

    /**
     * Processes a JSON to get a list of reviews
     * @param finalResult the JSON array to be parsed
     * @return a list of reviews
     * @throws JSONException if one of the operations fails
     */
    public static List<ReviewData> reviewList(String json) throws JSONException {
        JSONTokener tokener = new JSONTokener(json);
        JSONArray resultArray = new JSONArray(tokener);
        List<ReviewData> output = new ArrayList<ReviewData>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = resultArray.getJSONObject(i);
            
            int review_id = 1;
            int lavatory_id = 1;
            int user_id = 1;
            String reviewText = "This bathroom has great atmosphere.";
            int rating = 5;
            
            ReviewData review = new ReviewData(review_id, user_id, lavatory_id, rating, reviewText);
            output.add(review);
        }
        return output;
    }

    /**
     * Processes a JSON to get a list of lavatories
     * @param finalResult the JSON array to be parsed
     * @return a list of lavatories
     * @throws JSONException if one of the operations fails
     */
    public static List<LavatoryData> lavatoryList(String json) throws JSONException {
        JSONTokener tokener = new JSONTokener(json);
        JSONObject finalResult = new JSONObject(tokener);
        List<LavatoryData> output = new ArrayList<LavatoryData>();
        JSONArray resultArray = finalResult.getJSONArray("lavatories");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = resultArray.getJSONObject(i);

            int lav_id = obj.getInt("lid");
            char lav_gender = (char)obj.getString("type").charAt(0);
            String building = obj.getString("building");
            String floor = "1";
            String roomNum = obj.getString("room");
            double longitude = 0;
            double latitude = 0;
            int numReviews = obj.getInt("reviews");
            double rating = obj.getDouble("avgRating");
            
            LavatoryData lav = new LavatoryData(lav_id, lav_gender, building, floor, roomNum, longitude, latitude, numReviews, rating);
            output.add(lav);
        }
        return output;
    }
    
    /**
     * Parses an HttpResponse to a JSONArray by reading in the data
     * @param resp the response to parse
     * @return a JSON string with the data
     * @throws IOException if the reading fails
     */
    public static String readJSON(HttpResponse resp) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
        return reader.readLine();
    }
    
}
