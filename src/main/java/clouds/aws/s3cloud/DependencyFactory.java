
package clouds.aws.s3cloud;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
//import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * The module containing all dependencies required by the {@link S3Handler}.
 */
public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
    	ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
    	return S3Client.builder()
    				   .region(Region.EU_NORTH_1)
    				   .credentialsProvider(credentialsProvider)
//                       .httpClientBuilder(ApacheHttpClient.builder())
                       .build();
    }
}
