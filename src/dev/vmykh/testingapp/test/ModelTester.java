package dev.vmykh.testingapp.test;

import static org.junit.Assert.*;

import static dev.vmykh.testingapp.model.Helper.areEqual;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.vmykh.testingapp.model.*;
import dev.vmykh.testingapp.model.exceptions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestMethod;

public class ModelTester {

	TestsManager testsManager;
	TestSessionsManager testSessionManager;
	PersistenceManager persistenceManager;
	ImagesManager imagesManager;
	Random rand = new Random(172);
	
	@Before
	public void init() throws TestSessionCorruptedException, TestCorruptedException, PersistenceException {
		testsManager = TestsManager.getInstance();
		testSessionManager = TestSessionsManager.getInstance();
		persistenceManager = PersistenceManager.getInstance();
		imagesManager = ImagesManager.getInstance();
	}
	
	@Test
	public void createTest() throws QuestionException, NotEnoughQuestionsException {
		dev.vmykh.testingapp.model.Test curTest = createTest(3, 2);
		
		Question q1 = createQuestion();
		Question q2 = createQuestion();
		Question q3 = createQuestion();
		Question q4 = createQuestion();
		Question q5 = createQuestion();
		assertEquals(q1.getId(), -1);
		assertEquals(q4.getId(), -1);
		assertEquals(q5.getId(), -1);
		
		assertEquals(curTest.getAllQuestions().size(), 0);
		
		curTest.addQuestion(q1);
		curTest.addQuestion(q2);
		curTest.addQuestion(q3);
		curTest.addQuestion(q5);
		
		assertEquals(curTest.getAllQuestions().size(), 4);
		
		assertNotEquals(q1.getId(), -1);
		assertNotEquals(q2.getId(), -1);
		assertNotEquals(q5.getId(), -1);
		
//		assertThat("question id after adding to test must be not \"-1\"", allOf())
		
		
		List<Question> questionsForSession = curTest.getQuestionsForSession();
		assertEquals(questionsForSession.size(), 3);
		for (int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				if (i != j) {
					assertNotEquals("questions id in session must be different", 
							questionsForSession.get(i).getId(), 
							questionsForSession.get(j).getId());
				}
			}
		}
		
	}
	
	@Test
	public void testNormalQuestion() throws QuestionException{
		Question q = createQuestion();
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "v1"));
		variants.add(new Variant(2, "v2"));
		variants.add(new Variant(5, "v5"));
		q.setVariants(variants);
	}
	
	@Test(expected = QuestionException.class)
	public void  testWrongVariantsNumberLess() throws QuestionException{
		Question q = createQuestion();
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "v1"));
		variants.add(new Variant(2, "v2"));
		q.setVariants(variants);
	}

	@Test(expected = QuestionException.class)
	public void  testWrongVariantsNumberGreater() throws QuestionException{
		Question q = createQuestion();
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "v1"));
		variants.add(new Variant(2, "v2"));
		variants.add(new Variant(3, "v3"));
		variants.add(new Variant(4, "v4"));
		q.setVariants(variants);
	}
	
	@Test(expected = QuestionException.class)
	public void  testSameIdInVariants() throws QuestionException{
		Question q = createQuestion();
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "v1"));
		variants.add(new Variant(2, "v2"));
		variants.add(new Variant(2, "v3"));
		q.setVariants(variants);
	}
	
	@Test(expected = QuestionException.class)
	public void  testWrongCorrectVariantId() throws QuestionException{
		Question q = createQuestion();
		
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "v1"));
		variants.add(new Variant(2, "v2"));
		variants.add(new Variant(3, "v3"));
		q.setVariants(variants);
		q.setCorrectVariantNumber(4);
	}
	
	@Test(expected = NotEnoughQuestionsException.class)
	public void testNotEnoughQuestionsForSession() throws QuestionException, NotEnoughQuestionsException {
		dev.vmykh.testingapp.model.Test curtest = createTest(3, 2);
		
		curtest.addQuestion(createQuestion());
		curtest.addQuestion(createQuestion());
		
		List<Question> qs = curtest.getQuestionsForSession();
	}
	
	@Test
	public void testIsEmpty() {
		assertTrue(Helper.isEmpty(null));
		assertTrue(Helper.isEmpty(""));
		assertTrue(Helper.isEmpty("   "));
		assertTrue(Helper.isEmpty("   		"));
		assertFalse(Helper.isEmpty("q"));
		assertFalse(Helper.isEmpty("0"));
		assertFalse(Helper.isEmpty("150m"));
	}
	
	@Test
	public void testQuestionsImmutabilityInTest() throws QuestionException, NotEnoughQuestionsException {
		dev.vmykh.testingapp.model.Test curtest = createTest(4, 3);
		for (int i = 0; i < 5; ++i) {
			curtest.addQuestion(createQuestion());
		}
		assertEquals(curtest.getAllQuestions().size(), 5);
		assertEquals(curtest.getQuestionsForSession().size(), 4);
		
		List<Question> qs = curtest.getAllQuestions();
		String title  = qs.get(3).getQuestionStr();
		qs.get(3).setQuestionStr("Something different");
		assertEquals(curtest.getAllQuestions().get(3).getQuestionStr(), title);
		assertNotEquals(qs.get(3).getQuestionStr(), title);
		
		qs = curtest.getQuestionsForSession();
		int id = qs.get(1).getId();
		title = qs.get(1).getQuestionStr();
		qs.get(1).setQuestionStr("Bingo");
		assertNotEquals(qs.get(1).getQuestionStr(), title);
		assertEquals(curtest.getQuestionById(id).getQuestionStr(), title);
		
		
	}
	
	@Test
	public void testVariantsImmutabilityInQuestion() throws QuestionException {
		List<Variant> vs = createVariants(3);
		Question q = createQuestion();
		q.setNumberOfVariants(3);
		q.setVariants(vs);
		q.setCorrectVariantNumber(1);
		assertTrue(q.isValid());
		
		vs = q.getVariants();
		String str = vs.get(1).getText();
		vs.get(1).setText("other text");
		assertNotEquals(str, vs.get(1).getText());
		assertEquals(str, q.getVariants().get(1).getText());
		
	}
	
	
	
	@Test
	public void checkVariantPositiveId() {
		Variant v = new Variant(-5, "bingo");
		assertEquals(v.getId(), 5);
	}
	
	@Test(expected = AnsweredQuestionException.class)
	public void testNotExistingChosenVariantSetup() throws QuestionException, AnsweredQuestionException {
		Question q = createQuestion();
		q.setNumberOfVariants(3);
		q.setVariants(createVariants(3));
		q.setCorrectVariantNumber(1);
		AnsweredQuestion aq = new AnsweredQuestion(q);
		aq.setChosenVariantId(10);
		
		//imagesManager.
	}
	
	@Test
	public void testAreEqual() {
		assertTrue(areEqual(null, null));
		assertTrue(areEqual("", ""));
		assertTrue(areEqual("bingo", "bingo"));
		assertFalse(areEqual("", null));
		assertFalse(areEqual("q", null));
		assertFalse(areEqual("\n", null));
		assertFalse(areEqual(null, ""));
		assertFalse(areEqual(null, "s"));
		assertFalse(areEqual(null, "\n"));
		
	}
	
	
	/*
	 * 
	 * 
	 * Helping functions start below
	 */
	
	public static List<Variant> createVariants(int amount) {
		List<Variant> vs = new ArrayList<Variant>(amount);
		for(int i = 0; i < amount; ++i) {
			vs.add(new Variant(i, "bingozavr" + i));
		}
		return vs;
	}
	
	public static dev.vmykh.testingapp.model.Test createTest(int qAmount, int correctAmount){
		dev.vmykh.testingapp.model.Test curTest = new dev.vmykh.testingapp.model.Test();
		curTest.setName("Тест с географии №" + getRandomNumber());
		curTest.setDescription("Тест проверяет базовые знания");
		curTest.setTimeForPassing(1000*60*15);  //15 minutes
		curTest.setId(-1);
		curTest.setQuestionAmountForSession(qAmount);
		curTest.setMinCorrectAnswersToPass(correctAmount);
		return curTest;
	}
	
	public static Question createQuestion() throws QuestionException {
		Question question = new Question();
		question.setNumberOfVariants(3);
		question.setQuestionStr("Вопрос №" + getRandomNumber());
		question.setId(-1);
		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(1, "Ответ №1"));
		variants.add(new Variant(2, "Ответ №2"));
		variants.add(new Variant(3, "Ответ №3"));
		question.setVariants(variants);
		question.setCorrectVariantNumber(2);
		return question;
	}
	
	public static int getRandomNumber(){
		return (Math.abs(Helper.getRandom().nextInt()) % 1000);
	}

}
