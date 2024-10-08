package actors;

import controllers.JanitorController;

public class Janitor {
	
	private JanitorController controller;
	
	public Janitor(JanitorController jctrl) {
		controller = jctrl;
	}
	
	public void cleanUp(boolean allRegistered) {
		if (allRegistered)
			controller.cleanRun();
			// TODO
	}
	
	public void cleanAll() {
		controller.cleanAll();
	}
	
}
