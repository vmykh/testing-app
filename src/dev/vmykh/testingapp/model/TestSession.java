package dev.vmykh.testingapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import dev.vmykh.testingapp.model.exceptions.AnsweredQuestionException;
import dev.vmykh.testingapp.model.exceptions.NotEnoughQuestionsException;

public class TestSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1201258043565096467L;
	
	private int id;
	private Student student;
	private int testId;
	private Date timeStart;
	private Date timeFinish;
	private List<AnsweredQuestion> answers;
	private boolean passed;
	private int minCorrectAnswersToPass;
	
	@Override
	public String toString() {
		String str = "TestSession with id=" + id;
		str += "\n" + student;
		str += "\ntestId: " + testId;
		str += "\ntimeStart: " + timeStart;
		str += "\ntimeFinish: " + timeFinish;
		str += "\npassed: " + passed;
		str += "\nminCorrectAnswersToPass" + minCorrectAnswersToPass;
		str += "\nAnswers: {";
		for (AnsweredQuestion aq : answers){
			str += "\n" + aq;
		}
		str += "\n}";
		return str;
	}
	
	public TestSession(Test test, Student student) throws NotEnoughQuestionsException {
		if (test == null) {
			throw new NullPointerException("Failed to initialize TestSession. "
					+ "Test is empty (null)");
		}
		if (student == null) {
			throw new NullPointerException("Failed to initialize TestSession. "
					+ "Student is empty (null)");
		}
		testId = test.getId();
		this.student = student;		
		List<Question> qs = test.getQuestionsForSession();
		answers = new ArrayList<AnsweredQuestion>();
		for (Question q : qs) {
			AnsweredQuestion aq = new AnsweredQuestion(q);
			try {
				aq.setChosenVariantId(-1);
			} catch (AnsweredQuestionException aqe) {
				throw new RuntimeException("Very strange error."
						+ " Can't set variant -1 to AnsweredQuestion", aqe);
			}
			answers.add(aq);
		}
		id = -1;
		minCorrectAnswersToPass = test.getMinCorrectAnswersToPass();
		passed = false;
		
	}
	
	public void checkout() {
		passed = getCorrectAnswersAmount() >= minCorrectAnswersToPass;
	}
//	public String getStartDate() {
//		//TODO
//		return null;
//	}
//	
//	public String getTimeElapsed() {
//		//TODO
//		return null;
//	}
	
	public int getCorrectAnswersAmount() {
		int correct = 0;
		for (AnsweredQuestion a : answers) {
			if (a.getChosenVariantId() == a.getCorrectVariantNumber()) {
				correct++;
			}
		}
		return correct;
	}
	
	public int getTotalAnswersAmount() {
		return answers.size();
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public int getTestId() {
		return testId;
	}
//	public void setTestId(int testId) {
//		this.testId = testId;
//	}
	public Date getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}
	public Date getTimeFinish() {
		return timeFinish;
	}
	public void setTimeFinish(Date timeFinish) {
		this.timeFinish = timeFinish;
	}
	public List<AnsweredQuestion> getAnswers() {
		List<AnsweredQuestion> res = new ArrayList<AnsweredQuestion>(answers.size());
		for (AnsweredQuestion aq : answers) {
			res.add(new AnsweredQuestion(aq));
		}
		return res;
	}
//	public void setAnswers(List<AnsweredQuestion> answers) {
//		this.answers = answers;
//	}

	public void setChosenAnswer(int questionIndex, int variantId) throws AnsweredQuestionException {
		if ( questionIndex < 0 || questionIndex >= answers.size()) {
			throw new IndexOutOfBoundsException("Attempt to set answer for"
					+ " question that doesn't exist in TestSession");
		}
		answers.get(questionIndex).setChosenVariantId(variantId);
	}
	
	public boolean isPassed() {
		return passed;
	}

	public int getMinCorrectAnswersToPass() {
		return minCorrectAnswersToPass;
	}
	
	

//	public void setPassed(boolean passed) {
//		this.passed = passed;
//	}
	
	
	
	
	
}
