package dev.vmykh.testingapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import dev.vmykh.testingapp.model.exceptions.NotEnoughQuestionsException;
import dev.vmykh.testingapp.model.exceptions.QuestionException;
import dev.vmykh.testingapp.model.exceptions.TestIsNotReadyException;

public class Test implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1903131601517607180L;
	
	
	
	private int nextQuestionId = 1;
	
	
	private int id;
	private String name;
	private String description;
	private int questionAmountForSession;
	private int minCorrectAnswersToPass;
	private long timeForPassing;
	private List<Question> questions;
	private boolean available;
	
	//private boolean passwordEnabled;
	private String password;
	
	@Override
	public String toString() {
		String str = "Test with id=" + id + ":\n";
		str += "name: " + name;
		str += "\ndescription: " + description;
		str += "\nquestionAmountForSession: " + questionAmountForSession;
		str += "\nminCorrectAnswersToPass: " + minCorrectAnswersToPass;
		str += "\ntimeForPassing: " + timeForPassing;
		str += "\navailable: " + available;
		str += "\npassword: " + password;
		str += "\nQuestions: {";
		for (int i = 0; i < questions.size() - 1; ++i) {
			str += "\n ---(  " + questions.get(i) + "  )---";
		}
		if (questions.size() > 0) {
			str += "\n ---(  " + questions.get(questions.size() - 1) + "  )---";
		}
		str += "\n}";
		
		
		return str;
	}
	
	
	public Test() {
		questions = new ArrayList<>();
		available = false;
		questionAmountForSession = 0;
		minCorrectAnswersToPass = 0;
		timeForPassing = 1000*60*30;   // 30 minutes
		id = -1;		
	}
	
	public boolean isValid() {
		if (Helper.isEmpty(name)) {
			return false;
		}
		if (questionAmountForSession <= 0 
				|| minCorrectAnswersToPass <= 0
				|| minCorrectAnswersToPass > questionAmountForSession
				|| timeForPassing <= 0
				|| questions.size() == 0
				|| questionAmountForSession > questions.size()
				) {
//			System.out.println("long check");
			return false;
		}
		return true;
	}
	public List<Question> getQuestionsForSession() throws NotEnoughQuestionsException {
		if (questions.size() < questionAmountForSession) {
			throw new NotEnoughQuestionsException(
					"Test doesn't have enough question for session. "
					+ "Number of available questions: " + questions.size()
					+ " Needed for session: " + questionAmountForSession
					);
		}
		List<Question> questionForSession = new ArrayList<Question>();
		List<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < questions.size(); ++i) {
			indexes.add(i);
		}
		for(int i = 0; i < questionAmountForSession; ++i){
			int curQuesIndex = Helper.getRandomInt(0, indexes.size() - 1);
			questionForSession.add(new Question(questions.get(indexes.get(curQuesIndex))));
			indexes.remove(curQuesIndex);
		}
		return questionForSession;
	}
	
	public Question getQuestionById(int id) {
		Question res = null;
		if (!questionExists(id)) {
			throw new NoSuchElementException("Question with id:" + id
					+ "   doesn't exist");
		}
		for(Question q : questions) {
			if(q.getId() == id) {
				return new Question(q);
			}
		}
		throw new NoSuchElementException("Question with id:" + id
				+ "   doesn't exist");
	}
	
	public void editQuestion(int id, Question q) throws QuestionException {
		if (!questionExists(id)) {
			throw new NoSuchElementException("Error while trying to edit question."
					+ " Question with id=" + id + " doesn't exist.");
		}
		if (q == null) {
			throw new QuestionException("Attempt to add empty (null) question"
					+ " in Test.editQuestion method");
		}
		if (!q.isValid()){
			throw new QuestionException("Attempt to add invalid question"
					+ "to Test in editQuestion method");
		}
		q.setId(id);
		for (int i = 0; i < questions.size(); ++i) {
			if (questions.get(i).getId() == id) {
				questions.set(i, new Question(q));
				return;
			}
		}
	}
	
	public boolean questionExists(int id){
		for(Question q: questions) {
			if (q.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public void addQuestion(Question question) throws QuestionException {
		if (question == null) {
			throw new QuestionException("Attempt to add empty (null) question"
					+ " in Test.addQuestion method");
		}
		question.setId(nextQuestionId++);
		if (!question.isValid()) {
			throw new QuestionException("Attempt to add invalid question to test");
		}
		questions.add(new Question(question));
	}

	public void deleteQuestionById(int id) {
		if (!questionExists(id)) {
			throw new NoSuchElementException("Error while trying to delete question."
					+ " Question with id=" + id + " doesn't exist.");
		}
		for (int i = 0; i < questions.size(); ++i) {
			if (questions.get(i).getId() == id) {
				questions.remove(i);
			}
		}
	}
	
	public List<Question> getAllQuestions() {
		List<Question> res = new ArrayList<>();
		for (Question q : questions) {
			res.add(new Question(q));
		}
		return res;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuestionAmountForSession() {
		return questionAmountForSession;
	}

	public void setQuestionAmountForSession(int questionAmountForSession) {
		this.questionAmountForSession = questionAmountForSession;
	}

	public int getMinCorrectAnswersToPass() {
		return minCorrectAnswersToPass;
	}

	public void setMinCorrectAnswersToPass(int minCorrectAnswersToPass) {
		this.minCorrectAnswersToPass = minCorrectAnswersToPass;
	}

	public long getTimeForPassing() {
		return timeForPassing;
	}

	public void setTimeForPassing(long timeForPassing) {
		this.timeForPassing = timeForPassing;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) throws TestIsNotReadyException {
		if (available) {
			if (isValid()) {
				this.available = available;
			} else {
				throw new TestIsNotReadyException("Invalid test can't be available for passing.");
			}	
		}
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
