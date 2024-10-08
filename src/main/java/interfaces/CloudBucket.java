package interfaces;

import java.util.List;

public interface CloudBucket {
	
	public String getName();
	
	public void addKey(String key);
	
	public void removeKey(String key);
	
	public List<String> getKeys();
}