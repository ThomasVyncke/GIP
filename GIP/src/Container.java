
public class Container {

	private int ID;
	private int capacity;
	private Location containerLocation;
	private boolean empty;
	private String wasteType;
	private Client client = null;

	public Container(int id, int capacity, Location containerLocation, boolean isEmpty, String wasteType){
		this.ID = id;
		this.capacity = capacity;
		this.containerLocation = containerLocation;
		this.empty = empty;
		this.wasteType = wasteType;
	}
	
	public Container(int id, int capacity, Location containerLocation, boolean isEmpty, String wasteType, Client client){
		this.ID = id;
		this.capacity = capacity;
		this.containerLocation = containerLocation;
		this.empty = empty;
		this.wasteType = wasteType;
		this.setClient(client);
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getWasteType() {
		return wasteType;
	}
	
	public void setWasteType(String wasteType) {
		this.wasteType = wasteType;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Location getContainerLocation() {
		return containerLocation;
	}

	public void setContainerLocation(Location containerLocation) {
		this.containerLocation = containerLocation;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}