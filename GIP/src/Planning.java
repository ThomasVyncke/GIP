import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class Planning {
	//unit: m/s (equivalent to 50km/h)
	public static final double averageSpeed = 19.44;
	public static final double distanceRatio = 1.5;
	private Date planningDate;
	private String author;
	private HashMap<Integer,Job> completedJobs;
	private HashMap<Integer,Job> idleJobs;
	
	/**
	 * @param planningDate
	 * @param author
	 * @param jobs: HashMap containing all the jobs that need to be processed during the planningDate.
	 */
	public Planning(Date planningDate, String supervisor, HashMap<Integer, Job> jobs){
		this.planningDate = planningDate;
		this.author = supervisor;
		this.idleJobs = jobs;
	}
	
	/**
	 * 
	 * job1 :Klant A --> AVC
	 * job2: Klant B --> AVC
	 * 
	 * Tijdsparen berekenen tussen elke start en stoplocatie.
	 * Bovendien ook tussen elke stoplocatie en startlocatie van een andere job. 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @return
	 */
		
	
	public HashMap<Integer, Float> pairTimes(){
		HashMap<Integer, Float> pairTimes = new HashMap<Integer, Float>();
		
		for(int i=0; i<idleJobs.size(); i++){
			Job job = idleJobs.get(i);
			float time = job.getJobTime();
			pairTimes.put(i, time);		
		}
		return pairTimes;			
	}
		
		
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getPlanningDate() {
		return planningDate;
	}
	
	public void setPlanningDate(Date planningDate) {
		this.planningDate = planningDate;
	} 
	
	
}
