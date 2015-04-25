package dev.vmykh.testingapp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class VariantLineInstructorController {
	private static final String LABEL_START = "Вариант ";
	
	@FXML
	private Label variantLabel;
	
	@FXML
	private TextArea variantText;
	
	@FXML
	private void initialize() {
		
	}
	
	
	public void initController(int index, String text) {
		variantLabel.setText(LABEL_START + index + ":");
		variantText.setText(text);
	}
	
	public String getText() {
		return variantText.getText();
	}
}
