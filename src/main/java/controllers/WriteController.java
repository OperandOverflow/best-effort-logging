package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import user.DataContainer;

public class WriteController {
	
//	private static final String PREFIX = "          [WriteController] ";
	
	//Constants
	private static String PATH_SEPARATOR = System.getProperty("file.separator");
	private static String DEFAULT_PATH = 
			"." + PATH_SEPARATOR + "runtime" + PATH_SEPARATOR + "write-files" + PATH_SEPARATOR;
			//".\runtime\write-files"
	
	//Tools
	private FileGenerator filegen;
	private DataContainer container;
	
	public WriteController(DataContainer data) {
		this.filegen = new FileGenerator();
		this.container = data;
	}
	
	/**
	 * Creates the runtime directory
	 */
	public void createDirectory() {
		File folder = new File(DEFAULT_PATH);
		folder.delete();
		folder.mkdirs();
	}
	
	/**
	 * Creates a bucket in clouds with the name of current time
	 */
	public String createBucket() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS");  
		LocalDateTime now = LocalDateTime.now();
		String bucketName = "bucket-" + dtf.format(now);
		container.getClouds().forEach(c -> c.createBucket(bucketName));
		return bucketName;
	}
	
	/**
	 * Creates a bucket in clouds with the given name
	 * @param bucketName Name of the bucket to be created
	 */
	public String createBucket(String bucketName) {
		container.getClouds().forEach(c -> c.createBucket(bucketName));
		return bucketName;
	}
	
	/**
	 * Generates a plain text file with the given size with the name of the current time
	 * @param size
	 * @return File
	 */
	public synchronized File generateFile(long size) {
		try {
				
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
			LocalDateTime now = LocalDateTime.now();
			String filename = DEFAULT_PATH + dtf.format(now) + ".txt";
			
			File file = filegen.generateFile(filename, size);
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Generates a plain text file with the given name and size
	 * @param filename
	 * @param size
	 * @return File
	 */
	public synchronized File generateFile(String filename, long size) {
		try {
			File folder = new File(DEFAULT_PATH);
			folder.delete();
			folder.mkdir();
			
			return filegen.generateFile(DEFAULT_PATH + filename, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void putFile(String bucket, Path path, String key) {
		container
		.getClouds()
		.forEach(a -> a.putCloudObject(bucket, path, key));
	}
}