package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.PersistenceManager;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestIsNotReadyException;

public class StudentWindowController {
	
	private Stage windowStage;
	//private TestsManager testManager;
	
	private int startIndexOfTestLinesInVBox;
	
	@FXML
	private VBox testLinesContainerVBox;
	
	@FXML
	private Tab currentTab;
	
	@FXML
	private void initialize() {
		
	}
	
	public void initController(Stage stage) {
		windowStage = stage;
		windowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	              PersistenceManager.getInstance().clearTempDir();
	          }
	      });
		startIndexOfTestLinesInVBox = testLinesContainerVBox.getChildren().size();
		updateTestLines();
	}
	
	public void updateTestLines() {
		TestsManager tm;
		try {
			tm = TestsManager.getInstance();
		} catch (TestCorruptedException | PersistenceException e) {
			throw new RuntimeException(e);
		}
		
		FXMLLoader loader;
		List<Test> tests = tm.getAllTests();
		testLinesContainerVBox.getChildren().remove(
				startIndexOfTestLinesInVBox, testLinesContainerVBox.getChildren().size());
		for (Test t : tests) {
			if (t.isAvailable()) {

				loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource("view/StudentWindowTestLine.fxml"));
				AnchorPane ap;
				try {
					ap = (AnchorPane)loader.load();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				StudentWindowTestLineController line = loader.getController();
				line.initController(currentTab, windowStage, this, t);
				testLinesContainerVBox.getChildren().add(ap);
			}
		}
	}
}
