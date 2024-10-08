package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import interfaces.AnalysisResult;
import interfaces.ResultWriter;

public class ResultCSVWriter implements ResultWriter{
	
	private final String LINE = System.getProperty("line.separator");
	
	public ResultCSVWriter() {
	}

	public boolean writeResult(String instant, int numreads, long filesize, long interval, List<AnalysisResult> list, Path path) {
		File file = path.toFile();
		FileWriter writer;
		list.sort(new AnalysisResultComparator());
		if (!file.exists()) {
			try {
				file.createNewFile();
				writer = new FileWriter(file);
				
				writer.write("exec time,");
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS");  
				LocalDateTime now = LocalDateTime.now();
				writer.write(dtf.format(now) + LINE);
				
				writer.write("no. reads,");
				writer.write(numreads + LINE);
				
				writer.write("interval,");
				writer.write(interval + LINE);
				
				writer.write("file size,");
				writer.write(filesize + LINE);
				
				List<String> name = new LinkedList<String>();
				name.add("Instant");
				for (AnalysisResult result : list) {
					name.add(result.getCloudName());
				}
				
				writeLine(writer, name.toArray(String[]::new));
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		try {
			writer = new FileWriter(file, true);
			List<String> data = new LinkedList<String>();
			data.add(instant);
			for (AnalysisResult result : list)
				data.add(Integer.toString(result.getLoggedReads()));
			writeLine(writer, data.toArray(String[]::new));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean writeLine(FileWriter writer, String[] data) throws IOException {
		String line = Stream.of(data).collect(Collectors.joining(","));
		writer.write(line + LINE);
		return true;
	} 
	
	private class AnalysisResultComparator implements Comparator<AnalysisResult> {
		public int compare(AnalysisResult o1, AnalysisResult o2) {
			return o1.getCloudName().compareTo(o2.getCloudName());
		}
	}

}
