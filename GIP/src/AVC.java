import java.util.Date;


public class AVC extends Location {

	private Date timeStart;
	private Date timeStop;
	private String wasteType; 
	
	
	/**
	 * @param ID
	 * @param address
	 * @param timeStart
	 * @param timeStop
	 * @param wasteType: Type of waste processed in the AVC = {general, paper, buildMaterial, dangerousWaste}
	 */
	public AVC(int ID, String address, Date timeStart, Date timeStop, String wasteType) {
		super(ID, address);
		this.timeStart = timeStart;
		this.timeStop = timeStop;
		this.wasteType = wasteType;		
	}

}
