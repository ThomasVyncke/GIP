import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;


public class Test {

	public static void main(String[] args) {		
		
		Planning planning = new Planning("Thomas");		
		
		Client client1 = new Client(1,"a",0, 36000, (float) 50.3,(float) 4.716051,"Paper",true,0,15);
		Client client2 = new Client(2,"b",0, 36000, (float) 50.818181,(float) 4.902819,"Paper",true,0,15);
		planning.putClient(client1);
		planning.putClient(client2);
		
		
		//LoadOnTruck --> container bij klant ingeven. + 1 (dummy, anders is containers.size() != idleJobs.size())
		//FillContainer --> container bij depot ingeven.
		//SWitchContainer --> 2 containers ingeven.
		Container container = new Container(1,15,client1,true,"");
		planning.addContainer(container);
		Container container1 = new Container(2,15,Planning.depot,true,"");
		planning.addContainer(container1);	
		Container container2 = new Container(2,15,client2,true,"");
		planning.addContainer(container2);
		Container container3 = new Container(2,15,Planning.depot,true,"");
		planning.addContainer(container3);
		
		ArrayList<String> wasteType = new ArrayList<String>();
		wasteType.add(0,"Paper");
		AVC avc = new AVC(1,(float) 50.5,(float) 4.774871, 0,36000,wasteType);
		planning.addAVC(avc);
		
		Job job1 = new Job("FillContainer", Planning.depot,client1,container1);
		planning.putJob(job1);	
		
		Job job2 = new Job("SwitchContainer", Planning.depot,client2,container2);
		planning.putJob(job2);
		
		
		
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
		
		
		
	}	
}
