package dev.vmykh.testingapp.view;

import java.io.IOException;

import dev.vmykh.testingapp.MainApp;
import dev.vmykh.testingapp.model.Student;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialogs {
	private static final String CONFIRM_DIALOG_OK_BUTTON_TEXT = "Да";
	private static final String CANCEL_BUTTON_TEXT = "Отмена";
	private static final String ERROR_DIALOG_OK_BUTTON_TEXT = "ОК";
	private static final String DATA_INPUT_MSG = "Ввод данных";
	

	/**
	 * 
	 * returns true if user clicked "Ok" button, and false when "Cancel" button
	 */
	public static boolean showConfirmationDialog(String title, String message) {
		return showConfirmDialog(title, message, CONFIRM_DIALOG_OK_BUTTON_TEXT, CANCEL_BUTTON_TEXT);
	}
	
	/**
	 * 
	 * returns true if user clicked "Ok" button, and false when "Cancel" button
	 * @param title
	 * @param message
	 * @param okButtonText
	 * @param cancelButtonText
	 * @return
	 */
	public static boolean showConfirmDialog(String title, String message, 
			String okButtonText, String cancelButtonText) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/ConfirmDialog.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ConfirmDialogController cdc = loader.getController();
		cdc.setMessage(message);
		cdc.setOkButtonText(okButtonText);
		cdc.setCancelButtonText(cancelButtonText);
		cdc.setStage(stage);
		
		
		Scene scene = new Scene(ap);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		stage.showAndWait();
		
		return cdc.isOkClicked();
	}
	
	public static void showErrorDialog(String title, String message) {
		showErrorDialog(title, message, ERROR_DIALOG_OK_BUTTON_TEXT);
	}
	
	public static void showErrorDialog(String title, String message, 
			String okButtonText) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/ErrorDialog.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ErrorDialogController edc = loader.getController();
		edc.setMessage(message);
		edc.setOkButtonText(okButtonText);
		edc.setStage(stage);
		
		
		Scene scene = new Scene(ap);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		stage.showAndWait();
	}
	
	public static Student showNameSurnameDialog(String password) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/NameSurnameDialog.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		NameSurnameDialogController nsdc = loader.getController();
		nsdc.setDialogStage(stage);
		nsdc.setPassword(password);
		
		Scene scene = new Scene(ap);
		stage.setScene(scene);
		stage.setTitle(DATA_INPUT_MSG);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		stage.showAndWait();
		
		return nsdc.getStudent();
	}
	
	public static void showChangePasswordDialog(String title) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/ChangePasswordDialog.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ChangePasswordDialogController cpdc = loader.getController();
		cpdc.setDialogStage(stage);
		
		
		Scene scene = new Scene(ap);
		stage.setScene(scene);
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		stage.showAndWait();
	}
	
	
}
