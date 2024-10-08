package interfaces;

import java.util.List;
import java.nio.file.Path;

public interface ResultWriter {
	public boolean writeResult(String instant,int numreads, long filesize, long interval, List<AnalysisResult> list, Path path);
}