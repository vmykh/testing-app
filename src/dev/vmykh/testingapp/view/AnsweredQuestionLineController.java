package dev.vmykh.testingapp.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.AnsweredQuestion;
import dev.vmykh.testingapp.model.Question;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.view.helpers.Mode;

public class AnsweredQuestionLineController {
	private static final String CORRECT_ANSWER = "Да";
	private static final String NOT_CORRECT_ANSWER = "Нет";
	
	private AnsweredQuestion aq;
//	private Stage mainStage;
	
	@FXML
	private Label questionIdLabel;
	
	@FXML
	private Label questionTextLabel;
	
	@FXML
	private Label answerCorrectnessLabel;
	
	@FXML
	private void initialize() {
		questionTextLabel.setWrapText(false);
	}
	
	
	
	public void initController(AnsweredQuestion aq) {
		this.aq = aq;
		questionIdLabel.setText("" + aq.getId());
		questionTextLabel.setText(aq.getQuestionStr());
		
		boolean correct = aq.getChosenVariantId() == aq.getCorrectVariantNumber();
		answerCorrectnessLabel.setText(correct ? CORRECT_ANSWER : NOT_CORRECT_ANSWER);
	}
}
