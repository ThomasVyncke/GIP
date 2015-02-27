import java.util.Date;


/**
 * @author Thomas
 *
 */
public class Job {

	private double orderTime; 
	private double dueTime;
	private String jobType;
	private Location startLocation;
	private Location targetLocation = null;
	private Container container = null; 
	
	
	public Job(String jobType, Location startLocation, Container container){
//		this.orderTime = orderTime;
//		this.dueTime = dueTime;
		this.jobType = jobType;
		this.startLocation = startLocation;	
		this.generateTargetLocation();
		if(container != null){
			container.setContainerLocation(startLocation);
		}
	}
	
	public Job(String jobType, Location startLocation, Location targetLocation, Container container){
//		this.orderTime = orderTime;
//		this.dueTime = dueTime;
		this.jobType = jobType;
		this.startLocation = startLocation;
		this.targetLocation = targetLocation;	
		if(container != null){
			container.setContainerLocation(targetLocation);
		}
	}
	
	public void generateTargetLocation(){
		//EmptyAVC, LoadOnTruck, PlaceClient, LoadPlaceDepot, FillContainer, SwitchContainer
		Location startLocation = this.getStartLocation();
		if(this.jobType.equals("EmptyAVC")){
			if(startLocation instanceof Client){
				container = startLocation.getContainer().get(0);
				AVC avc = Planning.getClosestAVC(container);
				this.setTargetLocation(avc);
			}
			else{
				throw new NullPointerException("an emptyAVC job its startlucation must be a client");
				}
		}
		if(this.jobType.equals("LoadOnTruck")){
			this.setTargetLocation(null);
		}
		if(this.jobType.equals("PlaceClient")){
			//brengt in rekening dat sommige klanten altijd zelfde container nodig hebben. dus container bevat klant.
			if(container.getClient() != null){
				this.setTargetLocation(container.getClient());
			}		
			if(this.getTargetLocation() == null){
				throw new NullPointerException("there can be no job of type placeclient without targetlocation or client attached");
			}
		}
		if(this.jobType.equals("LoadPlaceDepot")){
			if(this.getStartLocation() instanceof Depot){
				this.setTargetLocation(null);
			}
			else{
				this.setTargetLocation(Planning.depot);
			}
		}		
		if(this.jobType.equals("FillContainer")){	
			if(this.getTargetLocation() == null)
				throw new NullPointerException("there can be no job of type fillcontainer without targetlocation");
		}
		if(this.jobType.equals("SwitchContainer")){
			if(this.getTargetLocation() == null)
				throw new NullPointerException("there can be no job of type fillcontainer without targetlocation");
		}
			
	}	

//	public double getOrderTime() {
//		return orderTime;
//	}
//
//	public void setOrderTime(double orderTime) {
//		this.orderTime = orderTime;
//	}

	public Location getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	public Location getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(Location targetLocation) {
		this.targetLocation = targetLocation;
	}

	public double getDueTime() {
		return dueTime;
	}

	public void setDueTime(double dueTime) {
		this.dueTime = dueTime;
	}

	public String getJobType() {
		return jobType;
	}

	/**
	 * @param jobType: {EmptyAVC, LoadOnTruck, PlaceClient, LoadPlaceDepot, FillContainer, SwitchContainer
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public float getJobExecutionTime() throws NullPointerException{
		String jobType = this.getJobType();
		if (jobType.equals("EmptyAVC")){
			return 20;
		}
		if (jobType.equals("LoadOnTruck")){
			return 12;
		}
		if (jobType.equals("PlaceClient")){
			return 12;
		}
		if (jobType.equals("LoadPlaceDepot")){
			return 6;
		}
		if (jobType.equals("FillContainer")){
			return 30;
		}
		if (jobType.equals("SwitchContainer")){
			return 25;
		}
		else throw new NullPointerException("non-specified execution time for jobType. please specify in code.");		
	}
	
	public float getJobTime() throws NullPointerException{
		//travelTime + executionTime + client specific additionalTime
		
		Location target = this.getTargetLocation();
		float travelTime;
		if(target != null)
			travelTime = getStartLocation().getTravelTimeTo(target);			
		else
			travelTime = 0;
			
		float executionTime = this.getJobExecutionTime();
		float additionalTime = this.targetLocation.getAdditionalTime();
		float jobTime = travelTime + executionTime + additionalTime;
		return jobTime;
	}
}
	
