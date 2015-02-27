
public class Depot extends Location {

	private int emptyContainers;
	private int fullContainers;
	private int amountOfTrucks;
	


	public Depot(int ID, float latitude, float longitude) {
		super(ID, latitude, longitude);
		this.additionalTime = 0;
	}


}
