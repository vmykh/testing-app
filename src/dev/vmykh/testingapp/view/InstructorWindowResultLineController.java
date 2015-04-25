package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.NoSuchElementException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestSessionsManager;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;
import static dev.vmykh.testingapp.model.Helper.getDateFormat;

public class InstructorWindowResultLineController {
	private static final String CONFIRM_DELETE_TITLE = "Подтверждение действия";
	private static final String CONFIRM_DELETE_MSG = "Вы уверены, что хотите удалить результат?";

	private static final String TEST_PASSED = "Да";
	private static final String TEST_NOT_PASSED = "Нет";
	
	private static final String DATE_NOT_KNOWN = "Неизвестно";
	
	@FXML
	private Label testIdLabel;
	
	@FXML
	private Label studentNameLabel;	
	
	private Tab currentTab;
	private Stage mainStage;
	private InstructorWindowController iwc;
	private TestSession ts;
	
//	@FXML
//	private Label dateLabel;
	
	@FXML
	private Label groupLabel;
	
	@FXML
	private Label testPassedLabel;
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleDelete() {
		if (Dialogs.showConfirmationDialog(CONFIRM_DELETE_TITLE, CONFIRM_DELETE_MSG)) {
			try {
				TestSessionsManager.getInstance().removeTestSessionById(ts.getId());
				TestSessionsManager.getInstance().saveTestSessions();
//				TestsManager.getInstance().removeTestById(test.getId());
//				TestsManager.getInstance().saveTests();
			} catch (PersistenceException | TestSessionCorruptedException e) {
				throw new RuntimeException(e);
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
			iwc.updateResultLines();
		}
	}
	
	@FXML
	private void handleDetails() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/SessionResultView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		System.out.println("From handler of details middle");
		SessionResultViewController srvc = loader.getController();
		srvc.initController(currentTab, ts);
		System.out.println("From handler of details middle-2");
//		System.out.println("from edit item");
		currentTab.setContent(ap);
//		System.out.println("From handler of details middle-3");
		
	}
	
	public void initController(Tab tab, Stage stage, 
			InstructorWindowController iwc, TestSession ts) {
		currentTab = tab;
		mainStage = stage;
		this.iwc = iwc;
		this.ts = ts;
		setTestSessionParameters();
	}
	
	private void setTestSessionParameters() {
		testIdLabel.setText("" + ts.getTestId());
		String studentName = ts.getStudent().getSurname() + " " + ts.getStudent().getName();
		studentNameLabel.setText(studentName);
		String date;
		if (ts.getTimeFinish() != null) {
			date = getDateFormat().format(ts.getTimeFinish());
		} else {
			date = DATE_NOT_KNOWN;
		}		
		groupLabel.setText(ts.getStudent().getGroup());
		String passed = ts.isPassed() ? TEST_PASSED : TEST_NOT_PASSED;
		testPassedLabel.setText(passed);		
	}
}
