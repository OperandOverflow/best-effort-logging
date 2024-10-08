package clouds.aws.s3cloud;

import interfaces.CloudObject;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3CloudObject implements CloudObject{
	
	private static String objStr = "S3CloudObject: ";
	
	private S3Object object;
	
	public S3CloudObject(S3Object obj) {
		this.object = obj;
	}
	
	@Override
	public String getKey() {
		return object.key();
	}

	@Override
	public long getSize() {
		return object.size();
	}
	
	@Override
	public String toString() {
		return objStr + object.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof S3CloudObject))
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return object.equals(o);
	}

}
