package dev.vmykh.testingapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Helper {
	private static Random rand = new Random(new Date().getTime());  //TODO  change to new Date()
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	public static int getRandomInt(int min, int max) {
		if (max < min){
			throw new RuntimeException("Exception in Helper.getRandomInt(min, max)"
					+ "max can't be less than min");
		}
		if (max == min){
			return max;
		}
		return min + Math.abs(rand.nextInt()) % (max - min + 1);
	}
	
	public static SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	public static SimpleDateFormat getTimeFormat() {
		return timeFormat;
	}
	
	public static boolean isEmpty(String str) {
		if (str == null){
			return true;
		} else if (str.trim().equals("")) {
			return true;
		}
		return false;
//		return str != null && !str.trim().equals("");
	}
	
	public static boolean areEqual(String str1, String str2) {
		boolean res = false;
		if (str1 != null) {
			if (str1.equals(str2)) {
				res = true;
			} else {
				res = false;
			}
		} else if (str2 == null) {
			res = true;
		}
		return res;
	}
	
	public static Random getRandom() {
		return rand;
	}
}
