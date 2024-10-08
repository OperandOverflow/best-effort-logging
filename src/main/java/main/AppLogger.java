package main;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
	
	private static String PATH_SEPARATOR = System.getProperty("file.separator");
	private static String DEFAULT_PATH = 
			"." + PATH_SEPARATOR + "runtime-log" + PATH_SEPARATOR;
	
	private static PrintStream STDOUT = new PrintStream(new FileOutputStream(FileDescriptor.out));
	
	public static void changeoutput() {
		System.out.println("Creating runtime log folder...");
		File folder = new File(DEFAULT_PATH);
		folder.mkdirs();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS");  
		LocalDateTime now = LocalDateTime.now();
		
		File logfile = Paths.get(DEFAULT_PATH, "runtime-log-" + dtf.format(now) + ".txt").toFile();
		System.out.println("Changing System.out...");
		try {
			logfile.createNewFile();
			System.setOut(new PrintStream(logfile));
		} catch (IOException e) {
			restoreoutput();
			e.printStackTrace();
		}
		
	}
	
	public static void restoreoutput() {
		System.setOut(STDOUT);
	}
}
