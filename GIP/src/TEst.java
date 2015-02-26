import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;


public class TEst {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		Client client1 = new Client(1,"Gille",(float) 50.873459,(float) 4.716051,"boeitnogniet","Paper",true,0);
		Client client2 = new Client(1,"Robin",(float) 50.818181,(float) 4.902819,"boeitnogniet","Paper",true,0);
		ArrayList<String> wasteType = new ArrayList<String>();
		wasteType.add(0,"Paper");
		AVC avc = new AVC(1,(float) 50.859498,(float) 4.774871, 9,5,wasteType);
		//Make method to define closest AVC with same waste type processing.
		Job job1 = new Job(3,23,"EmptyAVC", client1,avc);
		Job job2 = new Job(4,23,"EmptyAVC", client2,avc);
		System.out.println("job1 executionTime = " + job1.getJobExecutionTime());
		System.out.println("job2 executionTime = " + job2.getJobExecutionTime());
		System.out.println("client1 to avc dist = "+ client1.getDistanceTo(avc));
		System.out.println("client2 to avc dist = "+ client2.getDistanceTo(avc));
		System.out.println("client1 to avc traveltime = "+ client1.getTravelTimeTo(avc));
		System.out.println("client2 to avc traveltime = "+ client2.getTravelTimeTo(avc));
		System.out.println("job1 jobTime = " + job1.getJobTime());
		System.out.println("job2 jobTime = " + job2.getJobTime());
		
		
		
	}	
}
