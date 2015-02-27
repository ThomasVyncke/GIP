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
	private ArrayList<Job> completedJobs;
	private ArrayList<Job> idleJobs;
	//HashMaps containing job times of the 
	private HashMap<Job, Float> fsTimesFirstStage;
	private HashMap<Job, Float> fsTimesSecondStage;
	private HashMap<Job, Float> fsTimesThirdStage;
	private HashMap<Job, Float> fsTimes;
	
	private HashMap<Client, Container> containersAtClients;
	
	private ArrayList<Container> containers;
	private static HashMap<Integer,Client> clients = new HashMap<Integer, Client>();
	private static HashMap<Integer,AVC> avcs = new HashMap<Integer, AVC>();
	static Depot depot =  new Depot(1,(float) 50.8, (float) 4.7);	
	
	/**
	 * @param planningDate
	 * @param author
	 * @param jobs: HashMap containing all the jobs that need to be processed during the planningDate.
	 */
	public Planning(Date planningDate, String supervisor, ArrayList<Job> jobs){
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
		for(int i = 0;i<avcs.size();i++){
			ArrayList<String> avcTypes = avcs.get(i).getWasteType();
			if(avcTypes.contains(containerWasteType)){
				suitedAVCS.add(avcs.get(i));
			}
		}
		float closestDistance = (float) Double.POSITIVE_INFINITY;
		
		AVC closestAVC = suitedAVCS.get(0);
		for(int j = 0;j<suitedAVCS.size();j++){
			float dist = container.getContainerLocation().getDistanceTo(suitedAVCS.get(j));
			if(dist < closestDistance){
				closestDistance = dist;
				closestAVC = suitedAVCS.get(j);
			}
		}
		return closestAVC;
	}	
	
	public void feasibleSolution(){
		//Deze for maakt voor elke gebelde klant een container aan. Vervolgens een job om deze containers op te laden in het depot. Vervolgens wordt bepaald welke job er naar de klant moet leiden.
		for(int i = 0;i<idleJobs.size();i++){
			float jobtime1 = 0;
			float jobtime2 = 0;
			Container container = new Container(i,1,depot,true,"");
			containers.add(container);
			
			if(idleJobs.get(i).getJobType().equals("FillContainer")){
				//eerst opladen in depot, dan vullen bij de klant
				Job job = new Job("LoadPlaceDepot",depot, container);
				jobtime1 = job.getJobTime();
				Location target = idleJobs.get(i).getTargetLocation();
				Job job1 = new Job("FillContainer",depot,target,containers.get(i));
				jobtime2 = job1.getJobTime();
			}
			if(idleJobs.get(i).getJobType().equals("SwitchContainer")){
				//eerst opladen in depot, dan gaan wisselen bij de klant
				Job job = new Job("LoadPlaceDepot",depot, container);
				jobtime1 = job.getJobTime();
				Job job1 = new Job("SwitchContainer",depot,idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobtime2 = job1.getJobTime();		
				//bij het aanmaken van de nieuwe job zet je de locatie van containers.get(i) op de klant.
				//de container die bij de klant staat, moet opgeladen worden en naar de AVC gebracht.
				Container containerAtClient = containersAtClients.get(containers.get(i).getContainerLocation());
				containersAtClients.put((Client) containers.get(i).getContainerLocation(), containers.get(i));
				containers.set(i, containerAtClient);
				
			}
			if(idleJobs.get(i).getJobType().equals("LoadOnTruck")){
				//niet opladen in depot, dan opladen bij de klant
				Job job1 = new Job("LoadOnTruck",depot,idleJobs.get(i).getTargetLocation(),null);
				jobtime2 = job1.getJobTime();
				//bij het aanmaken van de nieuwe job zet je de locatie van containers.get(i) op de klant.
				//de container die bij de klant staat, moet opgeladen worden en naar de AVC gebracht.
				Container containerAtClient = containersAtClients.get(containers.get(i).getContainerLocation());
				containersAtClients.put((Client) containers.get(i).getContainerLocation(),null);
				containers.set(i, containerAtClient);
			}
			
			Float jobtime = (jobtime1 + jobtime2);
			fsTimesFirstStage.put(idleJobs.get(i), jobtime);
		}
		for(int j = 0;j<idleJobs.size();j++){
			Job job = new Job("EmptyAVC",containers.get(j).getContainerLocation(),containers.get(j));
			//na deze constructur heeft de job een targetlocation. De container van bij deze job moet als locatie deze targetlocation krijgen.
			containers.get(j).setContainerLocation(job.getTargetLocation());
			float jobtime = job.getJobTime();
			fsTimesSecondStage.put(idleJobs.get(j),jobtime);					
		}
		for(int cntr = 0;cntr<idleJobs.size();cntr++){
			float jobtime3 = 0;
			if(containers.get(cntr).getClient() != null){
				Job job = new Job("PlaceClient",containers.get(cntr).getContainerLocation(),containers.get(cntr));
				float jobtime3temp = job.getJobTime();
				//met lege camion terugrijden naar depot.
				float travelTimeBackToDepot = job.getTargetLocation().getTravelTimeTo(depot);
				jobtime3 = jobtime3temp + travelTimeBackToDepot;
			}
			else{
				Job job = new Job("LoadPlaceDepot",containers.get(cntr).getContainerLocation(),containers.get(cntr));
				containers.get(cntr).setContainerLocation(depot);
				jobtime3 = job.getJobTime();
				fsTimesThirdStage.put(idleJobs.get(cntr),jobtime3);	
			}
			float jobtime1 = fsTimesFirstStage.get(idleJobs.get(cntr));
			float jobtime2 = fsTimesSecondStage.get(idleJobs.get(cntr));
			fsTimes.put(idleJobs.get(cntr),(jobtime1+jobtime2+jobtime3));			
		// Nu zit er in fsTimes de feasible solution per aangevraagde job met bijhorende tijd.
		}
	}
}
	
			
			
	
		
		