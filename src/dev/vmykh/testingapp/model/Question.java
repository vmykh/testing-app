package dev.vmykh.testingapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dev.vmykh.testingapp.model.exceptions.QuestionException;

public class Question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9124417135874726374L;
	
	
	private int id;
	private String questionStr;
	private List<Variant> variants;
	private String imageName;
	private int correctVariantNumber = -1;
	private int numberOfVariants;
	
	public Question(){
		
	}
	
	public Question(Question another){
		this.id = another.id;
		this.questionStr = another.questionStr;
		this.imageName = another.imageName;
		this.correctVariantNumber = another.correctVariantNumber;
		this.numberOfVariants = another.numberOfVariants;
		this.variants = new ArrayList<Variant>();
		for (Variant v : another.variants) {
			this.variants.add(new Variant(v));
		}
	}
	
	public boolean isValid() {
		if (questionStr == null) {
			return false;
		}
		if (numberOfVariants != variants.size()) {
			return false;
		}
		try {
			setCorrectVariantNumber(this.correctVariantNumber);
			// this method checks whether variant number is valid
			// so you can't set invalid variant number without exception
			// but you can set valid number and the change variants list
			// and then variant number will become invalid.
			// To check this (whether variants were changed we try to set old variant number again
		} catch(QuestionException qe) {
			return false;
		}
		return true;
	}
	
	public List<Variant> getVariantsInRandomOrder() {
		//TODO    make it returns really in random order      :)
		List<Variant> res = getVariants();
		Collections.shuffle(res, Helper.getRandom());
		return res;
	}
	
	public List<Variant> getVariants() {
		List<Variant> res = new ArrayList<Variant>(variants.size());
		for (Variant v : variants) {
			res.add(new Variant(v));
		}
		return res;
	}

	public int getId() {
		return id;
	}

	/*
	 * Question id shoud be managed only by Test class
	 */
	public void setId(int id) {
		this.id = id;
	}

	public String getQuestionStr() {
		return questionStr;
	}

	public void setQuestionStr(String questionStr) {
		this.questionStr = questionStr;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getCorrectVariantNumber() {
		return correctVariantNumber;
	}

	public void setCorrectVariantNumber(int correctVariantNumber) throws QuestionException {
		boolean idExists = false;
		if (variants == null) {
			throw new QuestionException("Can't set correct id while variants == null");
		}
		for (Variant v : variants){
			if (v.getId() == correctVariantNumber){
				idExists = true;
				break;
			}
		}
		if (!idExists){
			throw new QuestionException("Invalid correctVariantNumber");
		}
		this.correctVariantNumber = correctVariantNumber;
	}

	public void setVariants(List<Variant> variants) throws QuestionException {
		if (variants == null){
			throw new QuestionException("Attempt to set empty (null) variants");
		}
		if (numberOfVariants == -1) {
			throw new QuestionException("Before adding variants, it's mandatory"
					+ "to set numberOfVariants");
		}
		if (variants.size() != numberOfVariants){
			throw new QuestionException("Number of Variants and actual amount of variants are not equal");
		}
		for (int i = 0; i < variants.size(); ++i) {
			for (int j = 0; j < variants.size(); ++j) {
				if (i != j) {
					if (variants.get(i).getId() == variants.get(j).getId()){
						throw new QuestionException("Two variants have same id");
					}
				}
			}
		}
		this.variants = new ArrayList<>(variants.size());
		for (Variant v : variants) {
			this.variants.add(v);
		}
	}

	public int getNumberOfVariants() {
		return numberOfVariants;
	}

	public void setNumberOfVariants(int numberOfVariants) {
		this.numberOfVariants = numberOfVariants;
	}
	
	@Override
	public String toString() {
		String str = "Question with id=" + id;
		str += "\nTitle: " + questionStr;
		str += "\nImageName: " + imageName;
		str += "\nNumberOfVariants: " + numberOfVariants;
		str += "\nCorrectVariantNumber: " + correctVariantNumber;
		str += "\n Variants: [";
		for (int i = 0; i < variants.size() - 1; ++i) {
			str += "\n" + variants.get(i) + ",";
		}
		if (variants.size() > 0) {
			str += "\n" + variants.get(variants.size() - 1);
		}
		str += "\n]";
		return str;
		
	}
}
