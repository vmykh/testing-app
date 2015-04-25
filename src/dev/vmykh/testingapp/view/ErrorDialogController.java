package dev.vmykh.testingapp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorDialogController {
	
	@FXML
	private Label errorMsg;
	
	@FXML
	private Button okButton;
	
	private Stage dialogStage;
	
	
	@FXML
	private void initialize() {
//		confirmMsg.setText("Bingo ringo bingo ringo Ney Work Los Angeles San Fierro Groovy limboro");
		errorMsg.setWrapText(true);
	}
	
	@FXML
	private void handleOk() {
		dialogStage.close();
	}
	
	public void setMessage(String msg) {
		errorMsg.setText(msg);
	}
	
	public void setOkButtonText(String t) {
		okButton.setText(t);
	}
	
	public void setStage(Stage stage) {
		dialogStage = stage;
	}
	
	
}
