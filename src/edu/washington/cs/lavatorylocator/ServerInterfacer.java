package edu.washington.cs.lavatorylocator;

/**
 * The ServerInterfacer is a class that handles the interface between the UI
 * and data types of the app and the server. It sends the queries to the server
 * and processes the results.
 * 
 * @author Wil
 *
 */
class ServerInterfacer {
    //TODO: implement
    static void bathroomSearch(String bldgName, String bathroomName,
            String floor, Coordinates userLocation,
            Coordinates searchLocation, int maxDist, String category) {
        
    }
    
    //TODO: implement
    static void getReviews(int bathroomID, int pageNo,
            String category) {
        
    }
    
    //TODO: implement
    static void requestAdd(Bathroom bathroom, int userID) {
        
    }
    
    //TODO: implement
    static void requestUpdate(int bathroomID,
            Bathroom newBathroom, int userID) {
        
    }
    
    //TODO: implement
    static void requestDelete(int bathroomID, int userID) {
        
    }
    
    //TODO: implement
    static void updateReview(int userID, int bathroomID, int rating,
            String review) {
        
    }
    
    //TODO: implement
    static void markAsHelpful(int userID, int reviewID) {
        
    }
}
