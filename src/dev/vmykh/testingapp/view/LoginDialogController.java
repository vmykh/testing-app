package dev.vmykh.testingapp.view;

import org.controlsfx.control.CheckListView;

import dev.vmykh.testingapp.model.PasswordManager;
import dev.vmykh.testingapp.model.exceptions.PasswordCorruptedException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginDialogController {
	private static final String LOGIN = "admin";
	//private static final String PASSWORD = "bingo";
	private static final String ERROR_MSG = "Ошибка. Данные не верны";
	
	private boolean authenticated = false;
		
	@FXML
	private TextField textField;
	
	@FXML
	private PasswordField passField;
	
	@FXML
	private Label errorLabel;
	
	private Stage dialogStage;
	
	
	@FXML
	private void initialize(){
		textField.setText("");
		passField.setText("");
		errorLabel.setText("");
	}
	
	@FXML
	private void handleCancel() {
		errorLabel.setText("");
		dialogStage.close();
	}
	
	@FXML void handleLogin() {
		if (checkLoginAndPassword()) {
			authenticated = true;
			dialogStage.close();
		} else {
			authenticated = false;
			errorLabel.setText(ERROR_MSG);
//			textField.setText("");
//			passField.setText("");
		}
	}
	
	private boolean checkLoginAndPassword() {
		String inputLogin = textField.getText();
		String inputPassword = passField.getText();
		try {
			return inputLogin != null &&  inputLogin.equals(LOGIN)
					&& inputPassword != null && inputPassword.equals(PasswordManager.getInstance().getPassword());
		} catch (PersistenceException | PasswordCorruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setDialogStage(Stage stage) {
		dialogStage = stage;
	}
}
