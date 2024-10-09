# Cloud best-effort logging benchmark

This project contains a maven application with [AWS Java SDK 2.x](https://github.com/aws/aws-sdk-java-v2) dependencies.

### Overview
This program uses the official AWS Java SDK to perform writings to a S3 bucket with server-side logging activated and then reads from it.
While reading, it collects the request IDs for later compare with the requests in the logs.
After the reading process, it retrieves the logs periodically (user defined timestamps) and count the number of read requests that were successfully logged.
If all the requests are logged, the program will finish and also delete the created bucket used for the test.

### Prerequisites
- Java 20+ (recommended)
- Apache Maven
- AWS command line interface (CLI)
- AWS account

### Limitations
Due to unknown reasons, read object requests made using the official SDK are not being logged by the server. Therefore, this program performs the read requests
through the AWS CLI., this requires the user to define the path to the AWS CLI executable in `S3Handler` class `getCloudObject()` function.

### Compiling and running

To compile the program, run the following command in the root directory of the project:
```
mvn clean package
```

To run the program, it's possible to use the `Execute.bat` script. In alternative, compile the `RunProject.java` class and run it.

