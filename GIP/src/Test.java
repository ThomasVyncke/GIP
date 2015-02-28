import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;


public class Test {

	public static void main(String[] args) {
		Planning planning = new Planning("1","Thomas");
		
		
		Client client1 = new Client(1,"a",(float) 50.873459,(float) 4.716051,"boeitnogniet","Paper",true,0);
		//Client client2 = new Client(1,"b",(float) 50.818181,(float) 4.902819,"boeitnogniet","Paper",true,0);
		planning.putClient(client1);
		//planning.putClient(client2);
		
		Container container = new Container(1,1,Planning.depot,true,"");
		planning.addContainer(container);
		//Container container1 = new Container(2,1,Planning.depot,true,"");
		//planning.addContainer(container1);
		
		ArrayList<String> wasteType = new ArrayList<String>();
		wasteType.add(0,"Paper");
		AVC avc = new AVC(1,(float) 50.859498,(float) 4.774871, 9,5,wasteType);
		planning.addAVC(avc);
		
		Job job1 = new Job("FillContainer", Planning.depot,client1,container);
		planning.putJob(job1);		
		
		//Job job2 = new Job("EmptyAVC", client2,avc,container1);
		System.out.println("job1 executionTime = " + job1.getJobExecutionTime());
		//System.out.println("job2 executionTime = " + job2.getJobExecutionTime());
		System.out.println("client1 to avc dist = "+ client1.getDistanceTo(avc));
		//System.out.println("client2 to avc dist = "+ client2.getDistanceTo(avc));
		System.out.println("client1 to avc traveltime = "+ client1.getTravelTimeTo(avc));
		//System.out.println("client2 to avc traveltime = "+ client2.getTravelTimeTo(avc));
		System.out.println("job1 jobTime = " + job1.getJobTime());
		//System.out.println("job2 jobTime = " + job2.getJobTime());
		
		planning.feasibleSolution();
		
		
		
	}	
}
