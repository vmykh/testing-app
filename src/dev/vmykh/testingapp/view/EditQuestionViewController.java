package dev.vmykh.testingapp.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import static dev.vmykh.testingapp.model.Helper.isEmpty;
import static dev.vmykh.testingapp.model.Helper.areEqual;
import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.view.helpers.Mode;
import dev.vmykh.testingapp.model.Helper;
import dev.vmykh.testingapp.model.ImagesManager;
import dev.vmykh.testingapp.model.Question;
import dev.vmykh.testingapp.model.ResourceBundle;
import dev.vmykh.testingapp.model.Test;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.Variant;
import dev.vmykh.testingapp.model.exceptions.ImageEditingException;
import dev.vmykh.testingapp.model.exceptions.ImageFormatNotSupportedException;
import dev.vmykh.testingapp.model.exceptions.ImageNotFoundException;
import dev.vmykh.testingapp.model.exceptions.ImagePackingException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.QuestionException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static javafx.stage.FileChooser.ExtensionFilter;;

public class EditQuestionViewController {
	private static final int MIN_NUMBER_OF_VARIANTS = 3;
	private static final int MAX_NUMBER_OF_VARIANTS = 7;
	
	private static final String CONFIRM_DIALOG_TITLE = "Подтверждение";
	private static final String CONFIRM_DIALOG_MSG = "Вы уверены, что хотите завершить редактирование?"
			+ "\nНесохранённые данные будут потеряны.";
	
	private static final String INPUT_ERRORS_TITLE = "Ошибки ввода";
	
	private static final String CANT_BE_EMPTY = "не может быть пустым";
	private static final String QUESTION_STR_EMPTY_ERROR = "Поле \"Вопрос\" "
			+ CANT_BE_EMPTY;
	private static final String VARIANT_STR_EMPTY_ERROR = "Поле \"Вариант %d\" "
			+ CANT_BE_EMPTY;
//	private static final String V2_STR_EMPTY_ERROR = "Поле \"Вариант %s\" "
//			+ CANT_BE_EMPTY;
//	private static final String V3_STR_EMPTY_ERROR = "Поле \"Вариант 3\" "
//			+ CANT_BE_EMPTY;
	
//	private static final int NUMBER_OF_VARIANTS = 3;
	
	private static final int START_OF_VARIANTS_GRID_ROW_INDEX = 3;
	
	
	@FXML
	private Label idLabel;
	
	@FXML
	private TextArea questionStrArea;
	
//	@FXML
//	private TextArea v1Area;
//	
//	@FXML
//	private TextArea v2Area;
//	
//	@FXML
//	private TextArea v3Area;
	
	@FXML
	private ChoiceBox<String> numberOfVariantsBox;
	
	@FXML
	private ChoiceBox<String> correctVariantBox;
	
	@FXML
	private VBox variantsVBox;
	
	private List<VariantLineInstructorController> variantControllers;
	
	@FXML
	private GridPane gridTable;
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Button uploadImageButton;
	
	@FXML
	private Button deleteImageButton;
	
	@FXML
	private Button updateImageButton;
	
	@FXML
	private HBox imageButtonsBox;
	
//	public static enum Mode {
//		CREATE, EDIT
//	}
	
	private Question question;
	private Test currentTest;
	private Tab currentTab;
	private AnchorPane previousView;
//	private Stage mainStage;
	private EditTestViewController editTestViewController;
	private String lastUploadedImageName;
	private Mode mode;
	
	private boolean deleteQuestionImage;
	
	
	
	@FXML
	private void initialize() {
		deleteQuestionImage = false;
	}
	
	@FXML
	private void handleUpdateImage () {
		File selectedFile = showFileChooser();
		if (selectedFile != null) {
			if (!Helper.isEmpty(lastUploadedImageName)) {
				deleteImageByName(lastUploadedImageName);
			}
			loadImage(selectedFile);
		}
	}
	
	@FXML
	private void handleDeleteImage () {
		if (!Helper.isEmpty(lastUploadedImageName)) {
			deleteImageByName(lastUploadedImageName);
			lastUploadedImageName = null;
		}
		deleteQuestionImage = true;
		updateImagePart(null);
	}
	
	@FXML
	private void handleUploadImage () {		 
//		 FileChooser fileChooser = new FileChooser();
//		 fileChooser.setTitle("Открыть файл");
//		 fileChooser.getExtensionFilters().addAll(
//		         new ExtensionFilter("Изображения", "*.png", "*.jpg", "*.gif"));
		 File selectedFile = showFileChooser();
//		 if (!Helper.isEmpty(lastUploadedImageName)) {
//			 deleteImageByName(lastUploadedImageName);
//		 }
		 if (selectedFile != null) {
			loadImage(selectedFile);
		 }
		 deleteQuestionImage = false;
	}
	
	private File showFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Открыть файл");
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Изображения", "*.png", "*.jpg", "*.gif"));
		return fileChooser.showOpenDialog(null);
	}
	
	private void loadImage(File imgFile) {
		try {
			lastUploadedImageName = ImagesManager.getInstance()
					.addImage(imgFile.getAbsolutePath());
			ImagesManager.getInstance().getImageByName(lastUploadedImageName);
		} catch (FileNotFoundException | ImageFormatNotSupportedException
				| ImageEditingException | ImagePackingException | ImageNotFoundException e) {
			throw new RuntimeException(e);
		}
		updateImageView(lastUploadedImageName);
//		String loadedImageAbsPath = ResourceBundle.RESOURCES_DIR
//				+ File.separator + ResourceBundle.TEMP_DIR + File.separator
//				+ lastUploadedImageName;
//		Image image = new Image("file:" + loadedImageAbsPath);
//		System.out.println(loadedImageAbsPath);
//	    imageView.setImage(image);
//	    imageView.setFitWidth(image.getWidth());
//	    imageView.setFitHeight(image.getHeight());
	    updateImageButtons(true);
	}
	
	private void updateImageView(String imgName) {
		String imageAbsPath = ResourceBundle.RESOURCES_DIR
				+ File.separator + ResourceBundle.TEMP_DIR + File.separator
				+ imgName;
		Image image = new Image("file:" + imageAbsPath);
//		System.out.println(loadedImageAbsPath);
	    imageView.setImage(image);
	    imageView.setFitWidth(image.getWidth());
	    imageView.setFitHeight(image.getHeight());
	}
	
	@FXML
	private void handleSave() {
		String errors = "";
		errors = getInputErrors();
		if (!Helper.isEmpty(errors)){
			Dialogs.showErrorDialog(INPUT_ERRORS_TITLE, errors);
		} else {
			saveQuestion();
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
		editTestViewController.updateQuestionLines();
		currentTab.setContent(previousView);
	}
	
	
	public void updateVariants() {
		List<String> prevAnswers = new ArrayList<>();
		for (VariantLineInstructorController controller : variantControllers) {
			prevAnswers.add(controller.getText());
		}
		updateVariants(prevAnswers);
	}
	public void updateVariants(List<String> prevAnswers) {
		
		//Saving previous answers and
//		List<String> prevAnswers = new ArrayList<>();
//		for (VariantLineInstructorController controller : variantControllers) {
//			prevAnswers.add(controller.getText());
//		}
		variantControllers.clear();
		
		
		//updating variants
		variantsVBox.getChildren().clear();
		int numVariants;
		try {
			numVariants = Integer.parseInt(
					numberOfVariantsBox.getSelectionModel().getSelectedItem());		
			
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			throw new RuntimeException("Error when trying to detect new number of variants", nfe);
		}
		//System.out.println("numVariants: " + numVariants);
		
		for (int i = 0; i < numVariants; ++i) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/VariantLineInstructor.fxml"));
			AnchorPane ap;
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			VariantLineInstructorController vlic = loader.getController();
			//System.out.println("vlic: " + vlic);
			String varText = "";
			if (i < prevAnswers.size()) {
				varText = prevAnswers.get(i);
			}
			vlic.initController(i + 1, varText);
			variantsVBox.getChildren().add(ap);
			variantControllers.add(vlic);
		}
		updateCorrectVariantBox(numVariants);
	}
	
	public void initController(Tab tab, EditTestViewController etvc, Test t, Question q, Mode mode) {
		variantControllers = new ArrayList<>();
		currentTab = tab;
		editTestViewController = etvc;
		previousView = (AnchorPane)tab.getContent();
		this.mode = mode;
		setQuestion(q);
		currentTest = t;
		lastUploadedImageName = null;
		
	}
	
	private void setQuestion(Question q) {
		this.question = q;
		idLabel.setText("" + q.getId());
		questionStrArea.setText(q.getQuestionStr());
//		v1Area.setText(q.getVariants().get(0).getText());
//		v2Area.setText(q.getVariants().get(1).getText());
//		v3Area.setText(q.getVariants().get(2).getText());
		
		
		
//		correctVariantBox.setItems(FXCollections.observableArrayList("1", "2", "3"));
//		int correctId = q.getCorrectVariantNumber();
//		List<Variant> vs = q.getVariants();
//		for (int i = 0; i < vs.size(); ++i) {
//			if (vs.get(i).getId() == correctId) {
//				correctVariantBox.getSelectionModel().select(i);
//			}
//		}
		//System.out.println("Vars amount: " + q.getVariants().size());
		
		initNumberOfVariantsBox();
		updateCorrectVariantBox(q.getVariants().size());
		initVariants();
		
		
//		correctVariantBox.getSelectionModel().selectFirst();
		initImagePart();
		
	}
	
	private void initNumberOfVariantsBox() {
		List<String> varAmounts = new ArrayList<>();
		for (int i = MIN_NUMBER_OF_VARIANTS; i <= MAX_NUMBER_OF_VARIANTS; ++i) {
			varAmounts.add("" + i);
		}
		numberOfVariantsBox.setItems(FXCollections.observableArrayList(varAmounts));
		
		int size = question.getVariants().size();
		numberOfVariantsBox.getSelectionModel().select("" + size);
		
		numberOfVariantsBox.getSelectionModel().selectedIndexProperty()
			.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					numberOfVariantsBox.getSelectionModel().select(newValue.intValue());
					updateVariants();
					
				}
		});
	}
	
	private void initVariants() {
		List<Variant> vs = question.getVariants();
		List<String> varTexts = new ArrayList<>();
		for (Variant v : vs) {
			varTexts.add(v.getText());
		}
		updateVariants(varTexts);
		
	}
	
	private void updateCorrectVariantBox(int varsAmount) {
		List<String> vars = new ArrayList<>();
		for (int i = 0; i < varsAmount; ++i) {
			vars.add("" + (i+1));
		}
		correctVariantBox.setItems(FXCollections.observableArrayList(vars));
		
		//works if correct variant is already set
		int correctId = question.getCorrectVariantNumber();
		List<Variant> vs = question.getVariants();
		for (int i = 0; i < vs.size(); ++i) {
			if (vs.get(i).getId() == correctId) {
				correctVariantBox.getSelectionModel().select(i);
			}
		}
	}
	
	private void initImagePart() {
//		System.out.println("Init: " + question.getImageName());
		if (!Helper.isEmpty(question.getImageName())) {
			try {
				ImagesManager.getInstance().getImageByName(question.getImageName());
			} catch (ImageNotFoundException e) {
				e.printStackTrace();
				updateImagePart(null);
			} catch (ImagePackingException e) {
				throw new RuntimeException(e);
			}
			updateImagePart(question.getImageName());
		} else {
			updateImagePart(null);
		}
	}
	
	private void updateImagePart(String imageUrl) {
		if (isEmpty(imageUrl)) {
			imageView.setImage(null);
			imageView.setFitWidth(600);
			imageView.setFitHeight(0);
			updateImageButtons(false);
			
		} else {
			
			updateImageView(imageUrl);			
			updateImageButtons(true);
		}
	}
	
	private void updateImageButtons(boolean isImageSet) {
		imageButtonsBox.getChildren().clear();
		if (isImageSet) {
			imageButtonsBox.getChildren().add(updateImageButton);
			imageButtonsBox.getChildren().add(deleteImageButton);					
		} else {
			imageButtonsBox.getChildren().add(uploadImageButton);	
		}
	}
	
	private boolean dataChanged() {
		if (!areEqual(question.getQuestionStr(), questionStrArea.getText())) {
			return true;
		}
		
		if (variantControllers.size() != question.getVariants().size()) {
			return true;
		}
		
		List<Variant> vs = question.getVariants();
		for (int i = 0; i < vs.size(); ++i) {
			if (!areEqual(vs.get(i).getText(), variantControllers.get(i).getText())) {
				return true;
			}
		}
		
//		if (!areEqual(vs.get(0).getText(), v1Area.getText())) {
//			return true;
//		}
//		if (!AREEQUAL(VS.GET(1).GETTEXT(), V2AREA.GETTEXT())) {
//			RETURN TRUE;
//		}
//		IF (!AREEQUAL(VS.GET(2).GETTEXT(), V3AREA.GETTEXT())) {
//			RETURN TRUE;
//		}
		
		int vIndex = Integer.parseInt(correctVariantBox.getSelectionModel().getSelectedItem());
		if (question.getCorrectVariantNumber() != vs.get(vIndex - 1).getId()) {
			return true;
		}
		
		if (deleteQuestionImage) {
			return true;
		}
		
		if(!isEmpty(lastUploadedImageName)) {
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
		if (Helper.isEmpty(questionStrArea.getText())) {
			errors += QUESTION_STR_EMPTY_ERROR + "\n";
		}
		
		for (int i = 0; i < variantControllers.size(); ++i) {
			if (Helper.isEmpty(variantControllers.get(i).getText())) {
				errors += String.format(VARIANT_STR_EMPTY_ERROR, i + 1) + "\n";
			}
		}
		
//		if (Helper.isEmpty(v1Area.getText())) {
//			errors += V1_STR_EMPTY_ERROR + "\n";
//		}
//		if (Helper.isEmpty(v2Area.getText())) {
//			errors += V2_STR_EMPTY_ERROR + "\n";
//		}
//		if (Helper.isEmpty(v3Area.getText())) {
//			errors += V3_STR_EMPTY_ERROR + "\n";
//		}
		return errors;
	}
	
	private void saveQuestion() {
		question.setQuestionStr(questionStrArea.getText());
		List<Variant> variants = new ArrayList<Variant>(3);
		question.setNumberOfVariants(variantControllers.size());
		for (int i = 0; i < variantControllers.size(); ++i) {
			variants.add(new Variant(i + 1, variantControllers.get(i).getText()));
		}
//		variants.add(new Variant(1, v1Area.getText()));
//		variants.add(new Variant(2, v2Area.getText()));
//		variants.add(new Variant(3, v3Area.getText()));
			
		try {
			question.setVariants(variants);
		} catch (QuestionException e) {
			throw new RuntimeException(e);
		}
		
		String selectedVariant = correctVariantBox.getSelectionModel().getSelectedItem();
		int vIndex = Integer.parseInt(selectedVariant);
		if (vIndex > 0 && vIndex < (variantControllers.size() + 1) ) {
			try {
				question.setCorrectVariantNumber(
						question.getVariants().get(vIndex - 1).getId()
						);
			} catch (QuestionException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Strange index. Should be between 1 and 3.");
		}
		
		
		if (!Helper.isEmpty(lastUploadedImageName) 
				&& !lastUploadedImageName.equals(question.getImageName())) {
			if (!Helper.isEmpty(question.getImageName())) {
				deleteImageByName(question.getImageName());
			}
			question.setImageName(lastUploadedImageName);
			//System.out.println("From saving 1 case");
		} else if (deleteQuestionImage) {
			deleteImageByName(question.getImageName());
			question.setImageName(null);
			//System.out.println("From saving 2 case - deleting");
		}
		
		try {
			switch (mode) {
			case CREATE:
				currentTest.addQuestion(question);
				break;
			case EDIT:
				currentTest.editQuestion(question.getId(), question);
				break;
			}
			TestsManager.getInstance().saveTests();
		} catch (QuestionException | PersistenceException | TestCorruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void deleteImageByName(String name) {
		try {
			ImagesManager.getInstance().deleteImageByName(name);
		} catch (ImageNotFoundException e) {
			e.printStackTrace();
		} catch (ImagePackingException e) {
			throw new RuntimeException(e);
		}
	}
}
