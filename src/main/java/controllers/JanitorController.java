package controllers;

import user.DataContainer;

public class JanitorController {
	
	private DataContainer container;
	
	public JanitorController(DataContainer data) {
		this.container = data;
	}
	
	public void cleanRun() {
		container.getClouds().forEach(c -> c.deleteRunBuckets());
	}
	
	public void cleanAll() {
		container.getClouds().forEach(c -> c.deleteAllBuckets());
	}
	
	
}
