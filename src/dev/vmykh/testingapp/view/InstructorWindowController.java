package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.PersistenceManager;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestSessionsManager;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestIsNotReadyException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class InstructorWindowController {
	private static final String AUTHOR = "Author: vmykh";
	private static final String AUTHOR_EMAIL = "E-mail: dev.vmykh@gmail.com";
	private static final String CHANGE_PASSWORD_DIALOG_TITLE = "Изменить пароль";
	
	private static final String NEW_TEST_NAME = "Новый тест";
	
	private Stage windowStage;
	//private TestsManager testManager;
	
	private int startIndexOfTestLinesInVBox;
	private int startIndexOfResultLinesInVBox;
	
	@FXML
	private VBox testLinesContainerVBox;
	
	@FXML
	private Tab currentTab;
	
	@FXML
	private Tab resultsTab;
	
	
	
	@FXML
	private VBox resultLinesContainerVBox;
	
	@FXML
	private Label authorInfo;
	
	@FXML
	private Label emailLabel;
	
	@FXML
	private void initialize() {
		authorInfo.setText(AUTHOR);
		emailLabel.setText(AUTHOR_EMAIL);
	}
	
//	@FXML
//	private void initialize() {
//		
//	}
	
	
	
	@FXML 
	private void addNewHandler() {
		try {
			Test t = new Test();
			t.setName(NEW_TEST_NAME);
			t.setAvailable(false);
			t.setTimeForPassing(50*60*1000);
			t.setQuestionAmountForSession(15);
			t.setMinCorrectAnswersToPass(10);
			TestsManager.getInstance().addTest(t);
			TestsManager.getInstance().saveTests();
		} catch (TestIsNotReadyException | PersistenceException |
				TestCorruptedException e) {
			throw new RuntimeException(e);
		}
		updateTestLines();
		
	}
	
	public void initController(Stage stage) {
		windowStage = stage;
		windowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	              PersistenceManager.getInstance().clearTempDir();
	          }
	      });    
		startIndexOfTestLinesInVBox = testLinesContainerVBox.getChildren().size();
		startIndexOfResultLinesInVBox = resultLinesContainerVBox.getChildren().size();
		updateTestLines();
		updateResultLines();
	}
	
	@FXML
	private void handleChangePassword() {
		Dialogs.showChangePasswordDialog(CHANGE_PASSWORD_DIALOG_TITLE);
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
//			System.out.println("Another iteration");
			loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/InstructorWindowTestLine.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			InstructorWindowTestLineController line = loader.getController();
			line.initController(currentTab, windowStage, this, t);
			testLinesContainerVBox.getChildren().add(ap);
			
		}
	}
	
	public void updateResultLines() {
		TestSessionsManager tsm;
		try {
			tsm = TestSessionsManager.getInstance();
		} catch (PersistenceException | TestSessionCorruptedException e) {
			throw new RuntimeException(e);
		}
		
		FXMLLoader loader;
		List<TestSession> testSessions = tsm.getAllTestSessions();
		
		//sorting by group
		testSessions.sort(new Comparator<TestSession>() {

			@Override
			public int compare(TestSession o1, TestSession o2) {
				return o1.getStudent().getGroup().compareTo(o2.getStudent().getGroup());
			}
			
		});
		
		
		System.out.println(testSessions.size());
		resultLinesContainerVBox.getChildren().remove(
				startIndexOfResultLinesInVBox, resultLinesContainerVBox.getChildren().size());
		for (TestSession ts : testSessions) {
//			System.out.println("Another iteration" + ts + "\n\n\n");
			loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/InstructorWindowResultLine.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			InstructorWindowResultLineController line = loader.getController();
			line.initController(resultsTab, windowStage, this, ts);
			resultLinesContainerVBox.getChildren().add(ap);			
		}
	}
	
//	public void setWindowStage(Stage stage) {
//		windowStage = stage;
//	}
}
