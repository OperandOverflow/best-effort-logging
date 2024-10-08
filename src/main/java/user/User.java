package user;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import actors.Auditor;
import actors.Reader;
import actors.Writer;
import main.App;
import main.AppLogger;

public class User {
	
	private final String PREFIX = "    [User] ";
	
	private StartupInfoObject info;
	
	public User(StartupInfoObject startup) {
		info = startup;
	}
	
	public void execute() {
		
		if (info.redirectoutput)
			AppLogger.changeoutput();
		
		App.info(PREFIX + "Started execution.");
		
		App.info(PREFIX + "Writing...");
		// Write object
		for (Writer writer : info.writers)
			writer.write(info.numberreads, info.objectsize, info.readinterval);
		
		App.info(PREFIX + "Reading...");
		// Read object
		for (int i = 0; i < info.numberreads; i++) {
			for (Reader reader : info.readers)
				reader.read();
			try {
				Thread.sleep(info.readinterval);
			} catch (InterruptedException e) {
				App.warn(PREFIX + "Interruption while waiting for next reading!");
			}
		}
		
		App.info(PREFIX + "Auditing...");
		// Audit logs
		Timer timer = new Timer();
		for (int i = 0; i < info.auditinstants.length; i++) {
			timer.schedule(new AuditTask(info, Long.toString(info.auditinstants[i]), timer), info.auditinstants[i] * 1000);
		}
		
		if (info.redirectoutput)
			AppLogger.restoreoutput();
		
	} 
	
	private class AuditTask extends TimerTask {
		
		private final String instantString;
		private final StartupInfoObject infoObject;
		private final Timer timer;
		
		private AuditTask(StartupInfoObject info, String instant, Timer t) { 
			instantString = instant;
			infoObject = info;
			timer = t;
		} 
		
		public void run() {
			for (Auditor auditor : info.auditors) {
				List<Integer> result = auditor.audit(instantString, 
						infoObject.numberreads, infoObject.objectsize, infoObject.readinterval);
				boolean alldone = true;
				for (Integer integer : result)
					alldone = alldone && (integer == info.numberreads);
				if (alldone) {
					timer.cancel();
					info.janitor.cleanUp(alldone);
				}
			}
		} 
	}
}
