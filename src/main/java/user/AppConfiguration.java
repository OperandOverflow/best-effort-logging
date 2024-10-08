package user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * A class that reads the properties file and extracts configuration information for the program
 */
public class AppConfiguration {
	
	private final String PREFIX = "  [AppConfiguration] ";
	
	//0sec, 30sec, 1min, 3min, 5min, 10min, 15min, 30min, 1h, 3h, 6h, 12h, 24h, 48h
	private final long[] INSTANTS = 
			new long[] {0, 30, 60, 180};
	private final String[] CLOUDS = new String[] {"AMAZONS3"};
	
	private Properties config;
	
	/**
	 * Creates an instance of AppConfiguration
	 * @param path path to the configuration file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public AppConfiguration(String path){
		config = new Properties();
		try {
			config.load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println(PREFIX + "Couldn't find the '.properties' file!");
			System.out.println(PREFIX + "Swtiching to default configuration values.");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(PREFIX + "Error while trying to read the 'properties' file!");
			System.out.println(PREFIX + "Swtiching to default configuration values.");
		}
	}
	
	/**
	 * Returns the number of readings
	 * @return number of readings
	 */
	public int getNumberReads() {
		return Integer.parseInt(config.getProperty("number-reads", "10"));
	}
	
	/**
	 * Returns the size, in bytes, of the object to generate
	 * @return size of the object
	 */
	public long getObjectSize() {
		return Long.parseLong(config.getProperty("object-size", "1024"));
	}
	
	/**
	 * Returns the interval between reads, in milliseconds.
	 * @return interval between reads
	 */
	public long getReadingInterval() {
		return Long.parseLong(config.getProperty("read-interval", "1000"));
	}
	
	/**
	 * Returns an array of auditing instants, in seconds
	 * @return array of instants to perform audits, the default instants are:
	 * 		0sec, 30sec, 1min, 3min, 5min, 10min, 15min, 30min, 1h, 3h, 6h, 12h, 24h, 48h.
	 */
	public long[] getAuditInstants() {
		String numbers = config.getProperty("audit-instants");
		if (numbers == null)
			return INSTANTS;
		String[] numberStrings = numbers.replace(" ", "").split(",");
		if (numberStrings.length == 0)
			return INSTANTS;
		long[] result = new long[numberStrings.length];
		for (int i = 0; i < numberStrings.length; i++) 
			result[i] = Long.parseLong(numberStrings[i]);
		return result;
	}
	
	/**
	 * Returns an array of names of clouds to load
	 * @return array of names of clouds to load, the default names are:
	 * 		AMAZONS3.
	 */
	public String[] getLoadClouds() {
		String clouds = config.getProperty("load-clouds");
		if (clouds == null)
			return CLOUDS;
		String[] cloudStrings = clouds.replace(" ", "").split(",");
		if (cloudStrings.length == 0)
			return CLOUDS;
		return cloudStrings;
	}
	
	/**
	 * Returns the number of writers
	 * @return number of writers, the default value is 1.
	 */
	public int getNumberWriters() {
		return Integer.parseInt(config.getProperty("number-writers", "1"));
	}
	
	/**
	 * Returns the number of readers
	 * @return number of readers, the default value is 1.
	 */
	public int getNumberReaders() {
		return Integer.parseInt(config.getProperty("number-readers", "1"));
	}
	
	/**
	 * Returns the number of auditors
	 * @return number of auditors, the default value is 1.
	 */
	public int getNumberAuditors() {
		return Integer.parseInt(config.getProperty("number-auditors", "1"));
	}
	
	/**
	 * Returns true if the user wants the console output to be redirected to a file
	 * @return true if want the output to be redirected to a file, default value is false.
	 */
	public boolean getRedirectOutput() {
		return Boolean.parseBoolean(config.getProperty("redirect-console-output", "false"));
	}
	
}
