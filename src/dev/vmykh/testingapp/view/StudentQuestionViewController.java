package dev.vmykh.testingapp.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import static dev.vmykh.testingapp.model.Helper.isEmpty;


public class StudentQuestionViewController {
	
	private TestSession testSession;
	private int currentQuestionNumber;
	private Tab currentTab;
	private AnchorPane previousView;
	private AnsweredQuestion currentAQ;
	private List<Variant> variants;
	
	@FXML
	private Label questionNumberLabel;
	
	@FXML
	private Label totalQuestionLabel;
	
	@FXML
	private VBox answersVBox;
	
	@FXML
	private Label questionStrLabel;
	
//	@FXML
//	private RadioButton v1Button;
//	
//	@FXML
//	private RadioButton v2Button;
//	
//	@FXML
//	private RadioButton v3Button;
	
	@FXML
	private VBox contentVBox;
	
	@FXML
	private ImageView imageView;
	
	private List<RadioButton> radioButtons;
	
	@FXML
	private void handleNextQuestion() {
		int nextQuestionNumber = currentQuestionNumber + 1;
//		System.out.println("Next question number = " + nextQuestionNumber);
//		System.out.println("Total size = " + testSession.getAnswers().size());
		if (nextQuestionNumber < testSession.getAnswers().size()) {
			chooseAnswer();
			changeQuestion(nextQuestionNumber);
		}
	}
	
	@FXML
	private void handlePreviousQuestion() {
		int previousQuestionNumber = currentQuestionNumber - 1;
		System.out.println("Previous question number = " + previousQuestionNumber);
		System.out.println("Total size = " + testSession.getAnswers().size());
		if (previousQuestionNumber >= 0) {
			chooseAnswer();
			changeQuestion(previousQuestionNumber);
		}
	}
	
	@FXML
	private void handleFinishTest() {
		chooseAnswer();
		testSession.setTimeFinish(new Date());
		testSession.checkout();
		try {
			TestSessionsManager.getInstance().addTestSession(testSession);
			TestSessionsManager.getInstance().saveTestSessions();
		} catch (PersistenceException | TestSessionCorruptedException e) {
			throw new RuntimeException(e);
		}
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/StudentTestResultView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StudentTestResultController strc = loader.getController();
		strc.initController(currentTab, previousView, testSession);
		currentTab.setContent(ap);
	}
	
	//function made in shitty manner, I know. But  I didn't have enough time
	private void chooseAnswer() {
		try {
			testSession.setChosenAnswer(currentQuestionNumber, -1);
			for (int i = 0; i < variants.size(); ++i) {
				if (((RadioButton)answersVBox.getChildren().get(i)).isSelected()) {
					testSession.setChosenAnswer(currentQuestionNumber, 
							variants.get(i).getId());
				}
			}
//			
//			
//			if (v1Button.isSelected()) {
//				testSession.setChosenAnswer(currentQuestionNumber, 
//						variants.get(0).getId());
//			} else if (v2Button.isSelected()) {
//				testSession.setChosenAnswer(currentQuestionNumber, 
//						variants.get(1).getId());
//			} else if (v3Button.isSelected()) {
//				testSession.setChosenAnswer(currentQuestionNumber, 
//						variants.get(2).getId());
//			} else {
//				testSession.setChosenAnswer(currentQuestionNumber, 
//						-1);
//			}
		} catch (AnsweredQuestionException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void changeQuestion(int targetQuestionNumber) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/StudentQuestionView.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		StudentQuestionViewController sqvc = loader.getController();
		sqvc.initController(currentTab, previousView, testSession, targetQuestionNumber);
		currentTab.setContent(ap);
	}
	
	@FXML
	private void initialize() {
		questionStrLabel.setWrapText(true);
	}
	
	public void initController(Tab tab, AnchorPane previousView, TestSession ts, int questionNumber) {
		currentTab = tab;
		radioButtons = new ArrayList<>();
		this.previousView = previousView;
		this.testSession = ts;
		try {
			currentAQ = ts.getAnswers().get(questionNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}
		this.currentQuestionNumber = questionNumber;
		questionNumberLabel.setText("" + (questionNumber + 1));
		totalQuestionLabel.setText("" + ts.getAnswers().size());
		questionStrLabel.setText(currentAQ.getQuestionStr());
		initImageView();
		initToggleButtons();
	}
	
	
	//function made in shitty manner, I know. But  I didn't have enough time
	/**
	 * should be invoked after currentAQ  (AnsweredQuestion is set)
	 * 
	 */
	private void initToggleButtons() {
		variants = currentAQ.getVariantsInRandomOrder();
		answersVBox.getChildren().clear();
		
		ToggleGroup tg = new ToggleGroup();
		for (int i = 0; i < variants.size(); ++i) {
			RadioButton rb = new RadioButton();
			rb.setText(variants.get(i).getText());
			rb.setToggleGroup(tg);
			rb.setSelected(false);
			rb.setWrapText(true);
			rb.setMaxWidth(600);
			rb.setMaxHeight(1000);
			answersVBox.getChildren().add(rb);
		}
		
		int chosenVariant = currentAQ.getChosenVariantId();
		if (chosenVariant != -1) {
			for (int i = 0; i < variants.size(); ++i) {
				if (chosenVariant == variants.get(i).getId()) {
					((RadioButton)answersVBox.getChildren().get(i)).setSelected(true);
					break;
				}
			}
		}
		
//		
//		v1Button.setText(variants.get(0).getText());
//		v2Button.setText(variants.get(1).getText());
//		v3Button.setText(variants.get(2).getText());
//		ToggleGroup tg = new ToggleGroup();
//		v1Button.setToggleGroup(tg);
//		v2Button.setToggleGroup(tg);
//		v3Button.setToggleGroup(tg);
//		v1Button.setSelected(false);
//		v2Button.setSelected(false);
//		v3Button.setSelected(false);
//		int chosenVariant = currentAQ.getChosenVariantId();
//		if (chosenVariant != -1) {
//			if (chosenVariant == variants.get(0).getId()) {
//				v1Button.setSelected(true);
//			} else if (chosenVariant == variants.get(1).getId()) {
//				v2Button.setSelected(true);
//			} else if (chosenVariant == variants.get(2).getId()) {
//				v3Button.setSelected(true);
//			}
//		}
	}
	
	/**
	 * should be invoked after currentAQ  (AnsweredQuestion is set)
	 * 
	 */
	private void initImageView() {
		if (currentAQ == null) {
			throw new RuntimeException("Trying to init ImageView when currentAQ = null");
		}
		String img = currentAQ.getImageName();
		int elements = contentVBox.getChildren().size();
		if (isEmpty(img)) {
			//if question doesn't have image - remove from content box
			//nodes that are responsible for displaying image
			contentVBox.getChildren().remove(elements - 3, elements);
		} else {
			try {
				ImagesManager.getInstance().getImageByName(img);
			} catch (ImageNotFoundException | ImagePackingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			String absPath = ResourceBundle.RESOURCES_DIR + File.separator 
					+ ResourceBundle.TEMP_DIR + File.separator + img;
			System.out.println(absPath);
			Image image = new Image("file:" + absPath);
			imageView.setImage(image);
			imageView.setFitHeight(image.getHeight());
			imageView.setFitWidth(image.getWidth());
		}
	}
}
