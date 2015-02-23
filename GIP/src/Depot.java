
public class Depot extends Location {

	private int emptyContainers;
	private int fullContainers;
	private int amountOfTrucks;
	


	public Depot(int ID, String address, int emptyContainers, int fullContainers, int amountOfTrucks) {
		super(ID, address);
		this.emptyContainers = emptyContainers;
		this.fullContainers = fullContainers;
		this.amountOfTrucks = amountOfTrucks;
	}

	public int getEmptyContainers() {
		return emptyContainers;
	}


	public void setEmptyContainers(int emptyContainers) {
		this.emptyContainers = emptyContainers;
	}


	public int getFullContainers() {
		return fullContainers;
	}


	public void setFullContainers(int fullContainers) {
		this.fullContainers = fullContainers;
	}
	
	public int getAmountOfTrucks() {
		return amountOfTrucks;
	}

	public void setAmountOfTrucks(int amountOfTrucks) {
		this.amountOfTrucks = amountOfTrucks;
	}


}
