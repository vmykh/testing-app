package dev.vmykh.testingapp.view;

import dev.vmykh.testingapp.model.PasswordManager;
import dev.vmykh.testingapp.model.exceptions.IncorrectPasswordException;
import dev.vmykh.testingapp.model.exceptions.PasswordCorruptedException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static dev.vmykh.testingapp.model.Helper.isEmpty;
import static dev.vmykh.testingapp.model.Helper.areEqual;

public class ChangePasswordDialogController {
	private static final String OLD_PASSWORD_INCORRECT = "Старый пароль неверен";
	private static final String EMPTY_PASSWORD_ERROR = "Пароль не может быть пустым";
	private static final String PASSWORDS_DIFFER = "Пароли отличаются";
	
//	private boolean authenticated = false;
		
	@FXML
	private PasswordField oldPasswordField;
	
	@FXML
	private PasswordField newPasswordField;
	
	@FXML
	private PasswordField repeatedNewPasswordField;
	
	@FXML
	private Label errorLabel;
	
	private Stage dialogStage;
	
	
	@FXML
	private void initialize(){
		oldPasswordField.setText("");
		newPasswordField.setText("");
		repeatedNewPasswordField.setText("");
		errorLabel.setText("");
	}
	
	@FXML
	private void handleCancel() {
		errorLabel.setText("");
		dialogStage.close();
	}
	
	@FXML 
	private void handleChangePassword() {
		String oldpass = oldPasswordField.getText();
		String newpass = newPasswordField.getText();
		String newpass2 = repeatedNewPasswordField.getText();
		try {
			if (isEmpty(oldpass) || !oldpass.equals(PasswordManager.getInstance().getPassword())) {
				errorLabel.setText(OLD_PASSWORD_INCORRECT);
				return;
			}
		} catch (PersistenceException | PasswordCorruptedException e) {
			throw new RuntimeException(e);
		}
		if (!isEmpty(newpass) && !newpass.equals(newpass2)) {
			errorLabel.setText(PASSWORDS_DIFFER);
			return;
		}
		if (isEmpty(newpass)) {
			errorLabel.setText(EMPTY_PASSWORD_ERROR);
			return;
		}
		try {
			PasswordManager.getInstance().setPassword(newpass);
			dialogStage.close();
		} catch (IncorrectPasswordException | PersistenceException
				| PasswordCorruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setDialogStage(Stage stage) {
		dialogStage = stage;
	}
}
