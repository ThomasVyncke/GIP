import java.util.Collection;
import java.util.Date;
import java.util.HashMap;


public class Planning {

	private Date planningDate;
	private String author;
	private HashMap<Integer,Job> jobs;
	
	/**
	 * @param planningDate
	 * @param author
	 * @param jobs: HashMap containing all the jobs that need to be processed during the planningDate.
	 */
	public Planning(Date planningDate, String supervisor, HashMap<Integer, Job> jobs){
		this.planningDate = planningDate;
		this.author = supervisor;
		this.jobs = jobs;
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
