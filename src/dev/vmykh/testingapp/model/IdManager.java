package dev.vmykh.testingapp.model;

import java.io.Serializable;

public class IdManager implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2703976370755980378L;
	
	
	private int nextTestId = 1;
	
	public int getNextTestId() {
		return nextTestId++;
	}
	
	public IdManager(int startId) {
		nextTestId = startId;
	}
}
