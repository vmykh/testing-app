package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.NoSuchElementException;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.view.helpers.Mode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InstructorWindowTestLineController {

	private static final String CONFIRM_DELETE_TITLE = "Подтверждение действия";
	private static final String CONFIRM_DELETE_MSG = "Вы уверены, что хотите удалить тест?";
	
	private static final String TEST_AVAILABLE_STR = "Да";
	private static final String TEST_NOT_AVAILABLE_STR = "Нет";
	
	@FXML
	private Label testIdLabel;
	
	@FXML
	private Label testNameLabel;
	
	
	private Tab currentTab;
	private Stage mainStage;
	private InstructorWindowController iwc;
	private Test test;
	
	@FXML
	private Label testQuestionAmountLabel;
	
	@FXML
	private Label testAvailabilityLabel;
	
	
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleDelete() {
		if (Dialogs.showConfirmationDialog(CONFIRM_DELETE_TITLE, CONFIRM_DELETE_MSG)) {
			try {
				TestsManager.getInstance().removeTestById(test.getId());
				TestsManager.getInstance().saveTests();
			} catch (PersistenceException | TestCorruptedException e) {
				throw new RuntimeException(e);
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
			iwc.updateTestLines();
		}
	}
	
	@FXML
	private void handleEdit() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/EditTestView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		EditTestViewController etvc = loader.getController();
		etvc.initController(currentTab, mainStage, iwc, test);
//		System.out.println("from edit item");
		currentTab.setContent(ap);
		
	}
	
//	private void setCurrentTab(Tab tab) {
//		currentTab = tab;
//	}
	
	public void initController(Tab tab, Stage stage, 
			InstructorWindowController iwc, Test t) {
		currentTab = tab;
		mainStage = stage;
		this.iwc = iwc;
		setTestParameters(t);
	}
	
	private void setTestParameters(Test test) {
		testIdLabel.setText("" + test.getId());
		testNameLabel.setText(test.getName());
		testQuestionAmountLabel.setText("" + test.getAllQuestions().size());
		String availability = test.isAvailable() ? TEST_AVAILABLE_STR : TEST_NOT_AVAILABLE_STR;
		testAvailabilityLabel.setText(availability);
		this.test = test;
	}
}
