package controllers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileGenerator {
	
	public FileGenerator() {}
	
	public File generateFile(String pathname, long size) throws IOException {
		File file = new File(pathname);
		file.delete();
		file.createNewFile();
			
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.setLength(size);
		raf.close();
		return file;
	}
}