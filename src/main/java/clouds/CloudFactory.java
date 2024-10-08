package clouds;

import clouds.aws.s3cloud.S3CloudInfo;
import clouds.google.cloudstorage.GSCloudInfo;
import interfaces.CloudInfo;

public class CloudFactory {
	
	public static CloudInfo getCloud(String cloudname) {
		String type = cloudname.toUpperCase();
		if (type.contains("AMAZON") || type.contains("S3"))
			return S3CloudInfo.getInstance();
		if (type.contains("GOOGLE") || type.contains("GOOGLESTORAGE"))
			return GSCloudInfo.getInstance();
		if (type.contains("MICROSOFT") || type.contains("AZURE"))
			//TODO
			return null;
		return null;
	} 
	
}
