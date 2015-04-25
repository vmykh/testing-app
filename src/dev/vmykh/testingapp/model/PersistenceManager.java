package dev.vmykh.testingapp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

import dev.vmykh.testingapp.model.exceptions.CryptoException;
import dev.vmykh.testingapp.model.exceptions.IdManagerNotFoundException;
import dev.vmykh.testingapp.model.exceptions.ImageEditingException;
import dev.vmykh.testingapp.model.exceptions.ImageFormatNotSupportedException;
import dev.vmykh.testingapp.model.exceptions.ImageNotFoundException;
import dev.vmykh.testingapp.model.exceptions.ImagePackingException;
import dev.vmykh.testingapp.model.exceptions.PasswordCorruptedException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;


public class PersistenceManager {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final String CRYPTO_KEY = "FOOTBALLFOOTBALL";
	
	private File tempDir;
	private File zipFile;
	private File tempZipFile;
	
	
	private static PersistenceManager instance;
	public static PersistenceManager getInstance() {
		if (instance == null) {
			instance = new PersistenceManager();
		}
		return instance;
	}
	
	private PersistenceManager() {
		File tempFolder = new File(ResourceBundle.RESOURCES_DIR 
				+ File.separator +".temp");
//		System.out.println("From constructor !!!");
		if(!tempFolder.exists()){
			tempFolder.mkdir();
//			System.out.println("Hello mr Jones!!!!!");
		}
		
		tempDir = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEMP_DIR);
		initZipFiles();
	}
	
	private void initZipFiles() {
		zipFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.IMAGE_CONTAINER_FILE_NAME);
		tempZipFile = new File(ResourceBundle.RESOURCES_DIR + File.separator +
				ResourceBundle.IMAGE_CONTAINER_TEMP_FILE_NAME);
	}

	public void saveTests(List<Test> tests) throws PersistenceException {
		File testsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TESTS_FILE_NAME);
		File testsFileTemp = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TESTS_FILE_TEMP_NAME);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;		
		try  {
			testsFile.createNewFile();
			fos = new FileOutputStream(testsFileTemp);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(tests);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			throw new PersistenceException("Exception in method saveTests()", e);
		}
		
		try {
			encryptFile(CRYPTO_KEY, testsFileTemp, testsFile);
		} catch (CryptoException e) {
			throw new PersistenceException("Exception while encrypting tests file", e);
		}
		
		testsFileTemp.delete();		
		
	}

	public void saveTestSessions(List<TestSession> testSessions) throws PersistenceException {
		File sessionsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEST_SESSIONS_FILE_NAME);
		File sessionsFileTemp = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEST_SESSIONS_FILE_TEMP_NAME);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;		
		try  {
			sessionsFile.createNewFile();
			fos = new FileOutputStream(sessionsFileTemp);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(testSessions);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			throw new PersistenceException("Exception in method saveTests()", e);
		}
		
		try {
			encryptFile(CRYPTO_KEY, sessionsFileTemp, sessionsFile);
		} catch (CryptoException e) {
			throw new PersistenceException("Exception while encrypting tests file", e);
		}
		
		sessionsFileTemp.delete();
	}

	public List<Test> loadTests() throws PersistenceException {
		File tempTestsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TESTS_FILE_TEMP_NAME);
		File testsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TESTS_FILE_NAME);
		if (!testsFile.exists() || testsFile.length() == 0) {
			return new ArrayList<Test>();
		}
		
		try {
			decryptFile(CRYPTO_KEY, testsFile, tempTestsFile);
		} catch (CryptoException ce) {
			throw new PersistenceException("Exception while dencrypting tests file", ce);
		}
		
		List<Test> tests = null;
		FileInputStream fin = null;
		ObjectInputStream oin = null;		
		
		try {
			
			fin = new FileInputStream(tempTestsFile);
			oin = new ObjectInputStream(fin);
			tests = (List<Test>)oin.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new PersistenceException("Exception in method loadTests()", e);
		}
		tempTestsFile.delete();
		return tests;
	}

	public List<TestSession> loadTestsSessions() throws PersistenceException {
		File sessionsTempFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEST_SESSIONS_FILE_TEMP_NAME);
		File sessionsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEST_SESSIONS_FILE_NAME);
		if (!sessionsFile.exists() || sessionsFile.length() == 0) {
			return new ArrayList<TestSession>();
		}
		
		try {
			decryptFile(CRYPTO_KEY, sessionsFile, sessionsTempFile);
		} catch (CryptoException ce) {
			throw new PersistenceException("Exception while dencrypting sessions file", ce);
		}
		
		List<TestSession> sessions = null;
		FileInputStream fin = null;
		ObjectInputStream oin = null;		
		
		try {
			
			fin = new FileInputStream(sessionsTempFile);
			oin = new ObjectInputStream(fin);
			sessions = (List<TestSession>)oin.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new PersistenceException("Exception in method loadTestSessions()", e);
		}
		sessionsTempFile.delete();
		return sessions;
	}
	
	public IdManager loadIdManager() throws IdManagerNotFoundException, PersistenceException {
		File idManagerTempFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.ID_MANAGER_TEMP_FILE_NAME);
		File idManagerFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.ID_MANAGER_FILE_NAME);
		if (!idManagerFile.exists() || idManagerFile.length() == 0) {
			throw new IdManagerNotFoundException("IdManager file not found");
		}
		
		try {
			decryptFile(CRYPTO_KEY, idManagerFile, idManagerTempFile);
		} catch (CryptoException ce) {
			throw new PersistenceException("Exception while dencrypting IdManager file", ce);
		}
		
		IdManager idManager = null;
		FileInputStream fin = null;
		ObjectInputStream oin = null;		
		
		try {
			
			fin = new FileInputStream(idManagerTempFile);
			oin = new ObjectInputStream(fin);
			idManager = (IdManager)oin.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new PersistenceException("Exception in method loadTestSessions()", e);
		}
		idManagerTempFile.delete();
		if (idManager == null) {
			throw new IdManagerNotFoundException("Id Manager file was found. But idManager == null");
		}
		return idManager;
	}
	
	public void saveIdManager(IdManager idManager) throws PersistenceException {
		File idManagerFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.ID_MANAGER_FILE_NAME);
		
		File idManagerFileTemp = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.ID_MANAGER_TEMP_FILE_NAME);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;		
		try  {
			//idManagerFile.mkdirs();
			new File(ResourceBundle.RESOURCES_DIR + File.separator + ".temp").mkdirs();
			
			
			idManagerFile.createNewFile();
			fos = new FileOutputStream(idManagerFileTemp);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(idManager);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			throw new PersistenceException("Exception in method saveIdManager()", e);
		}
		
		try {
			encryptFile(CRYPTO_KEY, idManagerFileTemp, idManagerFile);
		} catch (CryptoException e) {
			throw new PersistenceException("Exception while encrypting idManager file", e);
		}
		
		idManagerFileTemp.delete();
	}

	/*
	 * method takes sourceFileName and creates image with name outImageName in zip archive
	 * 
	 */
	public void saveImage(File imgFile) throws ImagePackingException {
		if (imgFile == null) {
			throw new ImagePackingException("imgFile == null in saveImage method", 
					new NullPointerException());
		}
//		initZipFiles();
		initFilesForZipping();		

		byte[] buf = new byte[1024];

		try {
			FileInputStream fis = new FileInputStream(tempZipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String name = ze.getName();
				//if there is already file in zip archive 
				//with the same name as imgFile - skip it
				if (!name.equals(imgFile.getName())) {  
					zos.putNextEntry(ze);
					int len = 0;
					while ( (len = zis.read(buf)) > 0) {
						zos.write(buf, 0, len);
					}
				}
				ze = zis.getNextEntry();
			}

			zis.close();

			fis = new FileInputStream(imgFile);
			ze = new ZipEntry(imgFile.getName());
			zos.putNextEntry(ze);

			int len = 0;
			while ((len = fis.read(buf)) > 0) {
				zos.write(buf, 0, len);
			}
			zos.closeEntry();
			zos.close();
			fis.close();
			tempZipFile.delete();
		} catch (IOException ex) {
			throw new ImagePackingException("Error while updating zip archive.", ex);
		}
	}
	
	/**
	 * Finds image in package (probably zip archive) and copies it to .temp directory
	 * @param imageName name of image in package (probably zip archive)	 * 
	 * @throws ImageNotFoundException if there is no image with name "srcImage"
	 * @throws ImagePackingException if some other problems emerges
	 */
	public void loadImage(String imageName) throws ImagePackingException, ImageNotFoundException {
		//If imageName == null or zip file doesn't exist method throw exception
		checkPosibilityToFindImage(imageName);
		
		boolean imageFound = false;
		try {
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(fis);

			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String name = ze.getName();
//				System.out.println("\'" + name + "\'");
				if (name.equals(imageName)) {
//					System.out.println("From if");
					File imgFile = new File(tempDir, imageName);
					FileOutputStream fos = new FileOutputStream(imgFile);
					byte[] buf = new byte[1024];
					
					int len = 0;
					while ((len = zis.read(buf)) > 0) {
						fos.write(buf, 0, len);
					}
					fos.close();
					imageFound = true;
					break;
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
		} catch (IOException ex) {
			throw new ImagePackingException("Error while "
					+ "looking or loading image from zip archive", ex);
		}
		
		if (!imageFound) {
			throw new ImageNotFoundException("There is no image with name"
					+ " \"" + imageName + "\" in zip archive");
		}
	}
	
	public void deleteImage(String imageName) throws ImageNotFoundException, ImagePackingException {
		//If imageName == null or zip file doesn't exist method throw exception
		checkPosibilityToFindImage(imageName);

		initFilesForZipping();	
		byte[] buf = new byte[1024];
		boolean imageFound = false;

		try {
			FileInputStream fis = new FileInputStream(tempZipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String name = ze.getName();
				//if there is already file in zip archive 
				//with the same name as imageName - skip it
				if (!name.equals(imageName)) {  
					zos.putNextEntry(ze);
					int len = 0;
					while ( (len = zis.read(buf)) > 0) {
						zos.write(buf, 0, len);
					}
				} else {
					imageFound = true;
				}
				ze = zis.getNextEntry();
			}

			zis.close();
			zos.closeEntry();
			zos.close();
			fis.close();
			tempZipFile.delete();
		} catch (IOException ex) {
			throw new ImagePackingException("Error while updating zip archive to delete image.", ex);
		}

		if (!imageFound) {
			throw new ImageNotFoundException("There is no image with name"
					+ " \"" + imageName + "\" in zip archive");
		}
	}
	
	private void checkPosibilityToFindImage(String imageName) throws ImageNotFoundException {
		if (imageName == null) {
			throw new ImageNotFoundException("String imageName == null. "
					+ "Image with empty name doesn't exist.", 
					new NullPointerException());
		}		
		if (!zipFile.exists() || !zipFile.isFile()) {
			throw new ImageNotFoundException("Attemt to find image when"
					+ " zip file doesn't exist");
		}
	}
	
	private void initFilesForZipping() throws ImagePackingException {
		if (!zipFile.exists()) {
			try {
				zipFile.createNewFile();
				System.out.println("before temp deleting");
				tempZipFile.delete();
				System.out.println("before temp creating");
				System.out.println(tempZipFile.getAbsolutePath());
				tempZipFile.createNewFile();
				System.out.println("after temp creating");
			} catch (IOException ioe) {
				throw new ImagePackingException(
						"Zip File doesn't exist case. Error when trying to create"
								+ " emtpy zip file and empty temp file", ioe);
			}				
		} else {
			try {
				tempZipFile = File.createTempFile(zipFile.getName(), null, tempDir);
			} catch (IOException ioe) {
				throw new ImagePackingException(
						"Zip file exist case. "
								+ "Error when trying to create empty temp file.", ioe);
			}

			//delete temporary file. otherwise you can't rename existing zip file to it
			tempZipFile.delete(); 
			boolean renameOk = zipFile.renameTo(tempZipFile);
			if (!renameOk) {
				throw new ImagePackingException("Can't rename zipFile to tempFile");
			}
		}
	}
	

	private void encryptFile(String key, File inputFile, File outputFile) throws CryptoException {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}

	private void decryptFile(String key, File inputFile, File outputFile) throws CryptoException {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
	}
	
	private void doCrypto(int cipherMode, 
			String key, File inputFile, File outputFile ) throws CryptoException {
		try {
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);
			
			FileInputStream fis = new FileInputStream(inputFile);
//			System.out.println("\n\nInput File lenght: " + (int)inputFile.length()+ "\n\n");
			byte[] inputBytes = new byte[(int)inputFile.length()];
			fis.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(outputBytes);
			
			fis.close();
			fos.close();			
		} catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
			throw new CryptoException("Exception while encrypting/decrypting file", ex);
		}
		
	}
	
	public static void main(String [] args) throws IOException, InterruptedException, ImagePackingException, ImageNotFoundException, ImageFormatNotSupportedException, ImageEditingException {
//		TestsManager tm;
//		TestSessionsManager tsm;
//		try {
//			tm = TestsManager.getInstance();
//			tsm = TestSessionsManager.getInstance();
//		} catch (TestCorruptedException | TestSessionCorruptedException e) {
//			throw new RuntimeException(e);
//		} catch (PersistenceException e) {
//			throw new RuntimeException(e);
//		}
//		System.out.println("Tests: \n");
//		System.out.println(tm.getAllTests());
//		System.out.println("\n\n\nTestSessions: \n");
//		System.out.println(tsm.getAllTestSessions());
		
		
		
//		ImagesManager im = ImagesManager.getInstance();
//		try {
//			im.addImage("/home/mrgibbs/Desktop/table1.jpg");
//		} catch (FileNotFoundException | ImageFormatNotSupportedException
//				| ImageEditingException e) {
//			throw new RuntimeException(e);
//		}
		
		
//		System.out.println("Image was saved.");
//		File file = new File("/home/mrgibbs/Desktop/testingApp/bingo.data");
//		System.out.println("file name: " + file.getName());
//		file.createNewFile();
//		File tempFile = File.createTempFile("NewYork.png", null);
//		Thread.sleep(20*1000);
//		System.out.println("Delay finished.");
		
//		File emptyFile = new File("/home/mrgibbs/Desktop/testingApp/empty.data");
//		emptyFile.createNewFile();
////		FileOutputStream fos = new FileOutputStream(emptyFile);
////		ZipOutputStream zos = new ZipOutputStream(fos);
//		FileInputStream fis = new FileInputStream(emptyFile);
//		ZipInputStream zis = new ZipInputStream(fis);
//		ZipEntry ze = zis.getNextEntry();
//		if (ze == null) {
//			System.out.println("All right, guys!");
//		}
		
//		File file = new File("/home/mrgibbs/Desktop/testingApp/miami.data");
//		System.out.println(file.getAbsolutePath());
//		File nFile = new File("/home/mrgibbs/Desktop/testingApp/miami-2.data");
//		file.renameTo(nFile);
//		System.out.println(file.getAbsolutePath());
		
//		ImagesManager.getInstance().addImage("/home/mrgibbs/Desktop/1422241313_Yes.png");
		
	}
	
	public void clearTempDir() {
		deleteDirectory(tempDir);
	}
	
	private boolean deleteDirectory(File directory) {
		if (directory == null) {
			return false;
		}
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	public String loadPassword() throws FileNotFoundException, PersistenceException, PasswordCorruptedException {
		File passwordTempFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.PASSWORD_FILE_TEMP_NAME);
		File passwordFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.PASSWORD_FILE_NAME);
		if (!passwordFile.exists() || passwordFile.length() == 0) {
			throw new FileNotFoundException("File with password not found");
		}
		
		try {
			decryptFile(CRYPTO_KEY, passwordFile, passwordTempFile);
		} catch (CryptoException ce) {
			throw new PersistenceException("Exception while dencrypting IdManager file", ce);
		}
		
		List<String> passList = null;
		FileInputStream fin = null;
		ObjectInputStream oin = null;		
		
		try {
			
			fin = new FileInputStream(passwordTempFile);
			oin = new ObjectInputStream(fin);
			passList = (List<String>)oin.readObject();
			oin.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new PersistenceException("Exception in method loadTestSessions()", e);
		}
		passwordTempFile.delete();
		if (passList.size() != 5 || Helper.isEmpty(passList.get(2))) {
			throw new PasswordCorruptedException();
		}
		return passList.get(2);
	}
	
	public void savePassword(String password) throws PersistenceException {
		File passFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.PASSWORD_FILE_NAME);
		File passFileTemp = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.PASSWORD_FILE_TEMP_NAME);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		List<String> passList = new ArrayList<>();
		passList.add("Lukas");
		passList.add("Silva");
		passList.add(password);
		passList.add("New York");
		passList.add("Seattle");
		try  {
			//passFile.createNewFile();
			fos = new FileOutputStream(passFileTemp);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(passList);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			throw new PersistenceException("Exception in method saveIdManager()", e);
		}
		
		try {
			encryptFile(CRYPTO_KEY, passFileTemp, passFile);
		} catch (CryptoException e) {
			throw new PersistenceException("Exception while encrypting idManager file", e);
		}
		
		
		passFileTemp.delete();
	}
	
}
