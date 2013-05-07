package edu.washington.cs.lavatorylocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static List<ReviewData> reviewList(JSONArray finalResult) throws JSONException {
        List<ReviewData> output = new ArrayList<ReviewData>();
        for (int i = 0; i < finalResult.length(); i++) {
            JSONObject obj = finalResult.getJSONObject(i);
            
            int review_id = obj.getInt("review_id");
            int lavatory_id = obj.getInt("lavatory_id");
            int user_id = obj.getInt("user_id");
            String reviewText = obj.getString("review");
            int rating = obj.getInt("rating");
            
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
    public static List<LavatoryData> lavatoryList(JSONArray finalResult) throws JSONException {
        List<LavatoryData> output = new ArrayList<LavatoryData>();
        for (int i = 0; i < finalResult.length(); i++) {
            JSONObject obj = finalResult.getJSONObject(i);

            int lav_id = obj.getInt("lavatoryID");
            char lav_gender = (char)obj.getInt("lavatoryGender");
            String building = obj.getString("building");
            String floor = obj.getString("floor");
            String roomNum = obj.getString("roomNumber");
            double longitude = obj.getDouble("longitude");
            double latitude = obj.getDouble("latitude");
            int numReviews = obj.getInt("numReviews");
            double rating = obj.getDouble("avgRating");
            
            LavatoryData lav = new LavatoryData(lav_id, lav_gender, building, floor, roomNum, longitude, latitude, numReviews, rating);
            output.add(lav);
        }
        return output;
    }
    
    /**
     * Parses an HttpResponse to a JSONArray by reading in the data
     * @param resp the response to parse
     * @return a JSONArray with the proper elements
     * @throws IOException if the reading fails
     * @throws JSONException if the json part fails
     */
    public static JSONArray readJSON(HttpResponse resp) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        JSONTokener tokener = new JSONTokener(json);
        return new JSONArray(tokener);
    }
    
}
