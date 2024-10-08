package clouds.aws.s3cloud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.MethodNotSupportedException;

import interfaces.AnalysisResult;
import interfaces.CloudBucket;
import interfaces.CloudInfo;
import interfaces.CloudType;
import interfaces.Handler;
import main.App;

/**
 * A container that saves all necessary information about Amazon S3 Cloud Storage
 */
public class S3CloudInfo implements CloudInfo{
	
	private static final String PREFIX = "             [S3CloudInfo] ";
	
	//Singleton
	private static S3CloudInfo instance = null;
	
	//Type
	private static CloudType type = CloudType.AMAZONS3;
	
	//Tools
	private S3Handler handler;
	private S3LogAnalyser analyser;
	
	//Data
	/* List of buckets created during the run */
	private List<CloudBucket> buckets;
	/* List of log buckets created during the run */
	private List<CloudBucket> logbuckets;
	
	/* List of keys associated with uploaded files */
	private List<String> keys;
	
	/* List of request IDs from reading files */
	private List<String> readIDs;
	private Set<String> logpaths;
	private List<S3Log> logs; 
	
	private S3CloudInfo() {
		handler 	= new S3Handler();
		buckets 	= new ArrayList<CloudBucket>();
		logbuckets 	= new ArrayList<CloudBucket>();
		keys 		= new ArrayList<String>();
		readIDs 	= new ArrayList<String>();
		logpaths 	= new HashSet<String>();
		logs 		= new ArrayList<S3Log>();
	}
	
	/**
	 * Singleton and lazy initialization
	 * @return An instance of S3CloudInfo
	 */
	public static synchronized S3CloudInfo getInstance() {
		if (instance == null)
			instance = new S3CloudInfo();
		return instance;
	}

	public CloudType getCloudType() {
		return type;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public void createBucket(String name) {
		App.info(PREFIX + "Creating bucket...");
		CloudBucket bucket = handler.createBucket(name);
		buckets.add(bucket);
		App.info(PREFIX + "Creating log bucket...");
		CloudBucket logbucket = handler.createBucket("logbucket-for-" + name);
		logbuckets.add(logbucket);
		App.info(PREFIX + "Activating logging...");
		handler.activateLogging(bucket, logbucket);
	}
	
	public void putCloudObject(String bucket, Path path, String key) {
		App.info(PREFIX + "Putting object...");
		for (CloudBucket b : buckets) {
			if (b.getName().equals(bucket)) {
				handler.putCloudObject(b, path, key);
				keys.add(key);
			}
		}
	}

	public void getCloudObjects(Path path) {
		App.info(PREFIX + "Retrieving objects from buckets...");
		for (CloudBucket b : buckets) {
			for (String key : b.getKeys()) {
				Path filepath = Paths.get(path.toString(), key);
				File file = new File(filepath.toString());
				file.delete();
				readIDs.add(handler.getCloudObjectFast(b, key, filepath));
			}
		}
		//TODO remove the print
		App.info(PREFIX + "Completed readings: " + readIDs.size());
	}
	
	public void getCloudObject(String key, Path path) {
		for (CloudBucket b : buckets)
			readIDs.add(handler.getCloudObject(b, key, path));
	}

	
	public List<String> getReadIDs() {
		return readIDs;
	}
	
	
	public void getLogs(Path path) {
		App.info(PREFIX + "Reading logs");
		for (CloudBucket b : logbuckets) {
			App.info(PREFIX + "Reading logs from logbucket " + b.getName() + " ...");
			//creates a new folder for each bucket
			String folderpath = Paths.get(path.toString(), b.getName()).toString() ;
			File file = new File(folderpath);
			file.delete();
			file.mkdir();
			
			Iterator<String> fIt = handler.listCloudObjects(b).iterator();
			while (fIt.hasNext()) {
				String object = fIt.next();
				Path filepath = Paths.get(folderpath, object);
				File fileobj = new File(filepath.toString());
				fileobj.delete();
				handler.getCloudObjectFast(b, object, filepath);
				logpaths.add(filepath.toString());
			}
		}
		App.info(PREFIX + "End reading");
	}
	
	
	public void getLogsForBucket(String bucket, Path path) {
		//TODO complete the implementation
//		for (CloudBucket b : logbuckets) {
//			if (b.getName().contains(bucket)) {
//				Iterator<String> fIt = handler.listCloudObjects(b).iterator();
//				while (fIt.hasNext()) {
//					String object = fIt.next();
//					Path filepath = Paths.get(folderpath, object);
//					handler.getCloudObject(b, object, filepath);
//					logpaths.add(filepath.toString());
//				}
//			}
//		}
		System.out.println("getLogsForBucket not implemented!");
	}
	
	public void processLogs() {
		App.info(PREFIX + "Processing logs...");
		logs = new ArrayList<S3Log>();
		for (String logpath : logpaths) {
			try {
				Path p = Paths.get(logpath);
				S3Log log = new S3Log(p);
				logs.add(log);
			} catch (IOException e) {
				App.warn(PREFIX + "Error while fetching log located at: " + logpath);
			}
		}
	}
	
	public void analyseLogs() {
		App.info(PREFIX + "Analysing logs...");
		this.analyser = new S3LogAnalyser(logs, readIDs);
		analyser.analyse();
	}
	
	public AnalysisResult getAnalysisResult() {
		return analyser.getResult();
	}
	
	public void deleteRunBuckets() {
		buckets.forEach(a -> handler.deleteBucket(a));
		logbuckets.forEach(a -> handler.deleteBucket(a));
	}
	
	public void deleteAllBuckets() {
		handler.listBuckets().forEach(a -> {
			App.info(a.getName());
			handler.deleteBucket(a);});
	}
	
	public void close() {
		App.info(PREFIX + "Closing...");
		handler.close();
	}
	
	
}
