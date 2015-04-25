package dev.vmykh.testingapp.view;

import java.util.regex.Matcher;

import dev.vmykh.testingapp.model.Helper;
import dev.vmykh.testingapp.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static dev.vmykh.testingapp.model.Helper.isEmpty;

public class NameSurnameDialogController {
//	private static final String LOGIN = "mrjones";
//	private static final String PASSWORD = "bingo";
	private static final String ERROR_MSG = "Ошибка. Данные не корректны";
	private static final String PASSWORD_ERROR_MSG = "Ошибка. Пароль неверен";
	
	private boolean authenticated = false;
	private String correctPassword = "";
		
	@FXML
	private TextField nameField;
	
	@FXML
	private TextField surnameField;
	
	@FXML
	private TextField groupField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Label errorLabel;
	
	private Stage dialogStage;
	
	
	@FXML
	private void initialize(){
		nameField.setText("");
		surnameField.setText("");
		errorLabel.setText("");
	}
	
	@FXML
	private void handleCancel() {
		errorLabel.setText("");
		dialogStage.close();
		authenticated = false;
	}
	
	@FXML 
	void handleStart() {
		if (checkInput()) {
			if (checkPassword()) {
				authenticated = true;
				dialogStage.close();
			} else {
				authenticated = false;
				errorLabel.setText(PASSWORD_ERROR_MSG);
			}
		} else {
			authenticated = false;
			errorLabel.setText(ERROR_MSG);
		}
	}
	
	private boolean checkInput() {
		String inputName = nameField.getText();
		String inputSurname = surnameField.getText();
		return isValid(inputName) && isValid(inputSurname) && !Helper.isEmpty(groupField.getText());
	}
	
	private boolean checkPassword() {
		//System.out.println(correctPassword.trim() + " ! " + passwordField.getText().trim());
		if (Helper.isEmpty(correctPassword)) {
			return true;
		}
		return Helper.areEqual(correctPassword, passwordField.getText());
	}
	
	private boolean isValid(String str) {
		return !isEmpty(str) && str.matches("[A-Яа-яA-Za-zІіЇї' -]+");
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public Student getStudent() {
		if (isAuthenticated()) {
			//System.out.println("From nsdc - group: " + groupField.getText());
			return new Student(nameField.getText(), surnameField.getText(), groupField.getText());
		}
		return null;
	}
	
	public void setDialogStage(Stage stage) {
		dialogStage = stage;
	}
	
	public void setPassword(String password) {
		correctPassword = password;
	}
}
