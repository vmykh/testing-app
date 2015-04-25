package dev.vmykh.testingapp.view;

import java.io.IOException;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Question;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.view.helpers.Mode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class QuestionLineController {
	private static final String CONFIRM_DELETE_TITLE = "Подтверждение действия";
	private static final String CONFIRM_DELETE_MSG = "Вы уверены, что хотите удалить вопрос?";
	
	private Question question;
	private Test currentTest;
	private Tab currentTab;
	private EditTestViewController etvc;
//	private Stage mainStage;
	
	@FXML
	private Label questionIdLabel;
	
	@FXML
	private Label questionTextLabel;
	
	@FXML
	private void initialize() {
		questionTextLabel.setWrapText(false);
	}
	
	@FXML
	private void handleEdit() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/EditQuestionView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		EditQuestionViewController eqvc = loader.getController();
		eqvc.initController(currentTab, etvc, currentTest, question, Mode.EDIT);
		currentTab.setContent(ap);
	}
	
	@FXML
	private void handleDelete() {
		if (Dialogs.showConfirmationDialog(CONFIRM_DELETE_TITLE, CONFIRM_DELETE_MSG)) {
			currentTest.deleteQuestionById(question.getId());
			try {
				TestsManager.getInstance().saveTests();
			} catch (PersistenceException | TestCorruptedException e) {
				throw new RuntimeException(e);
			}
			etvc.updateQuestionLines();
		}
	}
	
	public void initController(Tab tab, EditTestViewController etvc, Test t, Question q) {
		currentTab = tab;
		setQuestion(q);
		currentTest = t;
		this.etvc = etvc;
	}
	
	private void setQuestion(Question q) {
		question = q;
		questionIdLabel.setText("" + q.getId());
		questionTextLabel.setText(q.getQuestionStr().replace("\n", " "));
	}
}
