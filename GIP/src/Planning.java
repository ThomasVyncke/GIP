import java.util.ArrayList;
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
	private static HashMap<Integer,Client> clients = new HashMap<Integer, Client>();
	private static HashMap<Integer,AVC> avcs = new HashMap<Integer, AVC>();
	
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
	
	public void putClient(Client client){
		Integer size = clients.size();
		clients.put(size+1,client);		
	}
	
	public void putAVC(AVC avc){
		Integer size = avcs.size();
		avcs.put(size+1,avc);		
	}
	
	/**
	 * Evt. nog een test voor timewindows invoeren.
	 * @param container
	 * @return
	 */
	public static AVC getClosestAVC(Container container){
		String containerWasteType = container.getWasteType();
		ArrayList<AVC> suitedAVCS = new ArrayList<AVC>();
		//Make a list with the AVCs which are able to process the clients type of waste.
		for(int i = 0;i<avcs.size()-1;i++){
			ArrayList<String> avcTypes = avcs.get(i).getWasteType();
			if(avcTypes.contains(containerWasteType)){
				suitedAVCS.add(avcs.get(i));
			}
		}
		float closestDistance = (float) Double.POSITIVE_INFINITY;
		
		AVC closestAVC = suitedAVCS.get(0);
		for(int j = 0;j<suitedAVCS.size()-1;j++){
			float dist = container.getContainerLocation().getDistanceTo(suitedAVCS.get(j));
			if(dist < closestDistance){
				closestDistance = dist;
				closestAVC = suitedAVCS.get(j);
			}
		}
		return closestAVC;		
	}
	
}
