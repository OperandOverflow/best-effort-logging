package interfaces;

public enum CloudType {
	AMAZONS3, GOOGLESTORAGE, MICROSOFTAZURE;
	
	public CloudType getCloudType(String name) {
		String type = name.toUpperCase();
		if (type.contains("AMAZON") || type.contains("S3"))
			return AMAZONS3;
		if (type.contains("GOOGLE") || type.contains("GOOGLE"))
			return GOOGLESTORAGE;
		if (type.contains("MICROSOFT") || type.contains("AZURE"))
			return MICROSOFTAZURE;
		else 
			return null;
	}
}
