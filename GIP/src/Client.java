
public class Client extends Location {

	private String name;
	private String wasteType;
	private Boolean sameContainer;
	


	// TODO containerLocation een meer zinvolle inhoud geven dan String kan.
	/**
	 * @param ID
	 * @param name
	 * @param address
	 * @param wasteType
	 * @param sameContainer: expresses the special needs of a client. 
	 * @param containerLocation: expresses the specific location of a container.
	 * @param additionalTime: client specific additional time needed. 
	 * 
	 */
	public Client(int ID, String name, float latitude, float longitude, String containerLocation, String wasteType, Boolean sameContainer, float additionalTime){
		super(ID, latitude, longitude);
		this.wasteType = wasteType;
		this.sameContainer = sameContainer;
		this.additionalTime = additionalTime;
	}
		
	public String getWasteType() {
		return wasteType;
	}


	public void setWasteType(String wasteType) {
		this.wasteType = wasteType;
	}


	public Boolean getSameContainer() {
		return sameContainer;
	}


	public void setSameContainer(Boolean sameContainer) {
		this.sameContainer = sameContainer;
	}
	
	public float getAdditionalTime() {
		return additionalTime;
	}


	public void setAdditionalTime(float additionalTime) {
		this.additionalTime = additionalTime;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
