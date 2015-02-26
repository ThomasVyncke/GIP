import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AVC extends Location {

	private double timeOpening;
	private double timeClosing;
	private ArrayList<String> wasteType; 
	
	
	/**
	 * @param ID
	 * @param address
	 * @param timeStart
	 * @param timeStop
	 * @param wasteType: Type of waste processed in the AVC = {general, paper, buildMaterial, dangerousWaste}
	 */
	public AVC(int ID, float latitude, float longitude, double timeStart, double timeStop, ArrayList<String> wasteType) {
		super(ID, latitude, longitude);
		this.timeOpening = timeStart;
		this.timeClosing = timeStop;
		this.wasteType = wasteType;		
		this.additionalTime = 0;
	}


	public double getTimeStart() {
		return timeOpening;
	}


	public void setTimeStart(double timeStart) {
		this.timeOpening = timeStart;
	}


	public double getTimeStop() {
		return timeClosing;
	}


	public void setTimeStop(double timeStop) {
		this.timeClosing = timeStop;
	}


	public ArrayList<String> getWasteType() {
		return wasteType;
	}


	public void setWasteType(ArrayList<String> wasteType) {
		this.wasteType = wasteType;
	}

}
