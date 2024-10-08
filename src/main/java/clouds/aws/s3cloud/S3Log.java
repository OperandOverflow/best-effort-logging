package clouds.aws.s3cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class S3Log {
	
	/**
	 * lines[i]
	 * 0 = bucket owner canonical ID
	 * 1 = bucket name
	 * 2 = time
	 * 3 = time zone offset
	 * 4 = requester IP address
	 * 5 = requester canonical ID
	 * 6 = request ID
	 * 7 = operation
	 * 8 = object key
	 */
	private Scanner sc;
	private List<String[]> lines;
	
	public S3Log(Path logpath) throws FileNotFoundException {
		lines = new ArrayList<String[]>();
		File file = logpath.toFile();
		sc = new Scanner(file);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.replace("[", "").replace("]", "").replace("\"", "");
			String[] elements = line.split(" ");
			lines.add(elements);
		}
		sc.close();
	}
	
	public String getBucket() {
		return lines.get(0)[1];
	}
	
	public List<String> getTime() {
		List<String> res = new ArrayList<String>();
		lines.forEach(a -> res.add(a[2] + " " + a[3]));
		return res;
	}
	
	public List<String> getRequestID() {
		return getAtIndex(6);
	}
	
	public List<String> getOperation() {
		return getAtIndex(7);
	}
	
	public List<String> getObjectKey() {
		return getAtIndex(8);
	}
	
	private List<String> getAtIndex(int index) {
		List<String> res = new ArrayList<String>();
		lines.forEach(a -> res.add(a[index]));
		return res;
	}
}
