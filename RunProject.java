import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;
import java.io.FileWriter;

public class RunProject {

	private static final String MAX_INSTANCES = "4";
	
//	private static final String NUM_READINGS = "1, 2";
	private static final String NUM_READINGS = "1, 10, 100, 1000";
	
	//64B, 256B, 512B, 1KiB, 2KiB, 10KiB, 100KiB, 500KiB, 1MiB
//	private static final String FILE_SIZES = "64";
	private static final String FILE_SIZES = "64, 256, 512, 1024, 2048, 10240, 102400, 512000, 1048576";

//	private static final String INTERVALS = "10000, 0";
	private static final String INTERVALS = "0, 1, 10, 100, 1000";

	private static final Path PROPERTIES = Paths.get(".", "properties");

	private static final String LN = System.getProperty("line.separator");

	private static final String PROPERTIES_FILE_TEMP = 
		"#Auto generated properties file" + LN + 
		"number-reads=%d" + LN +
		"object-size=%d" + LN + 
		"read-interval=%d" + LN +
		"audit-instants=0, 30, 60, 180, 300, 600, 900, 1800, 2700, 3600, 4500, 5400, 6300, 7200, 8100, 9000, 9900, " + 
		"10800, 11700, 12600, 13500, 14400, 15300, 16200, 17100, 18000" + LN +
		"number-writers=1" + LN + 
		"number-readers=1" + LN +
		"number-auditors=1" + LN + 
		"redirect-console-output=false";
	
	private static Properties configProperties;

	private static int maxInstances;
	private static int[] numReadings;
	private static Long[] fileSizes;
	private static Long[] readingIntevals;

	private static List<ProcessExitDetector> waitingProcesses = new LinkedList<ProcessExitDetector>();
	private static int processCounter = 0;
	private static int runningProcesses = 0;

	private static Object lock = new Object();
	
    public static void main(String... args) {
        try {
			config(args);
			prepareProcesses();
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


	private static void execute() throws IOException{
		while (runningProcesses < maxInstances && processCounter < waitingProcesses.size()) {
			ProcessExitDetector exitDetector = waitingProcesses.get(processCounter);
			Thread newThread = new Thread(exitDetector);
			try {
				newThread.start();
				synchronized(lock) {
					lock.wait();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Finished reading, proceding to the next thread.");
			processCounter++; 
			runningProcesses++;
		}

	}


	private static void prepareProcesses() {
		for (int i = 0; i < numReadings.length; i++) {
			for (int j = 0; j < fileSizes.length; j++) {
				for (int j2 = 0; j2 < readingIntevals.length; j2++) {
					ProcessExitDetector exitDetector = new ProcessExitDetector(numReadings[i], fileSizes[j], readingIntevals[j2]);
					waitingProcesses.add(exitDetector);
				}
			}
		}
	}
    
	private static synchronized void processFinished() {
		runningProcesses--;
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static File createProperties(int numreads, long size, long interval) {
		PROPERTIES.toFile().mkdirs();
		String fileName = numreads + "reads," + size + "bytes," + interval + "msec.properties";
		File propertiesFile = Paths.get(PROPERTIES.toString(), fileName).toFile();
		try {
			propertiesFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(propertiesFile));
			bw.write(String.format(PROPERTIES_FILE_TEMP, numreads, size, interval));
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return propertiesFile;
	}

	

    private static void config(String... args) throws IOException{
    	configProperties = new Properties();
    	if (args.length >= 1) {
			try {
				configProperties.load(new FileInputStream(new File(args[0])));
			} catch (FileNotFoundException e) {
				System.err.println("Configuration file not found, continuing with default values.");
			}
		}
		

		maxInstances = Integer.parseInt(configProperties.getProperty("max-instances", MAX_INSTANCES));

		String intString = configProperties.getProperty("number-readings", NUM_READINGS);
		String[] ints = intString.replace(" ", "").split(",");
		numReadings = new int[ints.length];
		for (int i = 0; i < ints.length; i++)
			numReadings[i] = Integer.parseInt(ints[i]);
		
		String sizesString = configProperties.getProperty("file-sizes", FILE_SIZES);
		String[] sizes = sizesString.replace(" ", "").split(",");
		fileSizes = Stream.of(sizes)
						  .map(a -> Long.parseLong(a))
						  .toArray(Long[] :: new);

		String intervString = configProperties.getProperty("reading-intervals", INTERVALS);
		String[] intervals = intervString.replace(" ", "").split(",");
		readingIntevals = Stream.of(intervals)
							    .map(a -> Long.parseLong(a))
								.toArray(Long[] :: new);
		
    }



	/**
	 * Class inspired from <a>https://beradrian.wordpress.com/2008/11/03/detecting-process-exit-in-java/</a>
	 */
	private static class ProcessExitDetector implements Runnable {
		private static final long MVN_STARTING_TIME = 10000;
		private static final long READ_TIME = 2000;

		private ProcessBuilder pBuilder;
		private long waitingTime;

		protected ProcessExitDetector(int numReads, long fileSize, long interval) {
			File propertiesFile = createProperties(numReads, fileSize, interval);
			pBuilder = new ProcessBuilder(
						"cmd.exe",
						"/c",
						"start",
						"/WAIT",
						"\"" + numReads + " reads, " + fileSize + " bytes, " + interval + " msec\"",
						"mvn",
						"exec:java",
						"-Dexec.mainClass=\"main.App\"",
						"-Dexec.args=\"" + propertiesFile.getPath() + "\""
					);
			waitingTime = MVN_STARTING_TIME + (READ_TIME + interval) * numReads;
		}

		public void run() {
			try {
				Process process = pBuilder.start();
				synchronized(this) {
					wait(waitingTime);
				}
				synchronized (lock) {
					lock.notifyAll();
				}
				process.waitFor();
				System.out.println("Process " + process.toString() + " finished.");
				processFinished();
			} catch (InterruptedException e) {
				e.printStackTrace();
				synchronized (lock) {
					lock.notify();
					processFinished();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
