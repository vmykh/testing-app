package dev.vmykh.testingapp.model;

import java.io.FileNotFoundException;
import java.util.Date;

import dev.vmykh.testingapp.model.exceptions.IncorrectPasswordException;
import dev.vmykh.testingapp.model.exceptions.PasswordCorruptedException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;

public class PasswordManager {
	private static final String DEFAULT_PASSWORD = "bingo"; 
	private static PasswordManager pm;
	private String currentPassword;
	
	private PasswordManager() throws PersistenceException, PasswordCorruptedException {
		try {
			currentPassword = PersistenceManager.getInstance().loadPassword();
		} catch (FileNotFoundException e) {
			System.out.println("Password file not found. Default password was used.");
			e.printStackTrace();
			try {
				setPassword(DEFAULT_PASSWORD);
			} catch (IncorrectPasswordException e1) {
				System.out.println("Strange error. Default password is not correct");
				e1.printStackTrace();
			}
		}
	}
	
	public static PasswordManager getInstance() throws PersistenceException, PasswordCorruptedException {
		if (pm == null) {
			pm = new PasswordManager();
		}
		return pm;
	}
	
	public String getPassword() {
		return currentPassword;
	}
	
	public void setPassword(String pass) throws IncorrectPasswordException, PersistenceException {
		if (Helper.isEmpty(pass)) {
			throw new IncorrectPasswordException();
		}
		currentPassword = pass;
		PersistenceManager.getInstance().savePassword(currentPassword);
	}
	
	private String generatePassword(){
		long time = new Date().getTime();
//		long arg = time / (1000 * 60 * 60 * 24);   //Depends on day
		long arg = time / (1000 * 60);    //Depends on minute
		arg = arg + 271;
		arg = arg % 152387;
		//arg = Math.abs(sin)
		arg = arg * (arg / 2) * (arg / 19);
		arg = arg / 28817;
		arg = arg - 23;
		arg = arg / 17;
		long arg2 = arg % 10000;
		long arg1 = (arg * 35178) % 10000;
		return ("" + arg1) + arg2;
	}
	
	public static void main(String[] args) throws PersistenceException, PasswordCorruptedException, IncorrectPasswordException {
		PasswordManager psm = PasswordManager.getInstance();
		System.out.println("1: " + psm.getPassword());
//		System.out.println(psm.generatePassword());
//		psm.setPassword("New York");
		
	}
}
