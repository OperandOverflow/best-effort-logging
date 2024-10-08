package clouds.google.cloudstorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import interfaces.AnalysisResult;
import interfaces.CloudBucket;
import interfaces.CloudInfo;
import interfaces.CloudType;
import interfaces.Handler;
import main.App;

public class GSCloudInfo implements CloudInfo{
	
	private static final String PREFIX = "             [GSCloudInfo] ";
	
	//Singleton
	private static GSCloudInfo instance = null;
	
	//Type
	private static CloudType type = CloudType.GOOGLESTORAGE;
	
	//Tools
	private Handler handler;
	private GSLogAnalyser analyser;
	
	private final String PROJECT_ID = "vital-folder-392010";
	
	//Data
	private List<CloudBucket> buckets;
	private List<CloudBucket> logbuckets;
	
	private List<String> keys;
	
	private List<String> readIDs;
	private Set<String> logpaths;
	private List<GSLog> logs;
	
	
	private GSCloudInfo() {
		handler 	= new GSHandler(PROJECT_ID);
		buckets 	= new ArrayList<CloudBucket>();
		keys 		= new ArrayList<String>();
		readIDs 	= new ArrayList<String>();
		logbuckets 	= new ArrayList<CloudBucket>();
		logpaths 	= new HashSet<String>();
		logs 		= new ArrayList<GSLog>();
		
	}
	
	public static synchronized GSCloudInfo getInstance() {
		if (instance == null) {
			instance = new GSCloudInfo();
		}
		return instance;
	}

	public CloudType getCloudType() {
		return type;
	}

	public Handler getHandler() {
		return handler;
	}

	public void createBucket(String name) {
		CloudBucket bucket = handler.createBucket(name);
		buckets.add(bucket);
		CloudBucket logBucket = handler.createBucket("logbucket-for-" + name);
		logbuckets.add(logBucket);
		handler.activateLogging(bucket, logBucket);
	}

	public void putCloudObject(String bucket, Path path, String key) {
		for (CloudBucket b : buckets) {
			if (b.getName().equals(bucket)) {
				handler.putCloudObject(b, path, key);
				keys.add(key);
			}
		}
	}

	public void getCloudObjects(Path path) {
		for (CloudBucket b : buckets) {
			for (String key : b.getKeys()) {
				Path filepath = Paths.get(path.toString(), key);
				File file = new File(filepath.toString());
				file.delete();
				readIDs.add(handler.getCloudObject(b, key, filepath));
			}
			// TODO
			readIDs.forEach(a -> System.out.println(a));
		}
	}

	public void getCloudObject(String key, Path path) {
		for (CloudBucket b : buckets)
			readIDs.add(handler.getCloudObject(b, key, path));
	}

	public List<String> getReadIDs() {
		return readIDs;
	}

	public void getLogs(Path path) {
		for (CloudBucket b : logbuckets) {
			//creates a new folder for each bucket
			String folderpath = Paths.get(path.toString(), b.getName()).toString() ;
			File file = new File(folderpath);
			file.delete();
			file.mkdir();
			
			Iterator<String> fIt = handler.listCloudObjects(b).iterator();
			while (fIt.hasNext()) {
				String object = fIt.next();
				if (!object.contains("usage"))
					continue;
				Path filepath = Paths.get(folderpath, object);
				File fileobj = new File(filepath.toString());
				fileobj.delete();
				handler.getCloudObject(b, object, filepath);
				logpaths.add(filepath.toString());
			}
		}
	}

	public void getLogsForBucket(String bucket, Path path) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("This operation is not supported!");
	}

	public void processLogs() {
		logs = new ArrayList<GSLog>();
		for (String logpath : logpaths) {
			try {
				Path p = Paths.get(logpath);
				GSLog log = new GSLog(p);
				logs.add(log);
			} catch (IOException e) {
				App.warn(PREFIX + "Error while fetching log located at: " + logpath);
			}
		}
		
	}

	public void analyseLogs() {
		this.analyser = new GSLogAnalyser(logs, readIDs);
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
		handler.listBuckets().forEach(a -> handler.deleteBucket(a));
	}

	public void close() {
	}

}
