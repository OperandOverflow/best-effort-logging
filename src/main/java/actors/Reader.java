package actors;

import controllers.ReadController;
import main.App;

public class Reader {
	
	private static final String PREFIX = "       [Reader] ";
	
	private ReadController controller;
	
	public Reader(ReadController rctrl) {
		this.controller = rctrl;
	}
	
	public void read() {
		App.info(PREFIX + "Reading");
		controller.readFile();
	}
}
