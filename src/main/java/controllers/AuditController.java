package controllers;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import interfaces.AnalysisResult;
import interfaces.CloudInfo;
import interfaces.ResultWriter;
import main.App;
import user.DataContainer;

public class AuditController {
	
	private final String PREFIX = "          [AuditController] ";
	
	// Constants
	private static final String PATH_SEPARATOR = System.getProperty("file.separator");
	
	// .\runtime\read-logs\
	private static final String DEFAULT_LOG_PATH = 
			"." + PATH_SEPARATOR + "runtime" + PATH_SEPARATOR + "read-logs" + PATH_SEPARATOR;
	
	// .\result\
	private static final String DEFAULT_RESULT_PATH =
			"." + PATH_SEPARATOR + "result" + PATH_SEPARATOR;
	
	private DataContainer container;
	private String resultPath;
	
	public AuditController(DataContainer data) {
		this.container = data;
		resultPath = null;
	}
	
	public void readLogs() {
		App.info(PREFIX + "Reading logs from clouds");
		//creates read-logs folder
		createDirectory();
		Iterator<CloudInfo> coIt = container.getClouds().iterator();
		while (coIt.hasNext()) {
			CloudInfo info = coIt.next();
			//creates folder for each cloud
			File folder = createDirectory(DEFAULT_LOG_PATH + info.getCloudType().toString());
			info.getLogs(folder.toPath());
		}
		App.info(PREFIX + "End reading");
	}
	
	public void processLogs() {
		App.info(PREFIX + "Processing logs");
		Iterator<CloudInfo> coIt = container.getClouds().iterator();
		while (coIt.hasNext()) {
			CloudInfo info = coIt.next();
			info.processLogs();
		}
		App.info(PREFIX + "End processing");
	}
	
	public void analyseLogs() {
		App.info(PREFIX + "Analysing logs");
		Iterator<CloudInfo> coIt = container.getClouds().iterator();
		while (coIt.hasNext()) {
			CloudInfo info = coIt.next();
			info.analyseLogs();
		}
		App.info(PREFIX + "End analysing");
	}
	
	public List<Integer> writeResults(String instant,  int numreads, long filesize, long interval) {
		App.info(PREFIX + "Generating analyse results {");
		
		createDirectory(DEFAULT_RESULT_PATH);
		ResultWriter resultWriter = new ResultCSVWriter();
		
		List<AnalysisResult> results = new ArrayList<AnalysisResult>();
		Iterator<CloudInfo> coIt = container.getClouds().iterator();
		while (coIt.hasNext())
			results.add(coIt.next().getAnalysisResult());
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS");  
		LocalDateTime now = LocalDateTime.now();
		if (resultPath == null)
			resultPath = "result-" + dtf.format(now) + 
				"(" + numreads + "reads," + filesize + "bytes," + interval + "msec)" + ".csv";
		resultWriter.writeResult(
				instant,
				numreads,
				filesize,
				interval,
				results,
				Paths.get(DEFAULT_RESULT_PATH, resultPath));
		
		List<Integer> loggedreads = new ArrayList<Integer>();
		results.forEach(a -> loggedreads.add(a.getLoggedReads()));
		App.info(PREFIX + "} End generating");
		return loggedreads;
	}
	
	private File createDirectory() {
		File folder = new File(DEFAULT_LOG_PATH);
		folder.delete();
		folder.mkdirs();
		return folder;
	}
	
	private File createDirectory(String foldername) {
		File folder = new File(foldername + PATH_SEPARATOR);
		folder.delete();
		folder.mkdirs();
		return folder;
	}
}
