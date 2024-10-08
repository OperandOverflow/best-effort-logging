package actors;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import controllers.WriteController;

public class Writer {
	
//	private static final String PREFIX = "       [Writer] ";
	
	private WriteController controller;

	public Writer (WriteController wctrl) {
		this.controller = wctrl;
	}
	
	/**
	 * Generates a file with the given name and size and upload it to 
	 * cloud.
	 * @param filename
	 * @param size
	 */
	public void write(String filename, long size) {
		controller.createDirectory();
		String bucket = controller.createBucket();
		File file = controller.generateFile(filename, size);
		controller.putFile(bucket, file.toPath(), filename);
	}
	
	/**
	 * Generates a file with the given size and a random name
	 * @param size
	 */
	public void write(long size) {
		controller.createDirectory();
		String bucket = controller.createBucket();
		File file = controller.generateFile(size);
		controller.putFile(bucket, file.toPath(), file.getName());
	}
	
	/**
	 * Creates bucket with legible name and puts file with the given size
	 * to the bucket
	 * @param numreads
	 * @param size
	 * @param interval
	 */
	public void write(int numreads, long size, long interval) {
		controller.createDirectory();
		String bucketName = numreads + "reads-" + size + "bytes-" + interval + "msec--";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");  
		LocalDateTime now = LocalDateTime.now();
		bucketName = bucketName + dtf.format(now);
		String bucket = controller.createBucket(bucketName);
		File file = controller.generateFile(size);
		controller.putFile(bucket, file.toPath(), file.getName());
	}
}
