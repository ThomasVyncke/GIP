import java.util.Date;


public class AVC extends Location {

	private Date timeOpening;
	private Date timeClosing;
	private String wasteType; 
	
	
	/**
	 * @param ID
	 * @param address
	 * @param timeStart
	 * @param timeStop
	 * @param wasteType: Type of waste processed in the AVC = {general, paper, buildMaterial, dangerousWaste}
	 */
	public AVC(int ID, float latitude, float longitude, Date timeStart, Date timeStop, String wasteType) {
		super(ID, latitude, longitude);
		this.timeOpening = timeStart;
		this.timeClosing = timeStop;
		this.wasteType = wasteType;		
		this.additionalTime = 0;
	}


	public Date getTimeStart() {
		return timeOpening;
	}


	public void setTimeStart(Date timeStart) {
		this.timeOpening = timeStart;
	}


	public Date getTimeStop() {
		return timeClosing;
	}


	public void setTimeStop(Date timeStop) {
		this.timeClosing = timeStop;
	}


	public String getWasteType() {
		return wasteType;
	}


	public void setWasteType(String wasteType) {
		this.wasteType = wasteType;
	}

}
