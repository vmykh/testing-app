package dev.vmykh.testingapp.view;

import java.io.IOException;

import javax.management.RuntimeErrorException;

import dev.vmykh.testingapp.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EntryController {
	private final static String LOGIN_DIALOG_TITLE = "Войти";
	
	private Stage primaryStage;
	
	@FXML
	private void initialize(){
		
	}
	
	public void setStage(Stage stage){
		primaryStage = stage;
	}
	
	@FXML
	private void enterAsStudent() {
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(MainApp.class.getResource("view/ConfirmDialog.fxml"));
//		AnchorPane ap;
//		try {
//			ap = (AnchorPane)loader.load();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		
//		Stage confirmStage = new Stage();
//		confirmStage.setTitle(LOGIN_DIALOG_TITLE);
//		confirmStage.initModality(Modality.WINDOW_MODAL);
//		confirmStage.initOwner(primaryStage);
//		Scene scene = new Scene(ap);
//		confirmStage.setScene(scene);
//		
//		confirmStage.showAndWait();
		FXMLLoader loader = new FXMLLoader();
		AnchorPane ap;
		Scene scene;
		loader.setLocation(MainApp.class.getResource("view/StudentWindow.fxml"));
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		scene = new Scene(ap);
		
		StudentWindowController swc = loader.getController();
		swc.initController(primaryStage);
		primaryStage.close();
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("Тесты");
		primaryStage.show();
	}
	
	@FXML
	private void enterAsInstructor() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/LoginDialog.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Stage loginStage = new Stage();
		loginStage.setTitle(LOGIN_DIALOG_TITLE);
		loginStage.initModality(Modality.WINDOW_MODAL);
		loginStage.initOwner(primaryStage);
		Scene scene = new Scene(ap);
		loginStage.setScene(scene);
		
		LoginDialogController ldc = loader.getController();
		ldc.setDialogStage(loginStage);
		
		loginStage.showAndWait();
		
		if(ldc.isAuthenticated()) {  
			loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/InstructorWindow.fxml"));
			try {
				ap = (AnchorPane)loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			scene = new Scene(ap);
			
			InstructorWindowController iwc = loader.getController();
//			iwc.setWindowStage(primaryStage);
//			iwc.updateTestLines();
			iwc.initController(primaryStage);
			primaryStage.close();
//			try {
//				Thread.sleep(1000*5);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.setMaximized(true);
			primaryStage.setTitle("Инструменты для инструктора");
			primaryStage.show();
			
		}
	}
}
