package clouds.google.cloudstorage;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.storage.Bucket;

import interfaces.CloudBucket;

public class GSBucket implements CloudBucket{
	
	private String name;
	private List<String> keys;
	
	public GSBucket(String name) {
		this.name = name;
		this.keys = new ArrayList<String>();
	}
	
	public GSBucket(Bucket bucket) {
		this.name = bucket.getName();
		this.keys = new ArrayList<String>();
	}
	
	
	public String getName() {
		return name;
	}

	
	public void addKey(String key) {
		keys.add(key);
	}

	
	public void removeKey(String key) {
		keys.remove(key);
		
	}

	
	public List<String> getKeys() {
		return keys;
	}

}
