package dev.vmykh.testingapp.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import dev.vmykh.testingapp.model.exceptions.ImageEditingException;
import dev.vmykh.testingapp.model.exceptions.ImageFormatNotSupportedException;
import dev.vmykh.testingapp.model.exceptions.ImageNotFoundException;
import dev.vmykh.testingapp.model.exceptions.ImagePackingException;

public class ImagesManager {
	private static final String OUT_IMG_FORMAT = "jpg";
	private static final int QUESTION_IMAGE_WIDTH = 600;
	
	private static ImagesManager instance;
	
	private List<String> availableFormats;
	
	private ImagesManager() {
		availableFormats = Arrays.asList(new String[]{"jpeg", "jpg", "png", "gif"});
	}
	
	public static ImagesManager getInstance(){
		if (instance == null) {
			instance = new ImagesManager();
		}
		return instance;
	}
	
	/**
	 * methods takes url of source image,
	 * adjusts it size and resolution
	 * and stores add its to file (probably zip) with images
	 * @return name of the image in file container (probably zip file)
	 * @throws ImagePackingException 
	 * 
	 */
	public String addImage(String srcImgFileName) 
			throws FileNotFoundException, ImageFormatNotSupportedException,
			ImageEditingException, ImagePackingException
	{
		
		File srcFile = new File(srcImgFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            throw new FileNotFoundException("Source file \"" + srcImgFileName + "\" not found");
        }
        String format = getFileExtension(srcImgFileName).toLowerCase();
        if (!checkFormat(format)) {
            throw new ImageFormatNotSupportedException("Format \"" + format + "\" is not supported");
        }
        BufferedImage srcImg = null;        
        try {
            srcImg = ImageIO.read(srcFile);
            BufferedImage jpgImg = new BufferedImage(
            		srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_RGB);
            jpgImg.createGraphics().drawImage(srcImg, 0, 0, Color.WHITE, null);
            srcImg = jpgImg;
        } catch (IOException ex) {
            throw new ImageEditingException("Error while reading file \"" + srcImgFileName + "\"", ex);
        }
        
        
        BufferedImage resizedImg = resizeImage(srcImg); 
        String outImageName = createOutputImageName(OUT_IMG_FORMAT);
        String outImageFullName = ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEMP_DIR + File.separator 
				+ outImageName;
        File outImageTempFile = new File(outImageFullName);       
        try {
            ImageIO.write(resizedImg, OUT_IMG_FORMAT, 
            		outImageTempFile);
        } catch (IOException ex) {
            throw new ImageEditingException("Error while writing file \"" + outImageName + "\"", ex);
        }
        
        PersistenceManager.getInstance().saveImage(outImageTempFile);
        
        outImageTempFile.delete();
		return outImageName;
	}
	
	private BufferedImage resizeImage(BufferedImage srcImg) {
		BufferedImage resized = Scalr.resize(srcImg, 
				Scalr.Mode.FIT_TO_WIDTH, QUESTION_IMAGE_WIDTH, Scalr.OP_ANTIALIAS);
		return resized;
	}
	
	private boolean checkFormat(String format) {
        return availableFormats.contains(format.toLowerCase());
    }
	
	private String getFileExtension(String fileName) throws ImageFormatNotSupportedException {
        String extension = null;
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i + 1);
        } else {
            throw new ImageFormatNotSupportedException("Can not identify file extension. File name:" + fileName);
        }        
        return extension;
    }
	
    private String createOutputImageName(String format) {
        String name = "img";
        long currentTime = new Date().getTime();
        int random = Math.abs(new Random(currentTime).nextInt()) % 10000;
        name += "_" + currentTime;
        name += "_" + random;
        name += "." + format;
        return name;
    }

	public String getImageByName(String imageName) throws ImageNotFoundException, ImagePackingException {
		PersistenceManager.getInstance().loadImage(imageName);
		return imageName;
	}

	public void deleteImageByName(String imageName) throws ImageNotFoundException, ImagePackingException {
		PersistenceManager.getInstance().deleteImage(imageName);
	}
}
