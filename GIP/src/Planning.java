import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.rits.cloning.Cloner;


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
	static Depot depot =  new Depot(1,(float) 50.9433, (float) 4.567);	
	
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
	
	public HashMap<Integer, ArrayList<Job>> cloneJobRoute(HashMap<Integer, ArrayList<Job>> routeMap){
		Cloner cloner = new Cloner();
		HashMap<Integer, ArrayList<Job>> hashMapClone = cloner.deepClone(routeMap);
		
		Set<Entry<Integer, ArrayList<Job>>> entrySet = routeMap.entrySet();
		for(Entry entry: entrySet){
			ArrayList<Job> valueClone = (ArrayList<Job>) cloner.deepClone(entry.getValue());
			ArrayList<Job> entryJobs = (ArrayList<Job>) entry.getValue();
			for(int j=0;j<valueClone.size();j++){				
				Job job = entryJobs.get(j);
				valueClone.remove(j);
				valueClone.add(j,job);
			}
			entry.setValue(valueClone);
			hashMapClone.replace((Integer) entry.getKey(),(ArrayList<Job>) entry.getValue());
		}
		return hashMapClone;
	}
	
	public HashMap<Job, ArrayList<Job>> cloneJobJobs(HashMap<Job, ArrayList<Job>> jobJobsToClone){
		Cloner cloner = new Cloner();
		HashMap<Job, ArrayList<Job>> hashMapClone = cloner.deepClone(jobJobsToClone);
		Set<Job> keySet = hashMapClone.keySet();
		hashMapClone.remove(keySet.removeAll(keySet));
		
		for(int i=0;i<idleJobs.size(); i++){
			ArrayList<Job> valueClone = cloner.deepClone(jobJobsToClone.get(idleJobs.get(i)));
			
			for(int j=0;j<jobJobsToClone.get(idleJobs.get(i)).size();j++){				
				Job job = jobJobsToClone.get(idleJobs.get(i)).get(j);
				valueClone.remove(j);
				valueClone.add(j,job);				
			}
			
			hashMapClone.put(idleJobs.get(i), valueClone);
		}						
	return hashMapClone;
	}
	
	public ArrayList<Job> cloneIdleJobs(ArrayList<Job> idleJobsToClone){
		Cloner cloner = new Cloner();
		ArrayList<Job> arrayListClone = cloner.deepClone(idleJobsToClone);
			for(int j=0;j<idleJobsToClone.size();j++){
				Job job = idleJobsToClone.get(j);
				arrayListClone.remove(j);
				arrayListClone.add(j,job);
			}
			return arrayListClone;
	}
	

	
		
		
	
//	public HashMap<Job, ArrayList<Job>> copyJobJobs(){
//		HashMap<Job, ArrayList<Job>> copy = new HashMap<Job, ArrayList<Job>>();
//		for(int i = 0; i<jobJobs.size();i++){
//			jobJobs.
//		}
//		
//		return copy;
//	}
	
	/** 
	 *  Determines feasible alternative locations.        
	 */
	public ArrayList<Location> PossibleAlternativeTargetLocations(Container container, Location location, int selfKey){
		
		//Tijd die gegeven container reeds doorgaan is. 
		int indexOfContainer = containers.indexOf(container);
		Job containerJob = idleJobs.get(indexOfContainer);
		float timeSoFar = fsTimesFirstStage.get(containerJob);
		timeSoFar = timeSoFar + fsTimesSecondStage.get(containerJob);
		System.out.println("Elapsed time so far: "+timeSoFar);
		//Lijst van mogelijke next stops maken.
		ArrayList<Location> alternativeLocations = new ArrayList<Location>();
		//Checken of we in een AVC zitten.
		if(location instanceof AVC){
			//Als container terug moet naar zelfde klant --> geen alternatieve locaties.
			if(container.getClient() != null){
				System.out.println("This container has to be delivered again to the same client.");
				return null;
			}
			//Anders over alle mogelijke klanten itereren die een fill/switch/place job aanvroegen. 
			else{
				AVC avc = (AVC) location;	
				
				//for(int i=0;i<jobRoute.size();i++){
				Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRoute.entrySet();
				for(Entry entry: entrySet){						
					if((Integer) entry.getKey() == selfKey){}
					//if( ((ArrayList<Job>) entry.getValue()).size() == 0){}
					else{
					Client client;
					Job job = ((ArrayList<Job>) entry.getValue()).get(0);
					if(job.getTargetLocation() instanceof Client){
						client = (Client) job.getTargetLocation();
						System.out.println("Considering client: "+ client);
					}
					else{
						throw new NullPointerException("targetlocations in idlejobs moeten altijd klanten zijn.");
					}
					int cap = client.getContainerCapacity();
					boolean capacityTest = (cap == container.getCapacity());					
					boolean jobTypeTest = (job.getJobType().equals("FillContainer") || job.getJobType().equals("SwitchContainer") || job.getJobType().equals("PlaceContainer"));
					
					
					float travelTime = location.getTravelTimeTo(client);
					//zoekt AVC die open is en dichtste bij.
					try{AVC closestAVC = getClosestOpenAVC((Client) idleJobs.get((Integer) entry.getKey()).getTargetLocation(), timeSoFar);}
					catch(NullPointerException exc){
						System.out.println("Geen AVC's beschikbaar");
					}

					
					//CHECKT OF DAT AAN ALLE TIMEWINDOWS VAN AVCS EN KLANTEN IN EEN POTENTIELE LOCATIE (+zijn vervolg!!!) VOLDAAN ZIJN.
					boolean timeTestAVC = true;
					boolean timeTestClient = true;
					//De doorlooptijd berekenen.
					Float time = (float) 0;
					ArrayList<Job> idleJobs = jobRoute.get(selfKey);
					for(int teller2 = 0; teller2<idleJobs.size(); teller2++){
						ArrayList<Job> jobSequence = jobJobs.get(idleJobs.get(teller2));
						for(int teller3 = 0; teller3<jobSequence.size(); teller3++){
							Job jobInSequence = jobSequence.get(teller3);								
							time = time + jobInSequence.getJobTime();
							
					//client 		
							
							
										
														
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
																				
					
					//HIER MOET OOK INKOMEN DAT DE BESCHOUWDE ALTERNATIVE LOCATION DE EERSTE MOET ZIJN VAN DE IDLEJOBSEQUENCE
					//Aankomen bij de klant voor die sluit. (service time in rekening brengen evt. door af te trekken van stopTime)			
					
					System.out.println("Job type test: " + jobTypeTest);
					System.out.println("Capacity test: " + capacityTest);
					System.out.println("AVC time test: " + timeTestAVC);
					System.out.println("Client time test: " + timeTestClient);
					
					if(capacityTest && jobTypeTest && timeTestAVC && timeTestClient){
						alternativeLocations.add(client);
					}					
				}
			}				
		}
	}
		
		if(location instanceof Client){
			//Eerst kijken of deze klant zijn job al gebeurd is. --> We staan er om zijn container terug te brengen.
			System.out.println("Current location is a client.");
			Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRoute.entrySet();
			for(Entry entry: entrySet){		
			//for(int i=0;i<jobRoute.size();i++){
				if((Integer) entry.getKey() == selfKey){}
				else{
				Client client;
				Job job = ((ArrayList<Job>) entry.getValue()).get(0);
				if(job.getTargetLocation() instanceof Client){
					client = (Client) job.getTargetLocation();
				}
				else{
					throw new NullPointerException("targetlocations in idlejobs moeten altijd klanten zijn.");
				}
				//Truck is sowieso leeg in dit geval, enige mogelijkheid is LoadOnTruck. 
				boolean jobTypeTest = (job.getJobType().equals("LoadOnTruck"));
				
				//CHECKT OF DAT AAN ALLE TIMEWINDOWS VAN AVCS EN KLANTEN IN EEN POTENTIELE LOCATIE (+zijn vervolg!!!) VOLDAAN ZIJN.
				boolean timeTestAVC = true;
				boolean timeTestClient = true;
				//De doorlooptijd berekenen.
				
				Float time = (float) 0;
				
					ArrayList<Job> idleJobs = (ArrayList<Job>) entry.getValue();
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
									
			
				if(jobTypeTest && timeTestAVC && timeTestClient){
					alternativeLocations.add(client);
				}				
				}
			}
		}
		System.out.println("Found alternative locations: " + alternativeLocations);
		return alternativeLocations; 
	}
			
	
	/**
	 * 
	 */
	public void uberHeuristiekRouteOpt(){
		float maxIt = 10;
		System.out.println("Current routing scheme:" + jobRoute);
		System.out.println("Start minimizing amount of routes... ");
		
		for(int i = 0; i < maxIt;i++){
			
			Random randomgen = new Random();
			int randomNumber = randomgen.nextInt(jobRoute.size());
			//int randomNumber = 1;
			System.out.println("Optimizing route: "+ randomNumber);
			
			//--> Lijst van opeenvolgende idleJobs.
			ArrayList<Job> idleJobsPerformed = jobRoute.get(randomNumber); 
			if(idleJobsPerformed != null){
			//--> Daarvan laatste nemen.
			Job lastIdleJob = idleJobsPerformed.get(idleJobsPerformed.size()-1);
			//--> Daarvan jobsequentie.
			ArrayList<Job> jobSequentie = jobJobs.get(lastIdleJob);
			//SOMS WORDT DIT NULL OP HET EINDE: ik weet niet goed waarom
			if(jobSequentie == null){
				System.out.println("eruit gebreakt.");
				break;
			}
			//--> Daarvan voorlaaste.
			Job voorlaatsteJob = jobSequentie.get(jobSequentie.size()-2);
			System.out.println("Last visited location of route is: " + voorlaatsteJob.getTargetLocation());
			System.out.println("Looking for alternative locations to visit...");
			
			ArrayList<Location> alternatives = PossibleAlternativeTargetLocations(voorlaatsteJob.getContainer(), voorlaatsteJob.getTargetLocation(), randomNumber );
			
			int amountOfRoutes = jobRoute.size();
			//Voor we hier binnenkomen is er een route voor elke idleJob.
			int negSumOfRsquaredAbs = -amountOfRoutes;
			float totalExecutionTime = (float) Double.POSITIVE_INFINITY;
			Location bestAlternative = null; 
			int key = Integer.MAX_VALUE;
			HashMap<Job, ArrayList<Job>> bestJobJobsClone = cloneJobJobs(jobJobs);
			//bestJobJobsClone = (HashMap<Job, ArrayList<Job>>) jobJobs.clone();
			HashMap<Integer, ArrayList<Job>> bestJobRouteClone = cloneJobRoute(jobRoute);
			//bestJobRouteClone = (HashMap<Integer, ArrayList<Job>>) jobRoute.clone();
			ArrayList<Job> bestIdleJobsClone = cloneIdleJobs(idleJobs);
			//bestIdleJobsClone = (ArrayList<Job>) idleJobs.clone();
			
			for(int j = 0; j< alternatives.size();j++){
				HashMap<Job, ArrayList<Job>> jobJobsClone = new HashMap<Job, ArrayList<Job>>();
				jobJobsClone = cloneJobJobs(jobJobs);
				
				HashMap<Integer, ArrayList<Job>> jobRouteClone = new HashMap<Integer, ArrayList<Job>>();
				jobRouteClone = cloneJobRoute(jobRoute);
								
				ArrayList<Job> idleJobsClone = new ArrayList<Job>();
				idleJobsClone = cloneIdleJobs(idleJobs);
				
				Location locationToSearch = alternatives.get(j);
				for(int counter = 0; counter<idleJobsClone.size(); counter++){
					//zoek bij welke idleJobs de alternatieve locatie hoort, en zoek de key ervan in jobRoute.
					if(locationToSearch == idleJobsClone.get(counter).getTargetLocation()){
						//get key from values
						
						Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRouteClone.entrySet();
						for(Entry entry: entrySet){	
//						for(int cntr = 0; cntr< jobRouteClone.size();cntr++){
							ArrayList<Job> jobsInJobRoute = (ArrayList<Job>) entry.getValue();
							
							if(jobsInJobRoute.get(0).getTargetLocation() == locationToSearch){
								key = (Integer) entry.getKey();
								System.out.println("Altloc job: "+jobsInJobRoute.get(0)+", Alternative location part of route: "+key);
							}
						}
					}
				}
				
				//Nu weten we waar er een sequentie van IdleJobs staat die begint met een IdleJob met als TargetLocation de alternatieve locatie van onze random gekozen.
				ArrayList<Job> idleJobsInJobRoute = jobRouteClone.get(key);
				
				
				
				
				
				
				
				
				
				
				
				//Tijd van huidige berekenen.				
				float timeFirstRoute = 0;
				ArrayList<Job> idleJobs1 = jobRouteClone.get(randomNumber);
				for(int teller2 = 0; teller2<idleJobs1.size(); teller2++){
					ArrayList<Job> jobSequence = jobJobsClone.get(idleJobs1.get(teller2));
					for(int teller3 = 0; teller3<jobSequence.size(); teller3++){
						Job job = jobSequence.get(teller3);
						timeFirstRoute = timeFirstRoute + job.getJobTime();									
					}
				}
				System.out.println(timeFirstRoute);
				
				//Tijd van toe te voegen berekenen.				
				float timeSecondRoute = 0;
				ArrayList<Job> idleJobs2 = idleJobsInJobRoute;
				for(int teller2 = 0; teller2<idleJobs2.size(); teller2++){
					ArrayList<Job> jobSequence = jobJobsClone.get(idleJobs2.get(teller2));
					for(int teller3 = 0; teller3<jobSequence.size(); teller3++){
						Job job = jobSequence.get(teller3);
						timeSecondRoute = timeSecondRoute + job.getJobTime();									
					}
				}
				System.out.println(timeSecondRoute);
				
				float timeTotal = timeFirstRoute + timeSecondRoute;
				
				
				//last arrow hier vanaf trekken
				//volgende potentiele jobs tijd uitrekenen 
					//eerste, mogelijk twee eerset tijden er vanaf trekken.
				float timeFirstArrow = 0;
				if(jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getJobType().equals("LoadPlaceDepot")){
					float timeFirstFirst = jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getJobTime();
					float timeFirstSecond = jobJobsClone.get(idleJobsInJobRoute.get(0)).get(1).getJobTime();
					timeFirstArrow = timeFirstFirst + timeFirstSecond;
				}
				else{
					float timeFirstFirst = jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getJobTime();
					timeFirstArrow = timeFirstFirst;
				}
				
				float timeLastArrow = 0;
				Job lastArrow = jobSequentie.get(jobSequentie.size()-1);
				timeLastArrow = lastArrow.getJobTime();
				
				Job connectionJob = new Job(jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getJobType(), voorlaatsteJob.getTargetLocation(), jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getTargetLocation(), voorlaatsteJob.getContainer());
				float timeConnectionJob = connectionJob.getJobTime();
				
				timeTotal = timeTotal - timeFirstArrow - timeLastArrow + timeConnectionJob;
				
				System.out.println("timeTotal= "+ timeTotal);
				
				
				
				if(timeTotal<36000){					
				//Eerste pijl in jobsequentie
				//Hangt van job af
				Job firstArrow = null;
				//Als de eerste een LoadPlaceDepot job is dan moet deze eerst verwijderd worden. 
				if(jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0).getJobType().equals("LoadPlaceDepot")){
					if(jobJobsClone.get(idleJobsInJobRoute.get(0)).remove(jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0))){}
				}
				firstArrow = jobJobsClone.get(idleJobsInJobRoute.get(0)).get(0);
				String firstArrowJobType = firstArrow.getJobType();
				//MOET DAT HIER WEL LASTIDLEJOB ZIJN? WAAROM NIET DE EERSTE VAN IDLEJOBSINJOBROUTE (--> veranderd naar firstArrow)
				connectionJob = new Job(firstArrowJobType, voorlaatsteJob.getTargetLocation(), firstArrow.getTargetLocation(), voorlaatsteJob.getContainer());
				
				//Moet in jobJobs aangepast worden want dat is de enige link tussen idleJobs en hun sequentie van jobs.
				if(jobJobsClone.get(idleJobsInJobRoute.get(0)).remove(firstArrow)){					
					
				}
				else{
					throw new NullPointerException("hij kan eerste arrow niet verwijderen dus alternativelocations werkt niet.");
				}						
				//Laatste pijl in jobsequentie
				lastArrow = jobSequentie.get(jobSequentie.size()-1);
				
				//Moet in jobJobs aangepast worden want dat is de enige link tussen idleJobs en hun sequentie van jobs.
				if(jobJobsClone.get(lastIdleJob).remove(lastArrow)){
					jobJobsClone.get(lastIdleJob).add(connectionJob);
				
				//idleJobs achter mekaar plakken in jobRoute.
				ArrayList<Job> jobRouteList = new ArrayList<Job>();
				Set<Entry<Integer,ArrayList<Job>>> entries = jobRouteClone.entrySet();
				for(Entry entry: entries){
					if((Integer) entry.getKey() == randomNumber){
						jobRouteList = jobRouteClone.get(entry.getKey());
					}						
				}
								
				jobRouteList.addAll(idleJobsInJobRoute);
				//idleJObs op de key-plaats verwijderen.
				jobRouteClone.remove(key);
				System.out.println(jobRouteClone);
				System.out.println(jobRoute);
				
				
				
					System.out.println("time < 36000");
					//score berekenen
					//1. #routes
					int tempAmountOfRoutes = jobRouteClone.size();
					int tempNegSumOfRSquaredAbs = 0;
					//2. -|r²|
					negSumOfRsquaredAbs = 0;
					for(int cntr = 0;cntr < jobRoute.size();cntr++){
						if(jobRouteClone.get(cntr) != null){
							int r = jobRouteClone.get(cntr).size();
							int rsquared = (int) Math.pow(r, 2);
							tempNegSumOfRSquaredAbs = negSumOfRsquaredAbs - Math.abs(rsquared);
						}
						else{
							//wanneer een route er wordt uitgehaald wordt deze null. De hashmap wordt echter niet opgeschoven dus size() blijft gelijk.
						}
					}
					
					if(tempAmountOfRoutes < amountOfRoutes){
						if(tempNegSumOfRSquaredAbs < negSumOfRsquaredAbs){
							System.out.println("Improvement found.");
							bestAlternative = alternatives.get(j);
							bestJobRouteClone = jobRouteClone;
							bestIdleJobsClone = idleJobsClone;
							bestJobJobsClone = jobJobsClone;
						}
						if(tempNegSumOfRSquaredAbs == negSumOfRsquaredAbs){
							float time = (float) 0;
							for(int teller = 0; teller<jobRouteClone.size(); teller++){
								ArrayList<Job> idleJobss = jobRouteClone.get(teller);
								for(int teller2 = 0; teller2<idleJobsClone.size(); teller2++){
									ArrayList<Job> jobSequence = jobJobsClone.get(idleJobss.get(teller2));
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
								System.out.println("Improvement found.");
								totalExecutionTime = time;
							}
						}
			
						this.jobJobs  = bestJobJobsClone;
						this.jobRoute = bestJobRouteClone;
						this.idleJobs = bestIdleJobsClone;	
					}				
				}
				else{
					System.out.println("test");					
				}
				}
				else{
					System.out.println("test1 timeconstraint violated.");
				}
				
			}		
		}		
		
		System.out.println("Found job route: " +jobRoute);
		printSolution(jobRoute);
		System.out.println("____________________________________________________");
		//printEndRoute(jobRoute);
		}
	}
	
	public void ondergrensTimeOpt(){
		//Alle idleJobs op een bepaalde index van jobRoute permuteren om zo te optimaliseren in de tijd. 
		//feasibility nog steeds testen.
		//de connecting jobs verwijderen en opnieuw aanmaken voor de nieuwe volgorde. 
		
//		//Clonen om beste so far in op te slaan
//		HashMap<Job, ArrayList<Job>> bestJobJobsClone = (HashMap<Job, ArrayList<Job>>) cloneJobJobs(jobJobs);
//		HashMap<Integer, ArrayList<Job>> bestJobRouteClone = (HashMap<Integer, ArrayList<Job>>) cloneJobRoute(jobRoute);
//		ArrayList<Job> bestIdleJobsClone = (ArrayList<Job>) cloneIdleJobs(idleJobs);
//		
//		//Clonen om elke iteratie in te kutten.
//		HashMap<Job, ArrayList<Job>> JobJobsClone = (HashMap<Job, ArrayList<Job>>) cloneJobJobs(jobJobs);
//		HashMap<Integer, ArrayList<Job>> JobRouteClone = (HashMap<Integer, ArrayList<Job>>) cloneJobRoute(jobRoute);
//		ArrayList<Job> IdleJobsClone = (ArrayList<Job>) cloneIdleJobs(idleJobs);
	
		System.out.println("Start optimizing time within routes.");
		
		Collection<List<Integer>> output = null;
		ArrayList<Job> idleJobsInRoute;
		ArrayList<Job> bestJobPermutation = new ArrayList<Job>();
		HashMap<Job,ArrayList<Job>> bestJobJobs = new HashMap<Job, ArrayList<Job>>();
		
		Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRoute.entrySet();
		for(Entry entry: entrySet){		
			//Een route eruit nemen
			System.out.println(jobRoute);
			idleJobsInRoute = (ArrayList<Job>) entry.getValue();
			//Binnen die route moeten permutaties worden uitgevoerd 
			//Eerst kijken waar de connectionJob zich bevindt. Deze moet eerst verwijderd worden. (--> laatste job)
			
			//Alle permutaties bepalen.
			Permutations<Integer> obj = new Permutations<Integer>();
			Collection<Integer> input = new ArrayList<Integer>();
			for(int j=0;j<idleJobsInRoute.size();j++){
				input.add(j);
			}
			output = obj.permute(input);	
			System.out.println("Generating possible job permutations within route.");
						
			
			float bestTime = (float) Double.POSITIVE_INFINITY;
			float timePermutated;
			
			for (List<Integer> permutation : output) {
				HashMap<Job,ArrayList<Job>> jobJobsClone = (HashMap<Job, ArrayList<Job>>) cloneJobJobs(jobJobs);
				ArrayList<Job> jobPermutation = new ArrayList<Job>();
				for(int j=0;j<permutation.size();j++){					  
					  jobPermutation.add(idleJobsInRoute.get(permutation.get(j)));
				}
				System.out.println(jobPermutation);
				//Van elke job, behalve de laatste, de laatste in sequentie wegdoen.
				for(int j=0;j<permutation.size()-1;j++){
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
				System.out.println("Time for considered permutation: "+timePermutated);
				if(timePermutated < bestTime){
					bestTime = timePermutated;
					bestJobPermutation = jobPermutation;
					bestJobJobs = jobJobsClone;
				}
			}
			
			jobJobs = bestJobJobs;
			entry.setValue(bestJobPermutation);
			System.out.println("Best Job Routes: " + jobRoute);
			
			}
		printSolution(jobRoute);
	}		
	
	public void uberHeuristiekTimeOpt(){
		
		System.out.println("Start optimizing time within routes.");
		System.out.println(jobRoute);
		
		Collection<List<Integer>> output = null;
		ArrayList<Job> idleJobsInRoute;
		ArrayList<Job> bestJobPermutation = new ArrayList<Job>();
		HashMap<Job,ArrayList<Job>> bestJobJobs = new HashMap<Job, ArrayList<Job>>();
		
		Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRoute.entrySet();
		for(Entry entry: entrySet){		
			//Een route eruit nemen
			idleJobsInRoute = (ArrayList<Job>) entry.getValue();
			//Binnen die route moeten permutaties worden uitgevoerd 
			//Eerst kijken waar de connectionJob zich bevindt. Deze moet eerst verwijderd worden. (--> laatste job)
			
			//Alle permutaties bepalen.
			Permutations<Integer> obj = new Permutations<Integer>();
			Collection<Integer> input = new ArrayList<Integer>();
			for(int j=0;j<idleJobsInRoute.size();j++){
				input.add(j);
			}
			output = obj.permute(input);	
			System.out.println("Generating possible job permutations within route.");
						
			
			float timePermutated;

			//TIME ELAPSED BY ORIGINAL SEQUENCE.
			float bestPermutationTime = (float) 0;
			
			for (List<Integer> permutation : output) {
				HashMap<Job,ArrayList<Job>> jobJobsClone = (HashMap<Job, ArrayList<Job>>) cloneJobJobs(jobJobs);
				ArrayList<Job> jobPermutation = new ArrayList<Job>();
				//permutatie van jobs.
				for(int j=0;j<permutation.size();j++){					  
					  jobPermutation.add(idleJobsInRoute.get(permutation.get(j)));
				}
				if(jobPermutation.size()>1){
				float timeStart = (float) 0;
				float timeStartArrow = (float) 0;
				float timeLoadJob = (float) 0;
				float timeIdleJob = (float) 0;
				float timeFirstArrow = (float) 0;
				float timeFirstArrow1 = (float) 0;
				float timeFirstArrow2 = (float) 0;
				float timeLastArrow = (float) 0;
				float timeNewConnectionJob = (float) 0;
				
				//Eerste Job in orde krijgen.
				Job firstIdleJob = jobPermutation.get(0);
				Job firstJobOfIdleJob = jobJobsClone.get(firstIdleJob).get(0);
				//Zorgen dat eerste job in depot vertrekt.
				if(firstJobOfIdleJob.getStartLocation() != Planning.depot){
					//SwitchContainer //Fillcontainer //PlaceClient 
					if(firstJobOfIdleJob.getJobType().equals("SwitchContainer") || firstJobOfIdleJob.getJobType().equals("FillContainer") || firstJobOfIdleJob.getJobType().equals("PlaceClient")){
						Job loadJob = new Job("LoadPlaceDepot",Planning.depot,firstJobOfIdleJob.getStartLocation(),firstJobOfIdleJob.getContainer());
						Job idleJob = new Job(firstJobOfIdleJob.getJobType(),Planning.depot, firstJobOfIdleJob.getStartLocation(), firstJobOfIdleJob.getContainer());								
						timeLoadJob = loadJob.getJobTime();
						timeIdleJob = idleJob.getJobTime();
						timeStart = timeStart + timeLoadJob;
						timeStart = timeStart + timeIdleJob;
					}
					else{
						Job idleJob = new Job(firstJobOfIdleJob.getJobType(),Planning.depot, firstJobOfIdleJob.getStartLocation(), firstJobOfIdleJob.getContainer());
						timeIdleJob = idleJob.getJobTime();
						timeStart = timeStart + timeIdleJob;
					}
				}		
				//zorgen dat eerste job juiste connection heeft.
				int jobJobsSize = jobJobsClone.get(jobPermutation.get(0)).size();					
				timeLastArrow= jobJobsClone.get(jobPermutation.get(0)).get(jobJobsSize-1).getJobTime();
				timeStart = timeStart - timeLastArrow;
				Job newConnectionJob = new Job(jobPermutation.get(1).getJobType(), jobJobsClone.get(jobPermutation.get(0)).get(jobJobsSize-1).getStartLocation(),jobPermutation.get(1).getTargetLocation(), jobJobsClone.get(jobPermutation.get(0)).get(jobJobsSize-1).getContainer());
				timeNewConnectionJob = newConnectionJob.getJobTime();
				timeStart = timeStart + timeNewConnectionJob;
				
				
				//zorgen dat middenste jobs juiste connecties hebben.
				for(int j = 1;j<jobPermutation.size();j++){
					if(jobJobsClone.get(jobPermutation.get(j)).get(0).getStartLocation() == Planning.depot){
						timeFirstArrow1 = jobJobsClone.get(jobPermutation.get(j)).get(0).getJobTime();
						timeFirstArrow2 = (float) 0;
						if(firstJobOfIdleJob.getJobType().equals("SwitchContainer") || firstJobOfIdleJob.getJobType().equals("FillContainer") || firstJobOfIdleJob.getJobType().equals("PlaceClient")){
							timeFirstArrow2 = jobJobsClone.get(jobPermutation.get(j)).get(1).getJobTime();
						}
						timeFirstArrow = timeFirstArrow1 + timeFirstArrow2;
						timeStart = timeStart - timeFirstArrow;
					}
					
					jobJobsSize = jobJobsClone.get(jobPermutation.get(j)).size();					
					timeLastArrow = jobJobsClone.get(jobPermutation.get(j)).get(jobJobsSize-1).getJobTime();
					timeStart = timeStart - timeLastArrow;
					
					try{
						newConnectionJob = new Job(jobPermutation.get(j+1).getJobType(), jobJobsClone.get(jobPermutation.get(j)).get(jobJobsSize-1).getStartLocation(),jobPermutation.get(j+1).getTargetLocation(), jobJobsClone.get(jobPermutation.get(j)).get(jobJobsSize-1).getContainer());
						timeNewConnectionJob = newConnectionJob.getJobTime();
						timeStart = timeStart + timeNewConnectionJob;
					}
					catch(IndexOutOfBoundsException exc){
						newConnectionJob = new Job("LoadPlaceDepot", jobJobsClone.get(jobPermutation.get(j)).get(jobJobsSize-1).getStartLocation(),Planning.depot, jobJobsClone.get(jobPermutation.get(j)).get(jobJobsSize-1).getContainer());
						timeNewConnectionJob = newConnectionJob.getJobTime();
						timeStart = timeStart + timeNewConnectionJob;
					}
					
				}			
				
		
				System.out.println("Time difference for considered permutation: "+timeStart);
				if(timeStart < bestPermutationTime){
					System.out.println("Better solution found.");
					bestPermutationTime = timeStart;
					bestJobPermutation = jobPermutation;
				}
				System.out.println("Best permutation for entry: " + entry.getKey()+ " is: " + bestJobPermutation);	
				}
				else{
					System.out.println("Best permutation for entry: " + entry.getKey()+ " No permutation: only 1 element)");
				}
					
			}
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
		if(suitedAVCS.size() == 0){
			throw new NullPointerException("No valid AVC defined in the system.");
		}
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
		if(suitedAVCS.size() == 0){
			throw new NullPointerException("No valid AVC defined in the system.");
		}		
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
		System.out.println("----------------------------------------------------");
		
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
				
				//Dan plaatsen bij de klant.
				Job job2 = new Job("PlaceClient",containers.get(i).getContainerLocation(),idleJobs.get(i).getTargetLocation(),containers.get(i));
				jobs.add(job2);
				jobtime2 = job2.getJobTime();
				System.out.println("Place container at client. Time: "+jobtime2);
				
				float jobtime = jobtime1 + jobtime2;
				fsTimesFirstStage.put(idleJobs.get(i), jobtime);
				
				Job job3 = new Job("LoadPlaceDepot",containers.get(i).getContainerLocation(),containers.get(i));				
				jobs.add(job3);
				float jobtime3 = job3.getJobTime() - job3.getJobExecutionTime();
				System.out.println("Driving back to depot. Time: " +jobtime3);
				fsTimesSecondStage.put(idleJobs.get(i), jobtime3);
				
				//ArrayList<Job> jobArray = new ArrayList<Job>();
				//jobRoute.put((Integer) i, jobArray);
				jobJobs.put(idleJobs.get(i), jobs);
				fsTimes.put(idleJobs.get(i),(jobtime1+jobtime2+jobtime3));
				System.out.println("Total time for job " + i + " is: "+ (jobtime1+jobtime2+jobtime3));								
				System.out.println("----------------------------------------------------");
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
				System.out.println("Loading container on truck at client. Time: "+ jobtime2);
				
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
				
				Job job5 = new Job("LoadPlaceDepot",job4.getTargetLocation(),containers.get(i));				
				jobs.add(job5);
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
			System.out.println("----------------------------------------------------");
			ArrayList<Job> idleJobsList = new ArrayList<Job>();
			idleJobsList.add(idleJobs.get(i));
			jobRoute.put(i, idleJobsList);
			}
		}
		System.out.println("____________________________________________________");
	}

	public void printSolution(HashMap<Integer, ArrayList<Job>> routeToPrint){
		Set<Entry<Integer, ArrayList<Job>>> entrySet = jobRoute.entrySet();
		System.out.println("____________________________________________________");
		System.out.println("This planning needs " + entrySet.size() + " trucks.");
		
		ArrayList<Float> execTimes = new ArrayList<Float>();
		
		
		for(Entry entry: entrySet){	
			ArrayList<Job> executedJobs = (ArrayList<Job>) entry.getValue();
			float time = 0;
			for(int i=0; i<executedJobs.size();i++){
				ArrayList<Job> jobsInExecutedJob = jobJobs.get(executedJobs.get(i));
				for(int j=0; j<jobsInExecutedJob.size();j++){
					time = time + jobsInExecutedJob.get(j).getJobTime();
					System.out.println("Type: "+ jobsInExecutedJob.get(j).getJobType()+ ", Startlocatie: " + jobsInExecutedJob.get(j).getStartLocation() + ", Targetlocation: " + jobsInExecutedJob.get(j).getTargetLocation()+ ", Time: "+time);
					
				}	
				if(i == executedJobs.size()-1){
					execTimes.add(time);						
				}
			}
			System.out.println("----------------------------------------------------");
		}
		float totalExecTime = (float) 0;
		for(int i=0;i<execTimes.size(); i++){
			totalExecTime = totalExecTime + execTimes.get(i);
		}
		
		
		System.out.println("Total time: "+ totalExecTime);
	}
	
	public void printEndRoute(HashMap<Integer,ArrayList<Job>> route){
		Set<Entry<Integer, ArrayList<Job>>> entrySet = route.entrySet();
		for(Entry entry: entrySet){	
			System.out.println("Route:");
			ArrayList<Job> executedJobs = (ArrayList<Job>) entry.getValue();
			for(int i = 0; i< executedJobs.size(); i++){
				Client client = (Client) executedJobs.get(i).getTargetLocation();
				System.out.println("Go to client: " + client.getName());
			}
		}
	
	}
}



	
			
			
	
		
		