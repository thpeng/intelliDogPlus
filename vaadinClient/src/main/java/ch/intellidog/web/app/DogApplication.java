package ch.intellidog.web.app;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import intelliDOG.ai.framework.Game;


public class DogApplication extends Application{



	@Override
	public void init() {
		Window mainWindow = new Window("Vaadintest Application");
		Label label = new Label("Hello Vaadin user");
		mainWindow.addComponent(label);
		setMainWindow(mainWindow);
	}
}
