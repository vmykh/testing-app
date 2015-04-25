package dev.vmykh.testingapp.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.rules.TestName;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Helper;
import dev.vmykh.testingapp.model.Question;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.Variant;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.QuestionException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestIsNotReadyException;
import dev.vmykh.testingapp.view.helpers.Mode;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import org.controlsfx.control.action.;;

import static dev.vmykh.testingapp.model.Helper.areEqual;
import static dev.vmykh.testingapp.model.Helper.isEmpty;

public class EditTestViewController {
	private static final String CONFIRM_DIALOG_TITLE = "Подтверждение";
	private static final String CONFIRM_DIALOG_MSG = "Вы уверены, что хотите завершить редактирование?"
			+ "\nНесохранённые данные будут потеряны.";
	
	private static final String INPUT_ERRORS_TITLE = "Ошибки ввода";
	private static final String AVAILABILITY_BOX_YES_OPTION = "Да";
	private static final String AVAILABILITY_BOX_NO_OPTION = "Нет";
	
	
	private static final int MAX_QUESTION_FOR_SESSION = 1000;
	private static final int MIN_QUESTION_FOR_SESSION = 1;
	
	
	private static final String TEST_NAME_EMPTY_ERROR = "Название теста не может быть пустым";
	private static final String INVALID_NUMBER_OF_QUESTIONS_FOR_SESSION_ERROR = 
			"Количество вопросов для сессии может быть от "
			+ MIN_QUESTION_FOR_SESSION 
			+ " до "
			+ MAX_QUESTION_FOR_SESSION;
	private static final String QUESTIONS_FOR_SESSION_FORMAT_ERROR = 
			"Некоректное значение поля \"Вопросов в сессии\"";
	private static final String MIN_FOR_PASSING_LESS_THAN_ONE_ERROR = 
			"Минимум вопросов для здачи не может быть меньше одного";
	private static final String MIN_FOR_PASSING_BIGGER_THAN_FOR_SESSION_ERROR = 
			"Минимум вопросов для здачи не может быть больше чем вопросов в сессии";
	private static final String MIN_FOR_PASSING_FORMAT_ERROR = 
			"Некоректное значение поля \"Минимум для здачи\"";
	private static final String AVAILABILITY_ERROR_NOT_ENOUGH_QUESTION_FOR_SESSION = 
			"Тест не может быть доступен. Недостаточно вопросов для сессии";
	
	
	private static final int MINUTE = 1000 * 60;
	private static final int FIFTY_MINUTES = 50 * MINUTE;
	

	
	
	@FXML
	private Label testIdLabel;
	
	@FXML
	private TextField nameField;
	
	@FXML
	private TextArea descriptionArea;
	
	@FXML
	private TextField NumberOfQuestionForSessionField;
	
	@FXML
	private TextField minForPassing;
	
	@FXML
	private TextField passwordField;
	
	@FXML
	private ChoiceBox<String> availabilityChoiceBox;
	
	@FXML
	private VBox contentBox;
	
	
	private Tab currentTab;
	private Stage mainStage;
	private InstructorWindowController iwc;
	private Test test;
	private AnchorPane previousView;
	private int startIndexOfQuestionLinesInVBox;
	
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void handleAddNewQuestion() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/EditQuestionView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		EditQuestionViewController eqvc = loader.getController();
		Question q = new Question();
		q.setQuestionStr("");
		int numberOfVariant = 3;
		q.setNumberOfVariants(numberOfVariant);
		List<Variant> vs = new ArrayList<>(numberOfVariant);
		vs.add(new Variant(1, ""));
		vs.add(new Variant(2, ""));
		vs.add(new Variant(3, ""));
		try {
			q.setVariants(vs);
			q.setCorrectVariantNumber(1);
		} catch (QuestionException e) {
			throw new RuntimeException(e);
		}
		eqvc.initController(currentTab, this, test, q, Mode.CREATE);
		currentTab.setContent(ap);
	}
	
	@FXML
	private void handleSave() {
		String errors = "";
		errors = getInputErrors();
		if (!Helper.isEmpty(errors)){
			Dialogs.showErrorDialog(INPUT_ERRORS_TITLE, errors);
		} else {
			saveTest();
			restorePreviousView();
		}
	}
	
	@FXML
	private void handleCancel() {
		if (dataChanged()) {
			if (Dialogs.showConfirmationDialog(
						CONFIRM_DIALOG_TITLE, 
						CONFIRM_DIALOG_MSG)) {
				restorePreviousView();
			}				
		} else {
			restorePreviousView();
		}
	}
	
	private void restorePreviousView() {
		iwc.updateTestLines();
		currentTab.setContent(previousView);
	}
//	public void setCurrentTab(Tab tab) {
//		currentTab = tab;
//	}
	
//	public void setTestsTabView(AnchorPane ap) {
//		
//		previousView = (AnchorPane)tab.getContent();
//	}
	
	public void initController(Tab tab, Stage stage, InstructorWindowController iwc, Test t) {
		currentTab = tab;
		this.iwc = iwc;
		previousView = (AnchorPane)tab.getContent();
		mainStage = stage;
		startIndexOfQuestionLinesInVBox = contentBox.getChildren().size();
		setTest(t);		
	}
	
	private void setTest(Test t) {
		test = t;
		testIdLabel.setText("" + test.getId());
		nameField.setText(t.getName());
		descriptionArea.setText(t.getDescription());
		NumberOfQuestionForSessionField.setText("" + t.getQuestionAmountForSession());
		minForPassing.setText("" + t.getMinCorrectAnswersToPass());
		passwordField.setText(t.getPassword());
		
		
//		System.out.println("Initially test availability: " + t.isValid());
		availabilityChoiceBox.setItems(FXCollections.observableArrayList(
				AVAILABILITY_BOX_YES_OPTION,
				AVAILABILITY_BOX_NO_OPTION));
		availabilityChoiceBox.getSelectionModel().select(t.isAvailable() ? 0 : 1);
		
		
		updateQuestionLines();
	}
	
	public void updateQuestionLines () {
		
		FXMLLoader loader;
		List<Question> qs = test.getAllQuestions();
		System.out.println(startIndexOfQuestionLinesInVBox);
		contentBox.getChildren().remove(startIndexOfQuestionLinesInVBox, contentBox.getChildren().size());
		for(Question q : qs) {
			loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/QuestionLine.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			QuestionLineController qlc = loader.getController();
			qlc.initController(currentTab, this, test, q);
			contentBox.getChildren().add(ap);
		}
	}
	
	private boolean dataChanged() {
		if (!areEqual(nameField.getText(), test.getName())) {
			return true;
		}
		
		if (!areEqual(passwordField.getText(), test.getPassword())) {
			return true;
		}
		
		if (!areEqual(descriptionArea.getText(), test.getDescription())) {
			return true;
		}
		if (!areEqual(NumberOfQuestionForSessionField.getText(), 
				test.getQuestionAmountForSession() + "")) {
			System.out.println("number of question");
			return true;
		}
		if (!areEqual(minForPassing.getText(), 
				test.getMinCorrectAnswersToPass() + "")) {
			System.out.println("min number for passing");
			return true;
		}
		
		String availabilityStr = availabilityChoiceBox.getSelectionModel().getSelectedItem();
		boolean available = availabilityStr.equals(AVAILABILITY_BOX_YES_OPTION) ? true : false;
		if (available != test.isAvailable()) {
			System.out.println("availability");
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * 
	 * @return if there are input errors returns String with errors otherwise emtpy String
	 */
	private String getInputErrors() {
		String errors = "";
		if (isEmpty(nameField.getText())) {
			errors += TEST_NAME_EMPTY_ERROR + "\n";
		}
		
		int questionsForSession;
		try {
			questionsForSession = Integer.parseInt(
					NumberOfQuestionForSessionField.getText());
			if (questionsForSession < 1 || questionsForSession > 1000) {
				errors += INVALID_NUMBER_OF_QUESTIONS_FOR_SESSION_ERROR + "\n";
				return errors;
			}
		} catch (NumberFormatException e) {
			errors += QUESTIONS_FOR_SESSION_FORMAT_ERROR + "\n";
			return errors;
		}
		
		try {
			int minQuestionsForPassing = Integer.parseInt(minForPassing.getText());
			if (minQuestionsForPassing < 1) {
				errors += MIN_FOR_PASSING_LESS_THAN_ONE_ERROR + "\n";
				return errors;
			} else if (minQuestionsForPassing > questionsForSession) {
				errors += MIN_FOR_PASSING_BIGGER_THAN_FOR_SESSION_ERROR + "\n";
				return errors;
			}
		} catch (NumberFormatException e) {
			errors += MIN_FOR_PASSING_FORMAT_ERROR + "\n";
			return errors;
		}
		
		switch(availabilityChoiceBox.getSelectionModel().getSelectedItem()) {
		case AVAILABILITY_BOX_YES_OPTION:
			if (test.getAllQuestions().size() < questionsForSession) {
				errors += AVAILABILITY_ERROR_NOT_ENOUGH_QUESTION_FOR_SESSION + "\n";
			}
			break;
		}		
		return errors;
	}
	
	
	
	private void saveTest() {
		test.setName(nameField.getText());
		test.setDescription(descriptionArea.getText());
		test.setQuestionAmountForSession(
				Integer.parseInt(NumberOfQuestionForSessionField.getText()));
		test.setMinCorrectAnswersToPass(
				Integer.parseInt(minForPassing.getText()));
		
		test.setTimeForPassing(FIFTY_MINUTES);  //15 minutes
		test.setPassword(passwordField.getText());
		
		try {
			switch (availabilityChoiceBox.getSelectionModel().getSelectedItem()) {
			case AVAILABILITY_BOX_YES_OPTION:
				test.setAvailable(true);
				break;
			case AVAILABILITY_BOX_NO_OPTION:
				test.setAvailable(false);
				break;
			}
		} catch (TestIsNotReadyException e) {
			throw new RuntimeException(e);
		}
		
		
		
		try {
			TestsManager.getInstance().saveTests();
		} catch (PersistenceException | TestCorruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
