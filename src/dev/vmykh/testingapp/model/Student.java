package dev.vmykh.testingapp.model;

import java.io.Serializable;

public class Student implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5875294090126342144L;
	
	private String name;
	private String surname;
	private String group;
	
	
	
	public Student() {
		
	}
	
	public Student(String name, String surname, String group) {
		super();
		this.name = name;
		this.surname = surname;
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "Student: " + name + " " + surname + " group: " + group;
	}
	

}
