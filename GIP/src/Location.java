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
	private String name;
	private String address;
	
	/**
	 * @param ID
	 * @param name
	 * @param address
	 */
	public Location(int ID, String name, String address){
		this.ID = ID;
		this.name = name;
		this.address = address;		
	}


	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
