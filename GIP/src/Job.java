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

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
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
		float travelTime = startLocation.getTravelTimeTo(targetLocation);
		float executionTime = this.getJobExecutionTime();
		float additionalTime = this.targetLocation.getAdditionalTime();
		float jobTime = travelTime + executionTime + additionalTime;
		return jobTime;
	}
}
	
