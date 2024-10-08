package clouds.google.cloudstorage;

import java.util.List;

import interfaces.AnalysisResult;
import interfaces.LogAnalyser;

public class GSLogAnalyser implements LogAnalyser {
	
	private List<GSLog> logs;
	private List<String> reads;
	
	private int totalreads;
	private int loggedreads;
	
	public GSLogAnalyser(List<GSLog> list, List<String> readIDs) {
		this.logs = list;
		this.reads = readIDs;
		this.loggedreads = 0;
	}
	
	public void analyse() {
		totalreads = reads.size();
		for (String idString : reads) {
			logs.forEach(a -> {
				a.getRequestID().forEach(b -> {
					if (idString.equals(b)) {
						loggedreads++;
					}
				});
			});
		}
	}
	
	public AnalysisResult getResult() {
		AnalysisResult result = new AnalysisResult("GOOGLE-STORAGE");
		result.setTotalReads(totalreads);
		result.setLoggedReads(loggedreads);
		return result;
	}
}
