package dev.vmykh.testingapp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmDialogController {
	
	@FXML
	private Label confirmMsg;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Button cancelButton;
	
	private Stage dialogStage;
	private boolean okClicked = false;
	
	
	@FXML
	private void initialize() {
//		confirmMsg.setText("Bingo ringo bingo ringo Ney Work Los Angeles San Fierro Groovy limboro");
		confirmMsg.setWrapText(true);
	}
	
	@FXML
	private void handleOk() {
		okClicked = true;
		dialogStage.close();
	}
	
	@FXML
	private void handleCancel() {
		okClicked = false;
		dialogStage.close();
	}
	
	public void setMessage(String msg) {
		confirmMsg.setText(msg);
	}
	
	public void setOkButtonText(String t) {
		okButton.setText(t);
	}
	
	public void setCancelButtonText(String t) {
		cancelButton.setText(t);
	}
	
	public boolean isOkClicked(){
		return okClicked;
	}
	
	public void setStage(Stage stage) {
		dialogStage = stage;
	}
	
	
}
