import java.util.Date;


public class Planning {

	private Date planningDate;
	private String author;
	
	public Planning(Date planningDate, String author, Client client, Depot depot, AVC avc){
		this.planningDate = planningDate;
		this.author = author;
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
