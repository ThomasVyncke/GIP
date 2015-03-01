import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class Planning {
	//unit: m/s (equivalent to 50km/h)
	public static final double averageSpeed = 19.44;
	public static final double distanceRatio = 1.5;
	private String author;
	private ArrayList<Job> idleJobs = new ArrayList<Job>();
	//HashMaps containing job times of the 
	private HashMap<Job, Float> fsTimesFirstStage = new HashMap<Job,Float>();
	private HashMap<Job, Float> fsTimesSecondStage = new HashMap<Job,Float>();
	private HashMap<Job, Float> fsTimesThirdStage = new HashMap<Job,Float>();
	private HashMap<Job, Float> fsTimes = new HashMap<Job,Float>();
	
	private HashMap<Client, Container> containersAtClients = new HashMap<Client, Container>();
	
	private ArrayList<Container> containers = new ArrayList<Container>();
	private static HashMap<Integer,Client> clients = new HashMap<Integer, Client>();
	private static ArrayList<AVC> avcs = new ArrayList<AVC>();
	static Depot depot =  new Depot(1,(float) 50.8, (float) 4.7);	
	
	/**
	 * @param planningDate
	 * @param author
	 * @param jobs: HashMap containing all the jobs that need to be processed during the planningDate.
	 */
	public Planning(String supervisor){
		this.author = supervisor;
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
	
	public void addContainer(Container container){
		if(container.getContainerLocation() instanceof Client){
			containersAtClients.put((Client) container.getContainerLocation(), container);
		}
		else{
		containers.add(container);
		}		
	}
	
	public void putClient(Client client){
		Integer size = clients.size();
		clients.put(size+1,client);		
	}
	
	public void addAVC(AVC avc){
		Integer size = avcs.size();
		avcs.add(size,avc);		
	}
	
	public void putJob(Job job){
		idleJobs.add(job);
	}
	
	
	
	/**
	 * Evt. nog een test voor timewindows invoeren.
	 * @param container
	 * @return
	 */
	public static AVC getClosestAVC(Client client){
		String clientWasteType = client.getWasteType();
		ArrayList<AVC> suitedAVCS = new ArrayList<AVC>();
		//Make a list with the AVCs which are able to process the clients type of waste.
		for(int i = 0;i<avcs.size();i++){
			ArrayList<String> avcTypes = avcs.get(i).getWasteType();
			if(avcTypes.contains(clientWasteType)){
				suitedAVCS.add(avcs.get(i));
			}
		}
		float closestDistance = (float) Double.POSITIVE_INFINITY;
		
		AVC closestAVC = suitedAVCS.get(0);
		for(int j = 0;j<suitedAVCS.size();j++){
			float dist = client.getDistanceTo(suitedAVCS.get(j));
			if(dist < closestDistance){
				closestDistance = dist;
				closestAVC = suitedAVCS.get(j);
			}
		}
		return closestAVC;
	}	
	
	public void feasibleSolution(){
		System.out.println("Start generation feasible solution...");
		for(int i = 0;i<idleJobs.size();i++){
			float jobtime1 = 0;
			float jobtime2 = 0;
						
			if(idleJobs.get(i).getJobType().equals("FillContainer")){
				System.out.println("Job "+i+" is a 'FillContainer' job.");
				//Eerst opladen in depot.				
				Job job1 = new Job("LoadPlaceDepot",depot, containers.get(i));
				jobtime1 = job1.getJobTime();
				System.out.println("Load container in depot. Time: "+jobtime1);
				
				//Dan vullen bij de klant.
				Job job2 = new Job("FillContainer",containers.get(i).getContainerLocation(),idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobtime2 = job2.getJobTime();
				System.out.println("Fill container at client. Time: "+jobtime2);
			}
			if(idleJobs.get(i).getJobType().equals("SwitchContainer")){
				System.out.println("Job "+i+" is a 'SwitchContainer' job.");
				//Eerst opladen in depot				
				Job job1 = new Job("LoadPlaceDepot",depot, containers.get(i));
				jobtime1 = job1.getJobTime();
				System.out.println("Load container in depot. Time: "+jobtime1);
				
				//Dan gaan wisselen bij de klant.
				Job job2 = new Job("SwitchContainer",depot,idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobtime2 = job2.getJobTime();	
				System.out.println("Switch container in depot. Time: "+ jobtime2);
				
				
				//De container die reeds bij de klant staat, moet opgeladen worden en naar de AVC gebracht.
				//Bij het aanmaken van de nieuwe job zet je de locatie van de betreffende container (containers.get(i)) op de klant.
				Container containerAtClient = containersAtClients.get(containers.get(i).getContainerLocation());				
				containersAtClients.put((Client) containers.get(i).getContainerLocation(), containers.get(i));
				containers.set(i, containerAtClient);
				
				
			}
			if(idleJobs.get(i).getJobType().equals("LoadOnTruck")){
				System.out.println("Job "+i+" is a 'LoadOnTruck' job.");
				//Niet opladen in depot, enkel opladen bij de klant					
				Job job1 = new Job("LoadOnTruck",depot,idleJobs.get(i).getTargetLocation(),null);
				jobtime2 = job1.getJobTime();
				
				//De container die bij de klant staat, moet opgeladen worden en naar de AVC gebracht.
				//Deze container moet dus verwijderd worden uit containersAtClients en toegevoegd aan containers.
				Container containerAtClient = containersAtClients.get(idleJobs.get(i).getContainer().getContainerLocation());
				containersAtClients.put((Client) containerAtClient.getContainerLocation(),null);
				containers.set(i, containerAtClient);
			}			
			Float jobtime = (jobtime1 + jobtime2);
			fsTimesFirstStage.put(idleJobs.get(i), jobtime);
		
	
			Client client = (Client) idleJobs.get(i).getContainer().getContainerLocation();
			//Set container waste type to the one picked up from the Client to find the right AVC.
			containers.get(i).setWasteType(client.getWasteType());
			Job job = new Job("EmptyAVC",containers.get(i).getContainerLocation(),containers.get(i));			
			//Na deze constructor heeft de job een targetlocation. 
			//De container van bij deze job moet als locatie deze targetlocation krijgen.
			containers.get(i).setContainerLocation(job.getTargetLocation());
			
			float jobtime4 = job.getJobTime();
			System.out.println("Empty container at avc. Time: " + jobtime4);
			fsTimesSecondStage.put(idleJobs.get(i),jobtime4);					
		
			float jobtime3 = 0;
			//Bepalen of dezelfde container terug naar de klant moet.
			if(containers.get(i).getClient() != null){
				System.out.println("The client wants the same container back.");
				Job job4 = new Job("PlaceClient",containers.get(i).getContainerLocation(),containers.get(i));				
				float jobtime3temp = job4.getJobTime();
				System.out.println("Returning container to client. Time: "+jobtime3temp);
				//met Lege camion terugrijden naar depot. --> Geen job voor maken (enkel travel time).
				float travelTimeBackToDepot = job4.getTargetLocation().getTravelTimeTo(depot);
				System.out.println("Bringing container back to depot. Time: "+travelTimeBackToDepot);
				jobtime3 = jobtime3temp + travelTimeBackToDepot;				
			}
			else{
				Job job5 = new Job("LoadPlaceDepot",containers.get(i).getContainerLocation(),containers.get(i));				
				containers.get(i).setContainerLocation(depot);
				jobtime3 = job5.getJobTime();
				System.out.println("Bring container back to depot. Time: " +jobtime3);
				fsTimesThirdStage.put(idleJobs.get(i),jobtime3);	
			}
			float jobtime5 = fsTimesFirstStage.get(idleJobs.get(i));
			float jobtime6 = fsTimesSecondStage.get(idleJobs.get(i));
			fsTimes.put(idleJobs.get(i),(jobtime5+jobtime6+jobtime3));
			System.out.println("Total time for job " + i + " is: "+ (jobtime5+jobtime6+jobtime3));
		// Nu zit er in fsTimes de feasible solutio n per aangevraagde job met bijhorende tijd.
		}
	}
}
	
			
			
	
		
		