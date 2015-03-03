
public class Client extends Location {

	private String name;
	private int containerCapacity;
	private String wasteType;
	private Boolean sameContainer;
	private Container containerAtClient;
	private float stopTime;
	private float startTime;
	
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
	public Client(int ID, String name, float startTime, float stopTime, float latitude, float longitude, String wasteType, Boolean sameContainer, float additionalTime, int containerCapacity){
		super(ID, latitude, longitude);
		this.wasteType = wasteType;
		this.sameContainer = sameContainer;
		this.additionalTime = additionalTime;
		this.containerCapacity = containerCapacity;
		this.startTime = startTime;
		this.stopTime = stopTime; 
	}
	
	public Client(int ID, String name, float latitude, float longitude, String wasteType, Boolean sameContainer, float additionalTime, Container startContainer, int containerCapacity){
		super(ID, latitude, longitude);
		this.wasteType = wasteType;
		this.sameContainer = sameContainer;
		this.additionalTime = additionalTime;
		this.addContainer(startContainer);
		this.containerCapacity = containerCapacity;
	}
	
	public float getStopTime() {
		return stopTime;
	}

	public void setStopTime(float stopTime) {
		this.stopTime = stopTime;
	}

	public float getStartTime() {
		return startTime;
	}

	public void setStartTime(float startTime) {
		this.startTime = startTime;
	}

	public int getContainerCapacity() {
		return containerCapacity;
	}

	public void setContainerCapacity(int containerCapacity) {
		this.containerCapacity = containerCapacity;
	}

	public void addContainer(Container container){
		this.containerAtClient = container;
	}
	
	public Container getContainer(){
		return containerAtClient;
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
