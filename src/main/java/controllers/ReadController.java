package controllers;

import java.io.File;
import java.util.Iterator;

import interfaces.CloudInfo;
import user.DataContainer;

public class ReadController {
	
	private static final String PREFIX = "          [ReadController] ";
	
	//Constants
	private static final String PATH_SEPARATOR = System.getProperty("file.separator");
	private static final String DEFAULT_PATH = 
			"." + PATH_SEPARATOR + "runtime" + PATH_SEPARATOR + "read-files" + PATH_SEPARATOR;
	
	private DataContainer container;
	
	public ReadController(DataContainer data) {
		this.container = data;
	}
	
	public void readFile() {
		createDirectory();
		Iterator<CloudInfo> cdit = container.getClouds().iterator();
		while (cdit.hasNext()) {
			CloudInfo info = cdit.next();
			File folder = createDirectory(info.getCloudType().toString());
			info.getCloudObjects(folder.toPath());
		}
	}
	
	private File createDirectory() {
		File folder = new File(DEFAULT_PATH);
		folder.delete();
		folder.mkdir();
		return folder;
	}
	
	private File createDirectory(String foldername) {
		File folder = new File(DEFAULT_PATH + foldername + PATH_SEPARATOR);
		folder.delete();
		folder.mkdir();
		return folder;
	}
}
