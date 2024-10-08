package interfaces;

public class AnalysisResult {
	
	private String cloudname;
	private int totalreads;
	private int loggedreads;
	
	public AnalysisResult(String cloud) {
		cloudname = cloud;
		totalreads = 0;
		loggedreads = 0;
	}
	
	public void setTotalReads(int reads) {
		this.totalreads = reads;
	}
	
	public void setLoggedReads(int reads) {
		this.loggedreads = reads;
	}
	
	public String getCloudName() {
		return this.cloudname;
	}
	
	public int getTotalReads() {
		return this.totalreads;
	}
	
	public int getLoggedReads() {
		return this.loggedreads;
	}
	
}