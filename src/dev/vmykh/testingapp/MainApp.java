package dev.vmykh.testingapp;

import java.io.IOException;

import javax.management.RuntimeErrorException;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import dev.vmykh.testingapp.view.EntryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initEntry();
	}
	
	public void initEntry(){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/Entry.fxml"));
		AnchorPane ap;
		try {
			ap = (AnchorPane)loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		EntryController ec = loader.getController();
		ec.setStage(primaryStage);
		Scene scene = new Scene(ap);
		primaryStage.setScene(scene);
		//primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
