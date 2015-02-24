import java.util.Date;


public class Job {

	private Date orderTime; 
	private Date dueTime;
	private String jobType;
	private Location startLocation;
	private Location targetLocation;
	
	public Job(Date orderTime, Date dueTime, String jobType, Location startLocation, Location targetLocation){
		this.orderTime = orderTime;
		this.dueTime = dueTime;
		this.jobType = jobType;
		this.startLocation = startLocation;
		this.targetLocation = targetLocation;
		
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
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
	
	public float getJobExecutionTime(){
		
	}
	
	public float getJobTime(){
		//travelTime + executionTime
		float travelTime = startLocation.getTravelTimeTo(targetLocation);
		
		return travelTime;
	}
}
	
