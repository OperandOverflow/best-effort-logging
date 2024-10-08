package user;

import java.util.ArrayList;
import java.util.List;

import actors.Auditor;
import actors.Janitor;
import actors.Reader;
import actors.Writer;
import clouds.CloudFactory;
import controllers.AuditController;
import controllers.JanitorController;
import controllers.ReadController;
import controllers.WriteController;
import main.App;

/**
 * Starter engine of the program, prepares the environment.
 */
public class Starter {
	
	private static final String PREFIX = " [Starter] ";

	private static AppConfiguration config;
	
	/**
	 * Initiates variables and actors
	 * @param args command line passed arguments
	 */
	public static void Start(String... args) {
		// 
		App.info(PREFIX +  "Began preparing execution environment.");
		
		config = args.length >= 1 ? new AppConfiguration(args[0]) : new AppConfiguration("");
		StartupInfoObject info = new StartupInfoObject();
		
		// Values
		info.setNumberReads(config.getNumberReads());
		info.setObjectSize(config.getObjectSize());
		info.setReadInterval(config.getReadingInterval());
		info.setAuditInstants(config.getAuditInstants());
		
		// DataContainer
		DataContainer container = DataContainer.getInstance();
		info.setDataContainer(container);
		
		String[] cloudnameStrings = config.getLoadClouds();
		for (String string : cloudnameStrings)
			container.addCloud(CloudFactory.getCloud(string));
		
		// Actors
		List<Writer> writers = new ArrayList<Writer>();
		for (int i = 0; i < config.getNumberWriters(); i++) 
			writers.add(new Writer(new WriteController(container)));
		info.setListWriters(writers);
		
		List<Reader> readers = new ArrayList<Reader>();
		for (int i = 0; i < config.getNumberReaders(); i++) 
			readers.add(new Reader(new ReadController(container)));
		info.setListReaders(readers);
		
		List<Auditor> auditors = new ArrayList<Auditor>();
		for (int i = 0; i < config.getNumberAuditors(); i++) 
			auditors.add(new Auditor(new AuditController(container)));
		info.setListAuditors(auditors);
		
		Janitor janitor = new Janitor(new JanitorController(container));
		info.setJanitor(janitor);
		
		// Debug
		info.setRedirectOutput(config.getRedirectOutput());
		
		App.info(PREFIX +  "Preparation finished, handing over to User.");
		
		// Start main execution
		User user = new User(info);
		user.execute();
	}
}
