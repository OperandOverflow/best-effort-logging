package clouds.google.cloudstorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GSLog {
	
	
	/**
	 * lines[i]
	 * 0 = time in microseconds
	 * 1 = requester ip
	 * 2 = requester ip type
	 * 3 = requester ip region
	 * 4 = HTTP operation type
	 * 5 = URI
	 * 6 = HTTP response code
	 * 7 = request sent bytes
	 * 8 = response sent bytes
	 * 9 = time taken to serve the request (round-trip time)
	 * 10 = host in request
	 * 11 = HTTP referrer
	 * 12 = user agent in request
	 * 13 = request id
	 * 14 = cloud storage operation
	 * 15 = requested bucket
	 * 16 = requested object
	 */
	private Scanner sc;
	private List<String[]> lines;
	
	public GSLog(Path logpath) throws FileNotFoundException {
		lines = new ArrayList<String[]>();
		File file = logpath.toFile();
		sc = new Scanner(file);
		// Consume the first line which only has field names
		if (sc.hasNextLine())
			sc.nextLine();
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.replace("\"", "");
			String[] elements = line.split(",");
			lines.add(elements);
		}
		sc.close();
	}
	
	public String getBucket() {
		return lines.get(0)[15];
	}
	
	public List<String> getTime() {
		return getAtIndex(0);
	}
	
	public List<String> getRequestID() {
		return getAtIndex(13);
	}
	
	public List<String> getOperation() {
		return getAtIndex(4);
	}
	
	public List<String> getObjectKey() {
		return getAtIndex(5);
	}
	
	
	private List<String> getAtIndex(int index) {
		List<String> res = new ArrayList<String>();
		lines.forEach(a -> res.add(a[index]));
		return res;
	}
}
