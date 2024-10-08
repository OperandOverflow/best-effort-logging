package interfaces;

import java.nio.file.Path;
import java.util.List;

/**
 * This interface represents handlers that talk to cloud storage
 */
public interface Handler {
	
	/**
	 * Create a bucket
	 * @param bucketName The name of the bucket to create
	 * @return An abstract representation of the created bucket
	 */
	public CloudBucket createBucket(String bucketName);
	
	/**
	 * Activate logging to a certain bucket
	 * @param objBucket
	 * @param logBucket
	 * @return request id
	 */
	public String activateLogging(CloudBucket objBucket, CloudBucket logBucket);
	
	/**
	 * Upload an object to a bucket in the cloud
	 * @param destBucket The bucket to where the object will be uploaded
	 * @param obj The object to be uploaded
	 * @return request id
	 */
	public String putCloudObject(CloudBucket destBucket, Path path, String key);
	
	/**
	 * Get an object from a bucket
	 * @param destBucket The bucket where the object is stored
	 * @param obj The object to be retrieved
	 * @param path The path where the file will be saved
	 * @return request id
	 */
	public String getCloudObject(CloudBucket destBucket, String key, Path path);
	
	
	/**
	 * Returns a list of all buckets
	 * @return
	 */
	public List<CloudBucket> listBuckets();
	
	
	/**
	 * List the objects in the bucket
	 * @param bucket
	 */
	public List<String> listCloudObjects(CloudBucket bucket);
	
	/**
	 * Returns true if the bucket contains the cloud object
	 * @param bucket The bucket in where to search for the object
	 * @param obj The object to be searched
	 * @return True if the bucket contains the cloud object
	 */
	public boolean contains(CloudBucket bucket, String key);
	
	/**
	 * Returns true if the bucket is empty
	 * @param bucket
	 * @return
	 */
	public boolean isEmpty(CloudBucket bucket);
	
	/**
	 * Delete a bucket and it's content
	 * @param bucket
	 * @return request id
	 */
	public String deleteBucket(CloudBucket bucket);
	
	/**
	 * Delete an object from the bucket
	 * @param destBucket The bucket where the object is stored
	 * @param obj The object to be deleted
	 * @return request id
	 */
	public String deleteCloudObject(CloudBucket destBucket, String key);
	
	/**
	 * Close connection with the cloud
	 */
	public void close();
}
