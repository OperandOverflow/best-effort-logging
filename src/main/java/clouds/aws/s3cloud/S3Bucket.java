package clouds.aws.s3cloud;

import java.util.ArrayList;
import java.util.List;

import interfaces.CloudBucket;
import software.amazon.awssdk.services.s3.model.Bucket;

public class S3Bucket implements CloudBucket{
	
	private String name;
	private List<String> keys;
	
	public S3Bucket(String name) {
		this.name = name;
		this.keys = new ArrayList<String>();
	}
	
	public S3Bucket(Bucket bucket) {
		this.name = bucket.name();
		this.keys = new ArrayList<String>();
	}

	
	public String getName() {
		return this.name;
	}

	
	public void addKey(String key) {
		this.keys.add(key);
	}

	
	public void removeKey(String key) {
		this.keys.remove(key);
	}

	
	public List<String> getKeys() {
		return keys;
	}
	
	public boolean contains(String key) {
		return this.keys.contains(key);
	}
}
