package clouds.aws.s3cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.AnalysisResult;
import interfaces.LogAnalyser;

public class S3LogAnalyser implements LogAnalyser {
	
	private static final String PREFIX = "                [S3LogAnalyser] ";
	
	private List<S3Log> logs;
	private List<String> reads;
	
	private int totalreads;
	private int loggedreads;
//	private List<S3Log> lostreads;
	
	
	public S3LogAnalyser(List<S3Log> list, List<String> reads) {
		this.logs = list;
		this.reads = reads;
		
		this.totalreads = 0;
		this.loggedreads = 0;
//		this.lostreads = new ArrayList<S3Log>();
	}
	
	public void analyse() {
		this.totalreads = reads.size();
		for (String idString : reads) {
			// Iterate through all logs
			logs.forEach(a -> {
				// Get IDs in each log file
				a.getRequestID().forEach(b -> {
					// Compare
					if (b.equals(idString))
						loggedreads++;
				});
			});
		}
		//TODO
	}
	
	public AnalysisResult getResult() {
		AnalysisResult result = new AnalysisResult("AMAZONS3");
		result.setTotalReads(this.totalreads);
		result.setLoggedReads(this.loggedreads);
		return result;
	}
}
