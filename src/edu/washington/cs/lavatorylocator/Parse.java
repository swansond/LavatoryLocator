package edu.washington.cs.lavatorylocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import edu.washington.cs.lavatorylocator.RESTLoader.RESTResponse;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.ReviewData;

/**
 * 
 * @author David Swanson
 *
 *
 * The Parse class contains the JSON parsing logic for the application.
 */
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
    public static List<ReviewData> reviewList(JSONObject result) throws JSONException {
        List<ReviewData> output = new ArrayList<ReviewData>();
        JSONArray resultArray = result.getJSONArray("reviews");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = resultArray.getJSONObject(i);
            
            int review_id = obj.getInt("rid");
            int lavatory_id = obj.getInt("lid");
            int user_id = obj.getInt("uid");
            String reviewText = obj.getString("review");
            int rating = obj.getInt("rating");
            // TODO add the missing fields from the JSON to the ReviewData object.
            
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
    public static List<LavatoryData> lavatoryList(JSONObject result) throws JSONException {
        List<LavatoryData> output = new ArrayList<LavatoryData>();
        JSONArray resultArray = result.getJSONArray("lavatories");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject obj = resultArray.getJSONObject(i);

            int lav_id = obj.getInt("lid");
            char lav_gender = obj.getString("type").charAt(0);
            String building = obj.getString("building");
            String floor = "1"; // TODO add this to the JSON
            String roomNum = obj.getString("room");
            double longitude = obj.getDouble("longitude");
            double latitude = obj.getDouble("latitude");
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
    public static JSONObject readJSON(RESTResponse resp) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getData().getContent(), "UTF-8"));
        String json = reader.readLine();
        JSONTokener tokener = new JSONTokener(json);
        return new JSONObject(tokener);
        
    }
    
}
