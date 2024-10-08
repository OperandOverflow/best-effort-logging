::Run CloudLogBenchmark
ECHO OFF
aws sso login
javac RunProject.java
java RunProject Properties.properties
ECHO ON