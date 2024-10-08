package user;

import java.util.ArrayList;
import java.util.List;

import interfaces.CloudInfo;
import interfaces.CloudType;

/**
 * A class that contains modules of different clouds and can add ad remove easily
 */
public class DataContainer {
	
	private static DataContainer instance = null;
	
	private List<CloudInfo> clouds;
	
	private DataContainer() {
		clouds = new ArrayList<CloudInfo>();
	}
	
	/**
	 * Returns an instance of DataContainer
	 * @return An instance of DataContainer
	 */
	public static synchronized DataContainer getInstance() {
		if (instance == null) {
			instance = new DataContainer();
			return instance;
		}
		return instance;
	}
	
	/**
	 * Adds a cloud module to the container
	 * @param info The cloud module to be added
	 */
	public void addCloud(CloudInfo info) {
		clouds.add(info);		
	}
	
	/**
	 * Returns all cloud modules
	 * @return A list of all cloud modules
	 */
	public List<CloudInfo> getClouds() {
		return this.clouds;
	}
	
	/**
	 * Returns a cloud module of a certain type
	 * @param type The module type 
	 * @return The type of module that was searching for, null if
	 * 			doesn't exist in the container
	 */
	public CloudInfo getCloud(CloudType type) {
		for (CloudInfo info : clouds)
			if (info.getCloudType() == type)
				return info;
		return null;
	}
}