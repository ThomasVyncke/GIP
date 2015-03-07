import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


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
	private HashMap<Job, ArrayList<Job>> jobJobs = new HashMap<Job, ArrayList<Job>>();
	private HashMap<Integer, ArrayList<Job>> jobRoute = new HashMap<Integer, ArrayList<Job>>();
	
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
	 *  Determines feasible alternative locations.        
	 */
	public ArrayList<Location> PossibleAlternativeTargetLocations(Container container, Location location){
		
		//Tijd die gegeven container reeds doorgaan is. 
		int indexOfContainer = containers.indexOf(container);
		Job containerJob = idleJobs.get(indexOfContainer);
		float timeSoFar = fsTimesFirstStage.get(containerJob);
		timeSoFar = timeSoFar + fsTimesSecondStage.get(containerJob);
		
		//Lijst van mogelijke next stops maken.
		ArrayList<Location> alternativeLocations = new ArrayList<Location>();
		//Checken of we in een AVC zitten.
		if(location instanceof AVC){
			//Als container terug moet naar zelfde klant --> geen alternatieve locaties.
			if(container.getClient() != null){
				return null;
			}
			//Anders over alle mogelijke klanten itereren die een fill/switch/place job aanvroegen. 
			else{
				AVC avc = (AVC) location;	
				ArrayList<Client> possibleClients = new ArrayList<Client>();
			//CHECKEN DAT HIJ ZICHZELF ER NIET UITHAALT.	
				for(int i=0;i<idleJobs.size();i++){
					Client client;
					Job job = idleJobs.get(i);
					if(job.getTargetLocation() instanceof Client){
						client = (Client) job.getTargetLocation();
					}
					else{
						throw new NullPointerException("targetlocations in idlejobs moeten altijd klanten zijn.");
					}
					int cap = client.getContainerCapacity();
					boolean capacityTest = (cap == container.getCapacity());
					boolean jobTypeTest = (job.getJobType().equals("FillContainer") || job.getJobType().equals("SwitchContainer") || job.getJobType().equals("PlaceContainer"));
					
					
					float travelTime = location.getTravelTimeTo(client);
					//zoekt AVC die open is en dichtste bij.
					AVC closestAVC = getClosestOpenAVC((Client) idleJobs.get(i).getTargetLocation(), timeSoFar);
					

					double key = Double.POSITIVE_INFINITY;
					Client locationToSearch = client;
					for(int counter = 0; counter<idleJobs.size(); counter++){
						//zoek bij welke idleJobs de gegeven locatie hoort, en zoek de key ervan in jobRoute.
						if(locationToSearch == idleJobs.get(counter).getTargetLocation()){
							//get key from value. 								
							for(double cntr = 0; cntr< jobRoute.size();cntr++){
								ArrayList<Job> jobsInJobRoute = jobRoute.get(cntr);
								if(jobsInJobRoute.get(0).getTargetLocation().equals(locationToSearch)){
									key = cntr;
								}
								else{							
								}
							}
						}
					}	
					
					//CHECKT OF DAT AAN ALLE TIMEWINDOWS VAN AVCS EN KLANTEN IN EEN POTENTIELE LOCATIE (+zijn vervolg!!!) VOLDAAN ZIJN.
					boolean timeTestAVC = true;
					boolean timeTestClient = true;
					//De doorlooptijd berekenen.
					jobRoute.get(key);
					Float time = (float) 0;
					for(int teller = 0; teller<jobRoute.size(); teller++){
						ArrayList<Job> idleJobs = jobRoute.get(teller);
						for(int teller2 = 0; teller2<idleJobs.size(); teller2++){
							ArrayList<Job> jobSequence = jobJobs.get(idleJobs.get(teller2));
							for(int teller3 = 0; teller3<jobSequence.size(); teller3++){
								Job jobInSequence = jobSequence.get(teller3);								
								time = time + jobInSequence.getJobTime();	
								
								if(jobInSequence.getTargetLocation() instanceof AVC){
									AVC avcInSequence = (AVC) jobInSequence.getTargetLocation();
									if(time > 28800 || time < avcInSequence.getTimeStart() || time> avcInSequence.getTimeStop()){
										timeTestAVC = false;
										break;
									}
								}
								if(jobInSequence.getTargetLocation() instanceof Client){
									Client clientInSequence = (Client) jobInSequence.getTargetLocation();
									if(time > 28800 || time < clientInSequence.getStartTime() || time> clientInSequence.getStopTime()){
										timeTestClient = false;
										break;
									}
								}
							}
						}
					}															
					
					//HIER MOET OOK INKOMEN DAT DE BESCHOUWDE ALTERNATIVE LOCATION DE EERSTE MOET ZIJN VAN DE IDLEJOBSEQUENCE
					//Aankomen bij de klant voor die sluit. (service time in rekening brengen evt. door af te trekken van stopTime)			
					
					if(capacityTest && jobTypeTest && timeTestAVC && timeTestClient){
						alternativeLocations.add(client);
					}
					
					for(int j = 0;j<jobRoute.size();j++){
						ArrayList<Job> completedJobs = jobRoute.get(j);
						for(int cntr = 0;cntr<completedJobs.size();cntr++){
							if(completedJobs.get(cntr).getTargetLocation() instanceof Client){
								Client visitedClient = (Client) completedJobs.get(cntr).getTargetLocation();
									alternativeLocations.remove(visitedClient);		
							}
							else{								
							}
						}
					}							
				}					
			}
		}
		
		if(container.getContainerLocation() instanceof Client){
			//Eerst kijken of deze klant zijn job al gebeurd is. --> We staan er om zijn container terug te brengen.
			for(int i=0;i<idleJobs.size();i++){
				Client client;
				Job job = idleJobs.get(i);
				if(job.getTargetLocation() instanceof Client){
					client = (Client) job.getTargetLocation();
				}
				else{
					throw new NullPointerException("targetlocations in idlejobs moeten altijd klanten zijn.");
				}
				//Truck is sowieso leeg in dit geval, enige mogelijkheid is LoadOnTruck. 
				boolean jobTypeTest = (job.getJobType().equals("LoadOnTruck"));
				
				float travelTime = location.getTravelTimeTo(client);
				//zoekt AVC die open is en dichtste bij.
				AVC closestAVC = getClosestOpenAVC((Client) idleJobs.get(i).getTargetLocation(), timeSoFar);
				float timeClientSecondStage = client.getTravelTimeTo(closestAVC);
				float newBeginTime = timeSoFar + travelTime; 
				float newEndTime = timeSoFar + travelTime + timeClientSecondStage;					
				boolean timeTestAVC = (newEndTime < closestAVC.getTimeStop() && newEndTime > closestAVC.getTimeStart());
				//Aankomen bij de klant voor die sluit. (service time in rekening brengen evt. door af te trekken van stopTime)			
				boolean timeTestClient = ((timeSoFar + travelTime) < client.getStopTime() && (timeSoFar + travelTime)>client.getStartTime());
		
				if(jobTypeTest && timeTestAVC && timeTestClient){
					alternativeLocations.add(client);
				}
				
				for(int j = 0;j<jobRoute.size();j++){
					ArrayList<Job> completedJobs = jobRoute.get(j);
					for(int cntr = 0;cntr<completedJobs.size();cntr++){
						Client visitedClient = (Client) completedJobs.get(cntr).getTargetLocation();
						alternativeLocations.remove(visitedClient);								
					}
				}					
			}
		}
		return alternativeLocations; 
	}
			
	
	/**
	 * 
	 */
	public void uberHeuristiekRouteOpt(){
		float startTemperature = 2000;
		float coolingFactor = (float) 0.95;
		float tempLimit = (float) 0.01; 
		float maxIt = 2500;
		float temperature = startTemperature;
		
		for(int i = 0; i < maxIt && temperature<tempLimit ;i++){
			
			Random randomgen = new Random();
			int randomNumber = randomgen.nextInt(jobRoute.size());
			
			//--> Lijst van opeenvolgende idleJobs.
			ArrayList<Job> idleJobsPerformed = jobRoute.get(randomNumber); 
			//--> Daarvan laatste nemen
			Job lastIdleJob = idleJobsPerformed.get(idleJobsPerformed.size()-1);
			//--> Daarvan jobsequentie.
			ArrayList<Job> jobSequentie = jobJobs.get(lastIdleJob);
			//--> Daarvan voorlaaste.
			Job voorlaatsteJob = jobSequentie.get(jobSequentie.size()-2);
			
			ArrayList<Location> alternatives = PossibleAlternativeTargetLocations(voorlaatsteJob.getContainer(), voorlaatsteJob.getTargetLocation());
			
			int amountOfRoutes = jobJobs.size();
			//Voor we hier binnenkomen is er een route voor elke idleJob.
			int negSumOfRsquaredAbs = amountOfRoutes;
			float totalExecutionTime = (float) Double.POSITIVE_INFINITY;
			Location bestAlternative = null; 
			double key = Double.POSITIVE_INFINITY;
			HashMap<Job, ArrayList<Job>> bestJobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
			HashMap<Integer, ArrayList<Job>> bestJobRouteClone = (HashMap<Integer, ArrayList<Job>>) jobRoute.clone();
			ArrayList<Job> bestIdleJobsClone = (ArrayList<Job>) idleJobs.clone();
			
			
			for(int j = 0; j< alternatives.size();j++){
				HashMap<Job, ArrayList<Job>> jobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
				HashMap<Integer, ArrayList<Job>> jobRouteClone = (HashMap<Integer, ArrayList<Job>>) jobRoute.clone();
				ArrayList<Job> idleJobsClone = (ArrayList<Job>) idleJobs.clone();
				
				Location locationToSearch = alternatives.get(j);
				for(int counter = 0; counter<idleJobsClone.size(); counter++){
					//zoek bij welke idleJobs de alternatieve locatie hoort, en zoek de key ervan in jobRoute.
					if(locationToSearch == idleJobsClone.get(counter).getTargetLocation()){
						//get key from value. 
						
						for(int cntr = 0; cntr< jobRouteClone.size();cntr++){
							ArrayList<Job> jobsInJobRoute = jobRouteClone.get(cntr);
							
							if(jobsInJobRoute.get(0).getTargetLocation().equals(locationToSearch)){
								key = cntr;
							}
						}
					}
				}
				
				//Nu weten we waar er een sequentie van IdleJobs staat die begint met een IdleJob met als TargetLocation de alternatieve locatie van onze random gekozen.
				ArrayList<Job> idleJobsInJobRoute = jobRouteClone.get(key);
						
				//Eerste pijl in jobsequentie
				Job firstArrow = jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0);
				String firstArrowJobType = firstArrow.getJobType();
				//MOET DAT HIER WEL LASTIDLEJOB ZIJN? WAAROM NIET DE EERSTE VAN IDLEJOBSINJOBROUTE (--> veranderd naar firstArrow)
				Job connectionJob = new Job(firstArrowJobType, voorlaatsteJob.getTargetLocation(), firstArrow.getTargetLocation(), voorlaatsteJob.getContainer());
				
				//Moet in jobJobs aangepast worden want dat is de enige link tussen idleJobs en hun sequentie van jobs.
				if(jobJobsClone.get(idleJobsInJobRoute.get(0)).remove(firstArrow)){
					
				}
				else{
					throw new NullPointerException("hij kan eerste arrow niet verwijderen dus alternativelocations werkt niet.");
				}						
				//Laatste pijl in jobsequentie
				Job lastArrow = jobSequentie.get(jobSequentie.size()-1);
				//Moet in jobJobs aangepast worden want dat is de enige link tussen idleJobs en hun sequentie van jobs.
				if(jobJobsClone.get(lastIdleJob).remove(lastArrow)){
					jobJobsClone.get(lastIdleJob).add(connectionJob);
				}
				else{
					throw new NullPointerException("hij kan laatste arrow niet verwijderen dus alternativelocations werkt niet.");
				}
				//idleJobs achter mekaar plakken in jobRoute.
				jobRouteClone.get(randomNumber).addAll(idleJobsInJobRoute);
				//idleJObs op de key-plaats verwijderen.
				jobRouteClone.remove(key);
				
				//score berekenen
				//1. #routes
				int tempAmountOfRoutes = jobRouteClone.size();
				int tempNegSumOfRSquaredAbs = 0;
				//2. -|r²|
				negSumOfRsquaredAbs = 0;
				for(int cntr = 0;cntr < jobRouteClone.size();cntr++){
					int r = jobRouteClone.get(cntr).size();
					int rsquared = (int) Math.pow(r, 2);
					tempNegSumOfRSquaredAbs = negSumOfRsquaredAbs - Math.abs(rsquared);
				}
				
				if(tempAmountOfRoutes < amountOfRoutes){
					if(tempNegSumOfRSquaredAbs < negSumOfRsquaredAbs){
						bestAlternative = alternatives.get(j);
						bestJobRouteClone = jobRouteClone;
						bestIdleJobsClone = idleJobsClone;
						bestJobJobsClone = jobJobsClone;
					}
					if(tempNegSumOfRSquaredAbs == negSumOfRsquaredAbs){
						Float time = (float) 0;
						for(int teller = 0; teller<jobRouteClone.size(); teller++){
							ArrayList<Job> idleJobs = jobRouteClone.get(teller);
							for(int teller2 = 0; teller2<idleJobs.size(); teller2++){
								ArrayList<Job> jobSequence = jobJobsClone.get(idleJobs.get(teller2));
								for(int teller3 = 0; teller3<jobSequence.size(); teller3++){
									Job job = jobSequence.get(teller3);
									time = time + job.getJobTime();									
								}
							}											
						}
						if(time < totalExecutionTime){
							bestAlternative = alternatives.get(j);
							bestJobRouteClone = jobRouteClone;
							bestIdleJobsClone = idleJobsClone;
							bestJobJobsClone = jobJobsClone;
						}
					}
					else{}
				}
			}
			this.jobJobs  = bestJobJobsClone;
			this.jobRoute = bestJobRouteClone;
			this.idleJobs = bestIdleJobsClone;			
		}		
	}
	
	public void uberHeuristiekTimeOpt(){
		//Alle idleJobs op een bepaalde index van jobRoute permuteren om zo te optimaliseren in de tijd. 
		//feasibility nog steeds testen.
		//de connecting jobs verwijderen en opnieuw aanmaken voor de nieuwe volgorde. 
		
		//Clonen om beste so far in op te slaan
		HashMap<Job, ArrayList<Job>> bestJobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
		HashMap<Integer, ArrayList<Job>> bestJobRouteClone = (HashMap<Integer, ArrayList<Job>>) jobRoute.clone();
		ArrayList<Job> bestIdleJobsClone = (ArrayList<Job>) idleJobs.clone();
		
		//Clonen om elke iteratie in te kutten.
		HashMap<Job, ArrayList<Job>> JobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
		HashMap<Integer, ArrayList<Job>> JobRouteClone = (HashMap<Integer, ArrayList<Job>>) jobRoute.clone();
		ArrayList<Job> IdleJobsClone = (ArrayList<Job>) idleJobs.clone();
		
		Collection<List<Integer>> output = null;
		ArrayList<Job> idleJobsInRoute;
		for(int i=0; i<jobRoute.size();i++){
			//Een route eruit nemen
			idleJobsInRoute = jobRoute.get(i);
			//Binnen die route moeten permutaties worden uitgevoerd 
			//Eerst kijken waar de connectionJob zich bevindt. Deze moet eerst verwijderd worden. (--> laatste job)
			
			//Alle permutaties bepalen.
			Permutations<Integer> obj = new Permutations<Integer>();
			Collection<Integer> input = new ArrayList<Integer>();
			for(int j=0;j<idleJobsInRoute.size();j++){
				input.add(j);
			}
			output = obj.permute(input);	
						
			ArrayList<Job> bestJobPermutation = new ArrayList<Job>();
			HashMap<Job,ArrayList<Job>> bestJobJobs = new HashMap<Job, ArrayList<Job>>();
			float bestTime = (float) Double.POSITIVE_INFINITY;
			float timePermutated;
			
			for (List<Integer> permutation : output) {
				HashMap<Job,ArrayList<Job>> jobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
				ArrayList<Job> jobPermutation = new ArrayList<Job>();
				for(int j=0;j<permutation.size();j++){					  
					  jobPermutation.add(idleJobsInRoute.get(permutation.get(j)));
				}
				//Van elke job, behalve de laatste, de laatste in sequentie wegdoen.
				for(int j=0;j<permutation.size()-2;j++){
					//Bepaalde idleJob nemen.
					Job idleJob = jobPermutation.get(j);					
					//Laatste job in diens jobSequentie nemen.
					Job lastJobInIdleJob = jobJobsClone.get(idleJob).get(jobJobsClone.get(idleJob).size()-1);
					
					//Volgende idleJob nemen.
					Job nextIdleJob = jobPermutation.get(j+1);
					//Eerste job in diens jobSequentie nemen
					Job firstJobInNextIdleJob = jobJobsClone.get(nextIdleJob).get(0);
					
					Job newConnectionJob = new Job(lastJobInIdleJob.getJobType(),lastJobInIdleJob.getTargetLocation(), firstJobInNextIdleJob.getStartLocation() , lastJobInIdleJob.getContainer());
					
					//Laatste job in de sequentie verwijderen
					if(jobJobsClone.get(idleJob).remove(lastJobInIdleJob)){
						//vervangen door de nieuwe connectionJob.
						jobJobsClone.get(idleJob).add(newConnectionJob);
					}
					else{
						throw new NullPointerException("kan de laatste idleJob niet verwijderen");
					}
				}	
				timePermutated = 0;
				for(int j=0;j<jobPermutation.size();j++){
					Job idleJobInPermutation = jobPermutation.get(j);
					ArrayList<Job> jobSequenceInPermutation = jobJobsClone.get(idleJobInPermutation);
					for(int cntr = 0;cntr<jobSequenceInPermutation.size();cntr++){
						timePermutated = timePermutated + jobSequenceInPermutation.get(cntr).getJobTime();						
					}
				}
				if(timePermutated < bestTime){
					bestTime = timePermutated;
					bestJobPermutation = jobPermutation;
					bestJobJobs = jobJobsClone;
				}
			}
			jobJobs = bestJobJobs;
			jobRoute.put(i, bestJobPermutation);
		}			
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
	
	
	/**
	 *
	 * @param container
	 * @return
	 */
	public static AVC getClosestOpenAVC(Client client, float time){
		String clientWasteType = client.getWasteType();
		ArrayList<AVC> suitedAVCS = new ArrayList<AVC>();
		//Make a list with the AVCs which are able to process the clients type of waste.
		for(int i = 0;i<avcs.size();i++){
			ArrayList<String> avcTypes = avcs.get(i).getWasteType();
			float travelTime = client.getTravelTimeTo(avcs.get(i));			
			float newBeginTime = time + travelTime; 
			boolean beginTimeTest = (newBeginTime > avcs.get(i).getTimeStart());
			if(avcTypes.contains(clientWasteType) && beginTimeTest){
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
		//KLANTEN MOETEN CONTAINER NOG TERUG KRIJGEN
		for(int i = 0;i<idleJobs.size();i++){
			float jobtime1 = 0;
			float jobtime2 = 0;
			ArrayList<Job> jobs = new ArrayList<Job>();
			if(idleJobs.get(i).getJobType().equals("PlaceClient")){
				System.out.println("Job " +i+" is a 'PlaceClient' job.");
				//Eerst oploaden in depot.
				Job job1 = new Job("LoadPlaceDepot",depot, containers.get(i));
				jobs.add(job1);
				
				jobtime1 = job1.getJobTime();
				System.out.println("Load container in depot. Time: "+jobtime1);
				
				//Dan vullen bij de klant.
				Job job2 = new Job("PlaceContainer",containers.get(i).getContainerLocation(),idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobs.add(job2);
				jobtime2 = job2.getJobTime();
				System.out.println("Place container at client. Time: "+jobtime2);
				
				float jobtime = jobtime1 + jobtime2;
				fsTimesFirstStage.put(idleJobs.get(i), jobtime);
				
				Job job3 = new Job("LoadPlaceDepot",containers.get(i).getContainerLocation(),containers.get(i));				
				jobs.add(job3);
				containers.get(i).setContainerLocation(depot);
				float jobtime3 = job3.getJobTime() - job3.getJobExecutionTime();
				System.out.println("Bring container back to depot. Time: " +jobtime3);
				fsTimesSecondStage.put(idleJobs.get(i), jobtime3);
				System.out.println("Bringing container back to depot. Time: "+jobtime3);
				
				jobJobs.put(idleJobs.get(i), jobs);
				fsTimes.put(idleJobs.get(i),(jobtime1+jobtime2+jobtime3));
				System.out.println("Total time for job " + i + " is: "+ (jobtime1+jobtime2+jobtime3));								
			// Nu zit er in fsTimes de feasible solutio n per aangevraagde job met bijhorende tijd.
					
			
			}
			else{				
			if(idleJobs.get(i).getJobType().equals("FillContainer")){
				System.out.println("Job "+i+" is a 'FillContainer' job.");
				//Eerst opladen in depot.				
				Job job1 = new Job("LoadPlaceDepot",depot, containers.get(i));
				jobs.add(job1);
				
				jobtime1 = job1.getJobTime();
				System.out.println("Load container in depot. Time: "+jobtime1);
				
				//Dan vullen bij de klant.
				Job job2 = new Job("FillContainer",containers.get(i).getContainerLocation(),idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobs.add(job2);
				jobtime2 = job2.getJobTime();
				System.out.println("Fill container at client. Time: "+jobtime2);
			}
			if(idleJobs.get(i).getJobType().equals("SwitchContainer")){
				System.out.println("Job "+i+" is a 'SwitchContainer' job.");
				//Eerst opladen in depot				
				Job job1 = new Job("LoadPlaceDepot",depot, containers.get(i));
				jobs.add(job1);
				jobtime1 = job1.getJobTime();
				System.out.println("Load container in depot. Time: "+jobtime1);
				
				//Dan gaan wisselen bij de klant.
				Job job2 = new Job("SwitchContainer",depot,idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobs.add(job2);
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
				jobs.add(job1);
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
			jobs.add(job);
			//jobroute bevat <nummer camion, gedane idle jobs>
			
			//Na deze constructor heeft de job een targetlocation. 
			//De container van bij deze job moet als locatie deze targetlocation krijgen.
			containers.get(i).setContainerLocation(job.getTargetLocation());
			
			float jobtime4 = job.getJobTime();
			System.out.println("Empty container at avc. Time: " + jobtime4);
			fsTimesSecondStage.put(idleJobs.get(i),jobtime4);					
			//ArrayList<Location> alternatives = new ArrayList<Location>();
			//alternatives = this.PossibleAlternativeTargetLocations(containers.get(i));
			
			float jobtime3 = 0;
			//Bepalen of dezelfde container terug naar de klant moet.
			if(containers.get(i).getClient() != null){
				System.out.println("The client wants the same container back.");
				Job job4 = new Job("PlaceClient",containers.get(i).getContainerLocation(),containers.get(i));				
				jobs.add(job4);
				float jobtime3temp = job4.getJobTime();
				System.out.println("Returning container to client. Time: "+jobtime3temp);
				//met Lege camion terugrijden naar depot. --> Geen job voor maken (enkel travel time).
				
				Job job5 = new Job("LoadPlaceDepot",containers.get(i).getContainerLocation(),containers.get(i));				
				jobs.add(job5);
				containers.get(i).setContainerLocation(depot);
				float travelTimeBackToDepot = job5.getJobTime() - job5.getJobExecutionTime();
				System.out.println("Driving truck back to depot. Time: "+travelTimeBackToDepot);
				jobtime3 = jobtime3temp + travelTimeBackToDepot;				
			}
			else{
				Job job5 = new Job("LoadPlaceDepot",containers.get(i).getContainerLocation(),containers.get(i));				
				jobs.add(job5);
				containers.get(i).setContainerLocation(depot);
				jobtime3 = job5.getJobTime();
				System.out.println("Bring container back to depot. Time: " +jobtime3);	
			}
			fsTimesThirdStage.put(idleJobs.get(i),jobtime3);	
			float jobtime5 = fsTimesFirstStage.get(idleJobs.get(i));
			float jobtime6 = fsTimesSecondStage.get(idleJobs.get(i));
			jobJobs.put(idleJobs.get(i), jobs);
			fsTimes.put(idleJobs.get(i),(jobtime5+jobtime6+jobtime3));
			System.out.println("Total time for job " + i + " is: "+ (jobtime5+jobtime6+jobtime3));
			// Nu zit er in fsTimes de feasible solutio n per aangevraagde job met bijhorende tijd.						
			}
		}
	}
}
	
			
			
	
		
		