package dev.vmykh.testingapp.model;

import java.io.Serializable;

public class Variant implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1074360198717105896L;
	
	private int id;
	private String text;
	
	
	
	public Variant(int id, String text) {
		super();
		setId(id);
		this.text = text;
	}
	
	public Variant(Variant another) {
		this.id = another.id;
		this.text = another.text;
	}
	
	public Variant() {
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {     //id must be positive
		if (id < 0) {
			id = Math.abs(id);
		}
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "Variant: id=" + id + " text = " + text;
	}
}
