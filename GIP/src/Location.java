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
	private String address;
	
	/**
	 * @param ID
	 * @param address
	 */
	public Location(int ID, String address){
		this.ID = ID;
		this.address = address;	
	}	

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
