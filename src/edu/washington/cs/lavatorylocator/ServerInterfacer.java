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
	protected static void bathroomSearch(String bldgName, String bathroomName,
									 int floor, Coordinates userLocation,
									 Coordinates searchLocation, int maxDist) {
		
	}
	
	//TODO: implement
	protected static void getReviews(int bathroomID, int pageNo,
									 String category) {
		
	}
	
	//TODO: implement
	protected static void requestAdd(BathroomData bathroom, int userID) {
		
	}
	
	//TODO: implement
	protected static void requestUpdate(int bathroomID,
										BathroomData newBathroom, int userID) {
		
	}
	
	//TODO: implement
	protected static void requestDelete(int bathroomID, int userID) {
		
	}
	
	//TODO: implement
	protected static void updateReview(int userID, int bathroomID, int rating,
									   String review) {
		
	}
	
	//TODO: implement
	protected static void markAsHelpful(int userID, int reviewID) {
		
	}
}
