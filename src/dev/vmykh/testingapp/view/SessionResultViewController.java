package dev.vmykh.testingapp.view;

import static dev.vmykh.testingapp.model.Helper.areEqual;
import static dev.vmykh.testingapp.model.Helper.isEmpty;
import static dev.vmykh.testingapp.model.Helper.getDateFormat;
import static dev.vmykh.testingapp.model.Helper.getTimeFormat;

import java.io.IOException;
import java.text.Format;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.imgscalr.Main;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.AnsweredQuestion;
import dev.vmykh.testingapp.model.Question;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestIsNotReadyException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SessionResultViewController {
	
	private Tab currentTab;
	private AnchorPane previousView;
	
	private static final String TEST_NOT_FOUND_ERROR = "Ошибка. Тест был удален.";
	private static final String TEST_PASSED = "Тест пройден";
	private static final String TEST_NOT_PASSED = "Тест не пройден";
	private static final String TIME_UNKNOWN = "Неизвестно";
	@FXML
	private Label testIdLabel;
	
	@FXML
	private Label testNameLabel;
	
	@FXML
	private Label studentNameLabel;
	
	@FXML
	private Label studentSurnameLabel;
	
	@FXML
	private Label resultLabel;
	
	@FXML
	private Label correctAnswersLabel;
	
	@FXML
	private Label totalQuestionsLabel;
	
	@FXML
	private Label percentOfCorrectAnswersLabel;
	
	@FXML
	private Label timeStartLabel;
	
	@FXML
	private Label timeFinishLabel;
	
	@FXML
	private Label groupLabel;
	
	@FXML
	private Label timeElapsedLabel;
	
	@FXML
	private VBox contentVBox;
	
	@FXML
	private void initialize() {
		
	}
	
	
	
	
	
	
	
	
	
	
	@FXML
	private void handleBack() {
		currentTab.setContent(previousView);
	}
	
	public void initController(Tab tab, TestSession ts) {
		currentTab = tab;
		previousView = (AnchorPane)tab.getContent();
		
		//System.out.println("Group: " + ts.getStudent().getGroup());
		
		testIdLabel.setText("" + ts.getTestId());
		
		String testName = TEST_NOT_FOUND_ERROR;
		try {
			testName = TestsManager.getInstance()
					.getTestById(ts.getTestId()).getName();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (PersistenceException | TestCorruptedException e) {
			throw new RuntimeException(e);
		}
		
		testNameLabel.setText(testName);
		studentNameLabel.setText(ts.getStudent().getName());
		studentSurnameLabel.setText(ts.getStudent().getSurname());
		groupLabel.setText(ts.getStudent().getGroup());
		
		resultLabel.setText(ts.isPassed() ? TEST_PASSED : TEST_NOT_PASSED);
		correctAnswersLabel.setText("" + ts.getCorrectAnswersAmount());
		totalQuestionsLabel.setText("" + ts.getTotalAnswersAmount());
		
		String percentage = String.format("%.2f", 
				100 * ts.getCorrectAnswersAmount() / ((float)ts.getTotalAnswersAmount()));
		
		percentOfCorrectAnswersLabel.setText(percentage + "%");
		initTimeLabels(ts);
		initAnswerList(ts);
			
	}
	
	private void initAnswerList(TestSession ts) {
		List<AnsweredQuestion> aqs = ts.getAnswers();
		for (AnsweredQuestion aq : aqs) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/AnsweredQuestionLine.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			AnsweredQuestionLineController aqlc = loader.getController();
			aqlc.initController(aq);
			contentVBox.getChildren().add(ap);
		}
	}
	
	private void initTimeLabels(TestSession ts) {
		String time = TIME_UNKNOWN;
		if (ts.getTimeStart() != null) {
			time = getDateFormat().format(ts.getTimeStart());
		}
		timeStartLabel.setText(time);
		
		time = TIME_UNKNOWN;
		if (ts.getTimeFinish() != null) {
			time = getDateFormat().format(ts.getTimeFinish());
		}
		timeFinishLabel.setText(time);
		
		time = TIME_UNKNOWN;
		if (ts.getTimeFinish() != null && ts.getTimeStart() != null) {
//			time = getTimeFormat().format(
//					new Date(ts.getTimeFinish().getTime() - ts.getTimeStart().getTime())
//					);
			long start = ts.getTimeStart().getTime();
			long finish = ts.getTimeFinish().getTime();
			
			long elapsedInSeconds = (finish - start) / 1000;
			long seconds = elapsedInSeconds % 60;
			long minutes = (elapsedInSeconds / 60) % 60;
			long hours = elapsedInSeconds / (60 * 60);
			time = convert(hours) + ":" + convert(minutes) + ":" + convert(seconds);
			
		}
		timeElapsedLabel.setText(time);
	}
	
	private String convert(long number) {
		return (number / 10) > 0 ? "" + number : "0" + number;
	}
	
	
	
	
	
	
}
