package clouds.aws.s3cloud;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import interfaces.CloudBucket;
import interfaces.Handler;
import main.App;


public class S3Handler implements Handler{
	
//	private static final String PREFIX = "                [S3Handler] ";
	
    private final S3Client s3Client;
    
    private final String LN = System.getProperty("line.separator");
    
    private final String LOGGINGPOLICY = 
    		  "{" + LN
    		+ "    \"Version\": \"2012-10-17\"," + LN
    		+ "    \"Statement\": [" + LN
    		+ "        {" + LN
    		+ "            \"Sid\": \"S3ServerAccessLogsPolicy\"," + LN
    		+ "            \"Effect\": \"Allow\"," + LN
    		+ "            \"Principal\": {" + LN
    		+ "                \"Service\": \"logging.s3.amazonaws.com\"" + LN
    		+ "            }," + LN
    		+ "            \"Action\": [" + LN
    		+ "        			\"s3:DeleteObject\"," + LN
    		+ "        			\"s3:GetObject\"," + LN
    		+ "        			\"s3:PutObject\"" + LN
    		+ "      		]," + LN
    		+ "            \"Resource\": \"arn:aws:s3:::%s/*\"" + LN
    		+ "        }" + LN
    		+ "    ]" + LN
    		+ "}";

    private int readcount = 1;
    
    public S3Handler() {
        s3Client = DependencyFactory.s3Client();
        
    }

	public CloudBucket createBucket(String bucketName) {
		CloudBucket bucket = new S3Bucket(bucketName);
		try {
//            CreateBucketResponse createRes = 
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
        } catch (BucketAlreadyExistsException e) {
        	System.err.println(e.awsErrorDetails().errorMessage());
        	close();
            System.exit(1);
        } catch (BucketAlreadyOwnedByYouException e) {
        	System.err.println(e.awsErrorDetails().errorMessage());
        	close();
            System.exit(1);
		} catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            close();
            System.exit(1);
        }
		return bucket;
	}

	
	public String activateLogging(CloudBucket objBucket, CloudBucket logBucket) {
		try {
    		PutBucketLoggingRequest logRequest = 
    				PutBucketLoggingRequest
    				.builder()
    				.bucket(objBucket.getName())
    				.bucketLoggingStatus(
    						BucketLoggingStatus
    						.builder()
    						.loggingEnabled(
    								LoggingEnabled
    								.builder()
    								.targetBucket(logBucket.getName())
    								.targetPrefix("")
    								.build())
    						.build())
    				.build();
    		PutBucketLoggingResponse logRes = s3Client.putBucketLogging(logRequest);
    		S3ResponseMetadata metadata = logRes.responseMetadata();
    		
//    		PutBucketPolicyResponse polRes = 
    				s3Client.putBucketPolicy(
    				PutBucketPolicyRequest
    				.builder()
    				.bucket(logBucket.getName())
    				.policy(String.format(LOGGINGPOLICY, logBucket.getName()))
    				.build()
    				);
    		return metadata.requestId();
    	} catch  (S3Exception e) {
    		System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
    	}
		return null;
		
	}

	
	public String putCloudObject(CloudBucket dstBucket, Path path, String key) {
		try {
			PutObjectRequest putRequest = 
					PutObjectRequest
					.builder()
					.bucket(dstBucket.getName())
					.key(key)
					.build();
			PutObjectResponse putRes = s3Client.putObject(putRequest, RequestBody.fromFile(path));
			s3Client.waiter().waitUntilObjectExists(
					HeadObjectRequest
					.builder()
					.bucket(dstBucket.getName())
					.key(key)
					.build());
			S3ResponseMetadata metadata = putRes.responseMetadata();
			dstBucket.addKey(key);
			return metadata.requestId();
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
		}
		return null;
	}

	
	public String deleteBucket(CloudBucket bucket) {
		try {
			List<String> toRemove = listCloudObjects(bucket);
			Iterator<String> coIt = toRemove.iterator();
			while (coIt.hasNext())
				deleteCloudObject(bucket, coIt.next());
			DeleteBucketRequest deleteBucketRequest = 
					DeleteBucketRequest
					.builder()
					.bucket(bucket.getName())
					.build();
			DeleteBucketResponse deleteRes = s3Client.deleteBucket(deleteBucketRequest);
			s3Client.waiter().waitUntilBucketNotExists(
					HeadBucketRequest
					.builder()
					.bucket(bucket.getName())
					.build());
			S3ResponseMetadata metadata = deleteRes.responseMetadata();
			return metadata.requestId();
		} catch  (S3Exception e) {
    		System.err.println(e.awsErrorDetails().errorMessage());
//            System.exit(1);
    	}
		return null;
	}

	
	public String deleteCloudObject(CloudBucket destBucket, String key) {
		try {
			DeleteObjectRequest deleteObjectRequest = 
					DeleteObjectRequest
					.builder()
					.bucket(destBucket.getName())
					.key(key)
					.build();
			DeleteObjectResponse deleteRes = s3Client.deleteObject(deleteObjectRequest);
			s3Client.waiter().waitUntilObjectNotExists(
					HeadObjectRequest
					.builder()
					.bucket(destBucket.getName())
					.key(key)
					.build());
			S3ResponseMetadata metadata = deleteRes.responseMetadata();
			destBucket.removeKey(key);
			return metadata.requestId();
		} catch  (S3Exception e) {
    		System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            close();
    	}
		return null;
	}

	
	public void close() {
		s3Client.close();
	}

	
	public List<String> listCloudObjects(CloudBucket bucket) {
		List<String> list = new ArrayList<String>();
		try {
			ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucket.getName()).build();
			ListObjectsResponse listRes = s3Client.listObjects(listObjectsRequest);
			List<S3Object> s3List = listRes.contents();
			Iterator<S3Object> s3It = s3List.iterator();
			while (s3It.hasNext())
				list.add(s3It.next().key());
			return list;
		} catch (NoSuchBucketException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			close();
		}
		return list;
	}
	
	public boolean contains(CloudBucket bucket, String key) {
		boolean res = false;
		List<String> list = listCloudObjects(bucket);
		Iterator<String> coIt = list.iterator();
		while (coIt.hasNext() && !res)
			res = res || coIt.next().equals(key);
		return res;
	}
	
	public boolean isEmpty(CloudBucket bucket) {
		return listCloudObjects(bucket).size() == 0;
	}

	
	public List<CloudBucket> listBuckets() {
		List<CloudBucket> res = new ArrayList<CloudBucket>();
		ListBucketsResponse listRes = s3Client.listBuckets();
		List<Bucket> list = listRes.buckets();
		Iterator<Bucket> bIt = list.iterator();
		while (bIt.hasNext()) {
			CloudBucket bucket = new S3Bucket(bIt.next().name());
			listCloudObjects(bucket).forEach(a -> bucket.addKey(a));;
			res.add(bucket);
		}
		return res;
	}

	/**
	 * Machine-dependent method, in order to execute in another computer must verify the aws cli path
	 */
	public String getCloudObject(CloudBucket destBucket, String key, Path path) {
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"C:\\Program Files\\Amazon\\AWSCLIV2\\aws.exe", 
					"s3",
					"cp",
					"s3://" + destBucket.getName() + "/" + key,
					"\"" + path.toAbsolutePath().toString() + "\"",
					"--debug"
					);
			
			File readLogFolder = Paths.get(path.getParent().toString(), "getobject-awscli-output").toFile();
			readLogFolder.mkdirs();
			File readLog = Paths.get(readLogFolder.toString(), "read-output-"+ readcount +"-" + key).toFile();
			readLog.delete();
			readLog.createNewFile();
			readcount++;
			
			Process p = pb.redirectError(readLog).start();
			p.waitFor();
//			InputStream is = p.getInputStream();
//		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
//		    String line = null;
//		    while ((line = br.readLine()) != null)
//		    	App.info(PREFIX + "[AWS CLI] "+ line);
		    return extractReadID(destBucket, key, readLog);
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String extractReadID(CloudBucket bucket, String key, File readLogFile) {
		try {
			Scanner sc = new Scanner(readLogFile);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				
				if (!line.matches(".*GET /" + key + " HTTP/1.1.*") &&
					!line.matches(".*GET /" + bucket.getName() + "/" + key + " HTTP/1.1.*"))
					continue;
				line = sc.nextLine().replace("'", "").replace(",", "");
				String[] lines = line.split(" ");
				for (int i = 0; i < lines.length; i++) {
					if (lines[i].equals("x-amz-request-id:")) {
						sc.close();
						return lines[i+1];
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	public String getCloudObjectFast(CloudBucket destBucket, String key, Path path) {
		try {
			
			GetObjectRequest getObjectRequest = 
		            GetObjectRequest
		            .builder()
		            .bucket(destBucket.getName()).key(key)
		            .responseCacheControl("no-cache")
		            .build();
		            
			GetObjectResponse getRes = s3Client.getObject(getObjectRequest, path);
			S3ResponseMetadata metadata = getRes.responseMetadata();
			return metadata.requestId();
			
//			GetObjectRequest getObjectRequest = 
//					GetObjectRequest
//					.builder()
//					.bucket(destBucket.getName()).key(key)
//					.responseCacheControl("no-cache")
//					.build();
//			ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
//            byte[] data = objectBytes.asByteArray();
//            File myFile = new File(path.toString());
//            FileOutputStream os;
//			try {
//				os = new FileOutputStream(myFile);
//				os.write(data);
//	            System.out.println("Successfully obtained bytes from an S3 object");
//	            os.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//            
//            S3ResponseMetadata metadata = objectBytes.response().responseMetadata();
//            return metadata.requestId();
            

		} catch (NoSuchBucketException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			close();
		}
		return null;
	}
	
}
