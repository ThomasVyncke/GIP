import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;


public class Test {

	public static void main(String[] args) {		
		
		Planning planning = new Planning("Thomas");		
		
		Client client1 = new Client(1,"A",0, 36000, (float) 51.246,(float) 4.380723,"paper",true,0,20);
		Client client2 = new Client(2,"B",0, 36000, (float) 50.83,(float) 4.404,"general",true,900,15);
		Client client3 = new Client(3,"C",0, 18000, (float) 50.873145,(float) 4.696,"general",false,0,20);
		Client client4 = new Client(4,"D",0, 18000, (float) 51.017,(float) 4.478,"general",false,0,18);
		Client client5 = new Client(5,"E",0, 36000, (float) 50.83,(float) 4.404,"general",false,900,15);
		Client client6 = new Client(6,"F",0, 36000, (float) 51.05,(float) 3.758,"buildingMaterial",false,0,20);
		Client client7 = new Client(7,"G",0, 36000, (float) 50.873145,(float) 4.696,"paper",true,0,20);
		Client client8 = new Client(8,"H",0, 36000, (float) 50.926343,(float) 5.3845,"paper",true,900,18);
		Client client9 = new Client(9,"I",0, 36000, (float) 50.65,(float) 5.594,"general",false,0,20);
		Client client10 = new Client(10,"J",0, 36000, (float) 50.873145,(float) 4.696,"buildingMaterial",false,0,20);
		Client client11 = new Client(11,"K",0, 18000, (float) 51.246,(float) 4.380723,"general",false,0,18);
		Client client12 = new Client(12,"L",0, 36000, (float) 51.017,(float) 4.478,"general",true,0,20);
		Client client13 = new Client(13,"M",0, 36000, (float) 50.83,(float) 4.404,"general",false,0,20);
		Client client14 = new Client(14,"N",0, 18000, (float) 50.873145,(float) 4.696,"general",false,0,15);
		Client client15 = new Client(14,"O",0, 36000, (float) 51.18,(float) 3.2531,"general",false,0,20);

		System.out.println("Loading clients into the system...");
		planning.putClient(client1);
		planning.putClient(client2);
		planning.putClient(client3);
		planning.putClient(client4);
		planning.putClient(client5);
		planning.putClient(client6);
		planning.putClient(client7);
		planning.putClient(client8);
		planning.putClient(client9);
		planning.putClient(client10);
		planning.putClient(client11);
		planning.putClient(client12);
		planning.putClient(client13);
		planning.putClient(client14);
		planning.putClient(client15);
		
		System.out.println("Generating right containers...");				
		//LoadOnTruck --> container bij klant ingeven. + 1 (dummy, anders is containers.size() != idleJobs.size())
		//FillContainer --> container bij depot ingeven.
		//SWitchContainer --> 2 containers ingeven.
		Container container1 = new Container(1,20,client1,false,"paper",client1);	
		Container container1bis = new Container(1111,20,Planning.depot,false,"paper");
		Container container2 = new Container(2,15,client2,false,"general",client2);
		Container container21 = new Container(21,15,Planning.depot,false,"general");
		Container container3 = new Container(3,15,client3,false,"general");
		Container container31 = new Container(31,15,Planning.depot,true,"general");
		Container container4 = new Container(4,18,client4,false,"general");
		Container container41 = new Container(41,18,Planning.depot,true,"general");
		Container container5 = new Container(5,15,client5,false,"general");
		Container container51 = new Container(51,15,Planning.depot,true,"general");
		Container container6 = new Container(6,20,client6,false,"buildingMaterial");
		Container container61 = new Container(61,20,Planning.depot,true,"buildingMaterial");
		Container container7 = new Container(7,20,client7,false,"paper",client7);
		Container container71 = new Container(71,20,Planning.depot,false,"paper");
		Container container8 = new Container(8,18,client8,false,"dangerousWaste",client8);
		Container container81 = new Container(81,18,Planning.depot,false,"dangerousWaste");
		Container container9 = new Container(9,20,client9,false,"general");
		Container container91 = new Container(91,20,Planning.depot,true,"general");
		Container container10 = new Container(10,20,client10,false,"buildingMaterial");
		Container container101 = new Container(101,20,Planning.depot,true,"buildingMaterial");
		Container container11 = new Container(11,18,client11,false,"general");
		Container container111 = new Container(111,18,Planning.depot,true,"general");
		Container container12 = new Container(12,20,client12,false,"general",client12);
		Container container121 = new Container(121,20,Planning.depot,false,"general");
		Container container13 = new Container(13,20,client13,false,"general");
		Container container131 = new Container(131,20,Planning.depot,true,"general");
		Container container14 = new Container(14,15,client14,false,"general");
		Container container141 = new Container(141,15,Planning.depot,true,"general");
		Container container15 = new Container(15,20,client15,false,"general");
		Container container151 = new Container(151,20,Planning.depot,true,"general");
		planning.addContainer(container151);
		planning.addContainer(container15);
		planning.addContainer(container141);
		planning.addContainer(container14);
		planning.addContainer(container131);
		planning.addContainer(container13);
		planning.addContainer(container121);
		planning.addContainer(container12);
		planning.addContainer(container111);
		planning.addContainer(container11);
		planning.addContainer(container101);
		planning.addContainer(container10);
		planning.addContainer(container91);
		planning.addContainer(container9);
		planning.addContainer(container81);
		planning.addContainer(container8);
		planning.addContainer(container71);
		planning.addContainer(container7);
		planning.addContainer(container61);
		planning.addContainer(container6);
		planning.addContainer(container51);
		planning.addContainer(container5);
		planning.addContainer(container41);
		planning.addContainer(container4);
		planning.addContainer(container31);
		planning.addContainer(container3);
		planning.addContainer(container21);
		planning.addContainer(container2);
		planning.addContainer(container1bis);
		planning.addContainer(container1);
		
		System.out.println("Putting AVC's into the system...");
		
		//Mechelen
		ArrayList<String> wasteType1 = new ArrayList<String>();
		wasteType1.add("paper"); 
		wasteType1.add("buildingMaterial");
		wasteType1.add("general");
		AVC avc1 = new AVC(1, (float) 51.017,(float) 4.478, 0,36000,wasteType1);
		planning.addAVC(avc1);
		
		//AVC in Antwerpen ontdubbelen.
		ArrayList<String> wasteType2 = new ArrayList<String>();
		wasteType2.add("buildingMaterial");
		AVC avc2 = new AVC(2,  (float) 51.246,(float) 4.380723, 0,36000,wasteType2);
		planning.addAVC(avc2);
		
		ArrayList<String> wasteType3 = new ArrayList<String>();
		wasteType3.add("dangerousWaste"); 
		AVC avc3 = new AVC(3,  (float) 51.246,(float) 4.380723, 18000,32400,wasteType3);
		planning.addAVC(avc3);
		
		//Gent
		ArrayList<String> wasteType4 = new ArrayList<String>();
		wasteType4.add("paper"); 
		wasteType4.add("buildingMaterial");
		wasteType4.add("general");
		AVC avc4 = new AVC(4, (float) 51.05,(float) 3.758, 0,36000,wasteType4);
		planning.addAVC(avc4);
		
		
		System.out.println("Generating different jobs to execute...");
		
		Job job1 = new Job("LoadOnTruck", Planning.depot,client1,container1);
		planning.putJob(job1);	
		
		Job job2 = new Job("LoadOnTruck", Planning.depot,client2,container2);
		planning.putJob(job2);
		
		Job job3 = new Job("SwitchContainer", Planning.depot,client3,container31);
		planning.putJob(job3);
		
		Job job4 = new Job("SwitchContainer", Planning.depot,client4,container41);
		planning.putJob(job4);
		
		Job job5 = new Job("SwitchContainer", Planning.depot,client5,container51);
		planning.putJob(job5);
		
		Job job6 = new Job("SwitchContainer", Planning.depot,client6,container61);
		planning.putJob(job6);

		Job job7 = new Job("LoadOnTruck", Planning.depot,client7,container7);
		planning.putJob(job7);
		
		Job job8 = new Job("LoadOnTruck", Planning.depot,client8,container8);
		planning.putJob(job8);
		
		Job job9 = new Job("SwitchContainer", Planning.depot,client9,container91);
		planning.putJob(job9);
		
		Job job10 = new Job("SwitchContainer", Planning.depot,client10,container101);
		planning.putJob(job10);
		
		Job job11 = new Job("SwitchContainer", Planning.depot,client11,container111);
		planning.putJob(job11);
		
		Job job12 = new Job("LoadOnTruck", Planning.depot,client12,container12);
		planning.putJob(job12);
		
		Job job13 = new Job("PlaceClient", Planning.depot,client13,container131);
		planning.putJob(job13);
		
		Job job14 = new Job("SwitchContainer", Planning.depot,client14,container141);
		planning.putJob(job14);
		
		Job job15 = new Job("SwitchContainer", Planning.depot,client15,container151);
		planning.putJob(job15);
		
		
		
		
		//Job job2 = new Job("EmptyAVC", client2,avc,container1);
//		System.out.println("job1 executionTime = " + job1.getJobExecutionTime());
		//System.out.println("job2 executionTime = " + job2.getJobExecutionTime());
//		System.out.println("client1 to avc dist = "+ client1.getDistanceTo(avc));
		//System.out.println("client2 to avc dist = "+ client2.getDistanceTo(avc));
//		System.out.println("client1 to avc traveltime = "+ client1.getTravelTimeTo(avc));
		//System.out.println("client2 to avc traveltime = "+ client2.getTravelTimeTo(avc));
//		System.out.println("job1 jobTime = " + job1.getJobTime());
		//System.out.println("job2 jobTime = " + job2.getJobTime());
		
		planning.feasibleSolution();
		planning.uberHeuristiekRouteOpt();		
		planning.uberHeuristiekTimeOpt();
		planning.ondergrensTimeOpt();
		
		
		
	}	
}
