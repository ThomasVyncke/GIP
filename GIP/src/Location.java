import java.util.Collection;
import java.util.HashMap;

/**
 * Deze klasse modelleert verschillende geografische locaties.
 */

/**
 * @author Thomas
 * @version 1.0
 * @since 22/02/2015
 *  
 * 
 */
public class Location {
	private int ID;
	private float latitude;
	private float longitude;
	protected float additionalTime;
	

	/**
	 * @param ID
	 * @param address
	 */
	public Location(int ID, float latitude, float longitude){
		this.ID = ID;
		this.latitude = latitude;
		this.longitude= longitude;
	}	

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public float getAdditionalTime() {
		return additionalTime;
	}

	public void setAdditionalTime(float additionalTime) {
		this.additionalTime = additionalTime;
	}
	
	/**
	 * Method to determine distance to another Location object. 
	 * @param location: Location to which distance has to be calculated.
	 * @return straightline distance in metres.
	 */
	public float getDistanceTo(Location location){
		float latitude1 = this.getLatitude();
		float longitude1 = this.getLongitude();
		float latitude2 = location.getLatitude();
		float longitude2 = location.getLongitude();
		float distance = distFrom(latitude1, longitude1, latitude2, longitude2);
		return distance;
	}
	
	// TO-DO 
	// http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
	    double earthRadius = 6371000; //meters
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    float dist = (float) (earthRadius * c);

	    return dist;
	    }
	
	
	/**
	 * 
	 * This method serves to calculate travel time between two locations. 
	 * Several assumptions are made:
	 * - There is a fixed ration between straight line distance and road distance: distanceRatio (see Planning class).
	 * - There is a fixed average speed of the trucks: averageSpeed (see Planning class). 
	 * @param location
	 * @return
	 */
	public float getTravelTimeTo(Location location){
		float distance = getDistanceTo(location);
		float travelTime = (float) (distance*Planning.distanceRatio/Planning.averageSpeed);
		return travelTime;
	}
}
