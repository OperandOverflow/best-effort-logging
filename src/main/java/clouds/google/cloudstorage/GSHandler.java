package clouds.google.cloudstorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.gax.paging.Page;
import com.google.cloud.Binding;
import com.google.cloud.Policy;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import interfaces.CloudBucket;
import interfaces.Handler;
import main.App;

public class GSHandler implements Handler{
	
	private static final String PREFIX = "                [GSHandler] ";
	
	private final Storage storage;
	
	private final String RETURN_MSG = "Request ID not appliable!";
	// TODO passar o default para o cloud info
	private final String DEFAULT_PROJECT_ID = "vital-folder-392010";
	
	public GSHandler() {
		this.storage = StorageOptions.newBuilder().setProjectId(DEFAULT_PROJECT_ID).build().getService();
	}
	
	public GSHandler(String projectID) {
		this.storage = StorageOptions.newBuilder().setProjectId(projectID).build().getService();
	}

	
	public CloudBucket createBucket(String bucketName) {
		Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName).build());
		return new GSBucket(bucket);
	}

	
	public String activateLogging(CloudBucket objBucket, CloudBucket logBucket) {
		// Allow log delivery
		Policy originalPolicy =
		        storage.getIamPolicy(logBucket.getName(), Storage.BucketSourceOption.requestedPolicyVersion(3));
		
		String role = "roles/storage.objectCreator";
	    String member = "group:cloud-storage-analytics@google.com";
	    
	    List<Binding> bindings = new ArrayList<Binding>(originalPolicy.getBindingsList());
		
	    Binding.Builder newMemberBindingBuilder = Binding.newBuilder();
	    newMemberBindingBuilder.setRole(role).setMembers(Arrays.asList(member));
	    bindings.add(newMemberBindingBuilder.build());
	    
	    Policy.Builder updatedPolicyBuilder = originalPolicy.toBuilder();
	    updatedPolicyBuilder.setBindings(bindings).setVersion(3);
	    storage.setIamPolicy(logBucket.getName(), updatedPolicyBuilder.build());
	    
	    // Active logging
	    BucketInfo.Logging logging = BucketInfo.Logging.newBuilder()
	    	    .setLogBucket(logBucket.getName())
	    	    .setLogObjectPrefix("")
	    	    .build();
	    
	    Bucket bucket = storage.get(objBucket.getName());
	    
	    bucket = bucket.toBuilder()
	    	    .setLogging(logging)
	    	    .build()
	    	    .update();
		return RETURN_MSG;
	}

	
	public String putCloudObject(CloudBucket destBucket, Path path, String key) {
		BlobId blobId = BlobId.of(destBucket.getName(), key);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		
		Storage.BlobWriteOption precondition;
		if (storage.get(destBucket.getName(), key) == null) {
			precondition = Storage.BlobWriteOption.doesNotExist();
		} else {
			precondition =
			          Storage.BlobWriteOption.generationMatch(
			              storage.get(destBucket.getName(), key).getGeneration());
		}
		try {
			storage.createFrom(blobInfo, path, precondition);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RETURN_MSG;
	}

	
	public String getCloudObject(CloudBucket destBucket, String key, Path path) {
		App.info(PREFIX + "reading object");
		
		ProcessBuilder pBuilder = new ProcessBuilder(
				"C:\\Users\\Xiting Wang\\AppData\\Local\\Google\\Cloud SDK\\google-cloud-sdk\\bin\\gcloud.cmd",
				"storage", 
				"cp",
				"gs://" + destBucket.getName() + "/" + key,
				"\"" + path.toAbsolutePath().toString() + "\"",
				"--log-http"
				);
		pBuilder.redirectErrorStream(true);
		
		try {
			return extractReadID(pBuilder);
		} catch (IOException e) {
			e.printStackTrace();
			return RETURN_MSG;
		}
	}
	
	public String getCloudObjectFast(CloudBucket destBucket, String key, Path path) {
		Blob object = storage.get(BlobId.of(destBucket.getName(), key));
		if (object != null)
			object.downloadTo(path);
		return RETURN_MSG;
	}

	
	public List<CloudBucket> listBuckets() {
		Page<Bucket> bucketsPage =  storage.list();
		List<CloudBucket> result = new ArrayList<CloudBucket>();
		for (Bucket bucket : bucketsPage.iterateAll())
			result.add(new GSBucket(bucket));
		return result;
	}

	
	public List<String> listCloudObjects(CloudBucket bucket) {
		Page<Blob> objects = storage.list(bucket.getName());
		List<String> result = new ArrayList<String>();
		for (Blob blob : objects.iterateAll())
			result.add(blob.getName());
		return result;
	}

	
	public boolean contains(CloudBucket bucket, String key) {
		return listCloudObjects(bucket).contains(key);
	}

	
	public boolean isEmpty(CloudBucket bucket) {
		return listBuckets().size() == 0;
	}

	
	public String deleteBucket(CloudBucket bucket) {
		Bucket delbucket = storage.get(bucket.getName());
		if (delbucket != null) {
			listCloudObjects(bucket).forEach(a -> deleteCloudObject(bucket, a));
			delbucket.delete();
		}
		return RETURN_MSG;
	}

	
	public String deleteCloudObject(CloudBucket destBucket, String key) {
		Blob object = storage.get(destBucket.getName(), key);
		if (object != null) {
			Storage.BlobSourceOption precondition =
			        Storage.BlobSourceOption.generationMatch(object.getGeneration());
			storage.delete(destBucket.getName(), key, precondition);
		}
		return RETURN_MSG;
	}

	
	public void close() {
	}
	
	private String extractReadID(ProcessBuilder pBuilder) throws IOException {
		App.warn(PREFIX + "Extracting id");
		
		Process process = pBuilder.start();
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String lineString, result = "";
		while ((lineString = bReader.readLine()) != null) {
			
			if (lineString.contains("X-GUploader-UploadID")) {
				App.warn(lineString);
				String[] lineStrings = lineString.replace(" ", "").split(":");
				if (lineStrings.length >= 2)
					result = lineStrings[1];
				break;
			}
		}
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			bReader.close();
		}
		bReader.close();
		return result;
	}

}
