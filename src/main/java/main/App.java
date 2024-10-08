package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import user.Starter;

public class App {
	
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * TODO
     * 	set exception handling class
     *  set application starter class (done)
     *  create user class (done)
     *  create configuration class (done)
     * @param args
     */
    public static void main(String... args) {
    	
    	info("App Starts");

    	Starter.Start(args);
        
        info("Application ends");
    }
    
    public static void info(String str) {
    	logger.info(str);
    }
    
    public static void warn(String str) {
    	logger.warn(str);
    } 
    
    public static void debug(String str) {
    	logger.debug(str);
    } 
    
    public static void trace(String str) {
    	logger.trace(str);
    }
}
