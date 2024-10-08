package interfaces;

import java.nio.file.Path;
import java.util.List;

/**
 * Functions should be provided by classes that implement this interface,
 * which represent each cloud storage and it's metadata.
 */
public interface CloudInfo {
	
	/**
	 * Returns the enum of the cloud type
	 * @return Type of cloud
	 */
	public CloudType getCloudType();
	
	/**
	 * Returns the handler of the cloud storage.
	 * Should avoid using the handler directly!
	 * Instead invoke methods specified on this interface.
	 * @return
	 * 		Handler of the corresponding cloud storage
	 */
	public Handler getHandler();
	
	/**
	 * Creates a bucket in the cloud with the given name
	 * and it's corresponding log bucket and associates
	 * it with the log bucket
	 * @param name
	 * 		Name of the bucket to create
	 */
	public void createBucket(String name);
	
	/**
	 * Uploads the file at the given path to the bucket identified by it's  
	 * name and associate it with the given key
	 * @param bucket 
	 * 		Bucket to where the file will be uploaded
	 * @param path 
	 * 		Path of the file to be uploaded
	 * @param key
	 * 		Key to be associated with the file
	 */
	public void putCloudObject(String bucket, Path path, String key);
	
	/**
	 * Retrieves all files uploaded during the run from the created buckets
	 * @param path
	 */
	public void getCloudObjects(Path path);
	
	/**
	 * Retrieves the file with the given key and saves into the 
	 * given path
	 * @param key
	 * 		Key associated with the file
	 * @param path
	 * 		Path where the file will be stored
	 */
	public void getCloudObject(String key, Path path);
	
	/**
	 * Returns a list of Strings that represent request IDs of
	 * getCloudObject operations
	 * @return
	 * 		List of request IDs or
	 */
	public List<String> getReadIDs();
	
	/**
	 * Retrieves all the logs of all the buckets to the given path
	 * @param path
	 */
	public void getLogs(Path path);
	
	/**
	 * NOT IMPLEMENTED!
	 * Reads all logs of a specific bucket to a path
	 * @param bucket
	 * 		Bucket of which the logs will be retrieved
	 * @param path
	 * 		Path to where the logs will be saved
	 */
	public void getLogsForBucket(String bucket, Path path);
	
	/**
	 * Process logs, extract identifiers from the logs
	 */
	public void processLogs();
	
	/**
	 * Analysis logs, compare the extracted identifiers with
	 * the ones registered after each execution 
	 */
	public void analyseLogs();
	
	/**
	 * Returns the result of log analysis
	 * @return
	 * 		Object that contains result of log analysis
	 */
	public AnalysisResult getAnalysisResult();
	
	/**
	 * Deletes all buckets generated during the run
	 */
	public void deleteRunBuckets();
	
	/**
	 * Deletes all buckets on the cloud
	 */
	public void deleteAllBuckets();
	
	/**
	 * Closes the connection to the cloud
	 */
	public void close();
}
