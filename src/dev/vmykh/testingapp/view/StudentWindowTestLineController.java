package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Student;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.NotEnoughQuestionsException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;

public class StudentWindowTestLineController {

	
	@FXML
	private Label testIdLabel;
	
	@FXML
	private Label testNameLabel;
	
	
	private Tab currentTab;
	private Stage mainStage;
	private StudentWindowController swc;
	private Test test;
	
	@FXML
	private Label numberOfQuestioLabel;
	
	@FXML
	private Label minForPassingLabel;
	
	
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleStart() {
		Student student = Dialogs.showNameSurnameDialog(test.getPassword());
		//System.out.println("from test line - group: " + student.getGroup());
		if (student != null) {
			TestSession ts;
			try {
				ts = new TestSession(test, student);
			} catch (NotEnoughQuestionsException e) {
				throw new RuntimeException(e);
			}
			ts.setTimeStart(new Date());
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/StudentQuestionView.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			StudentQuestionViewController sqvc = loader.getController();
			sqvc.initController(currentTab, (AnchorPane)currentTab.getContent(), ts, 0);
			currentTab.setContent(ap);
		}
	}
	
	public void initController(Tab tab, Stage stage, 
			StudentWindowController swc, Test t) {
		currentTab = tab;
		mainStage = stage;
		this.swc = swc;
		setTestParameters(t);
	}
	
	private void setTestParameters(Test test) {
		testIdLabel.setText("" + test.getId());
		testNameLabel.setText(test.getName());
		numberOfQuestioLabel.setText("" + test.getQuestionAmountForSession());
		minForPassingLabel.setText("" + test.getMinCorrectAnswersToPass());
		this.test = test;
	}
}
