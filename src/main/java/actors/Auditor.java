package actors;

import java.util.List;

import controllers.AuditController;
import main.App;

public class Auditor {
	
	private static final String PREFIX = "       [Auditor] ";
	
	private AuditController controller;
	
	public Auditor(AuditController actrl) {
		this.controller = actrl;
	}
	
	public List<Integer> audit(String instant, int numreads, long filesize, long interval) {
		App.info(PREFIX + "Auditing at instant " + instant);
		controller.readLogs();
		controller.processLogs();
		controller.analyseLogs();
		List<Integer> result = controller.writeResults(instant, numreads, filesize, interval);
		return result;
	}
}
