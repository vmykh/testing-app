package dev.vmykh.testingapp.view;

import static dev.vmykh.testingapp.model.Helper.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.AnsweredQuestion;
import dev.vmykh.testingapp.model.ImagesManager;
import dev.vmykh.testingapp.model.ResourceBundle;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestSessionsManager;
import dev.vmykh.testingapp.model.Variant;
import dev.vmykh.testingapp.model.exceptions.AnsweredQuestionException;
import dev.vmykh.testingapp.model.exceptions.ImageNotFoundException;
import dev.vmykh.testingapp.model.exceptions.ImagePackingException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;

public class StudentTestResultController {
	private static final String SUCCESS_TEXT = "Вы прошли тест";
	private static final String SUCCESS_COLOR = "#059729";
	private static final String FAIL_TEXT = "Вы не прошли тест";
	private static final String FAIL_COLOR = "#FF0E0E";
	
	private TestSession testSession;
	private Tab currentTab;
	private AnchorPane testListView;
	
	@FXML
	private Label resultTitleLabel;
	
	@FXML
	private Label correctAnswersLabel;
	
	@FXML
	private Label totalAnswersLabel;
	
	@FXML
	private void handleBackToTests() {
		currentTab.setContent(testListView);
	}
	
	@FXML
	private void initialize() {
		
	}
	
	public void initController(Tab tab, AnchorPane testListView, TestSession ts) {
		this.currentTab = tab;
		this.testListView = testListView;
		this.testSession = ts;
		
		if (ts.isPassed()) {
			resultTitleLabel.setTextFill(Color.web(SUCCESS_COLOR));
			resultTitleLabel.setText(SUCCESS_TEXT);
		} else {
			resultTitleLabel.setTextFill(Color.web(FAIL_COLOR));
			resultTitleLabel.setText(FAIL_TEXT);
		}
		
		correctAnswersLabel.setText("" + ts.getCorrectAnswersAmount());
		totalAnswersLabel.setText("" + ts.getTotalAnswersAmount());		
	}
	
	
	
}
