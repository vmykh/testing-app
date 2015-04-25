package dev.vmykh.testingapp.model;

import dev.vmykh.testingapp.model.exceptions.AnsweredQuestionException;

public class AnsweredQuestion extends Question {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6358700283609531670L;
	
	private int chosenVariantId;   // "-1" means that variant is not chosen
	
	public AnsweredQuestion(Question q) {
		super(q);
		chosenVariantId = -1;
	}
	
	public AnsweredQuestion(AnsweredQuestion aq) {
		super(aq);
		this.chosenVariantId = aq.chosenVariantId;		
	}
	

	public int getChosenVariantId() {
		return chosenVariantId;
	}

	public void setChosenVariantId(int chosenVariantId) throws AnsweredQuestionException {
		if (chosenVariantId == -1) {
			this.chosenVariantId = chosenVariantId;
			return;
		}
		
		boolean variantIdExists = false;
		for (Variant v : getVariants()) {
			if (v.getId() == chosenVariantId) {
				variantIdExists = true;
				break;
			}
		}
		if (!variantIdExists) {
			throw new AnsweredQuestionException("Attemt to choose variant"
					+ " that doesn't exists");
		}
		this.chosenVariantId = chosenVariantId;
	}
	
	@Override
	public String toString() {
		String str = "Answered Question: --[  " + super.toString();
		str += "  ]--\nChosenVariantId: " + chosenVariantId;
		return str;
	}
}
