package dev.vmykh.testingapp.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import dev.vmykh.testingapp.model.ResourceBundle;
import dev.vmykh.testingapp.model.Student;
import dev.vmykh.testingapp.model.TestSession;
import dev.vmykh.testingapp.model.TestSessionsManager;
import dev.vmykh.testingapp.model.TestsManager;
import dev.vmykh.testingapp.model.exceptions.AnsweredQuestionException;
import dev.vmykh.testingapp.model.exceptions.NotEnoughQuestionsException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.QuestionException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;
import static dev.vmykh.testingapp.test.ModelTester.*;

public class PersistenceTester {
	
	@Before
	public void prepare() throws QuestionException, TestCorruptedException, PersistenceException, NoSuchElementException, NotEnoughQuestionsException, AnsweredQuestionException, TestSessionCorruptedException {
		File testsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TESTS_FILE_NAME);
		File sessionsFile = new File(ResourceBundle.RESOURCES_DIR + File.separator
				+ ResourceBundle.TEST_SESSIONS_FILE_NAME);
		testsFile.delete();
		sessionsFile.delete();
		
		
		
		dev.vmykh.testingapp.model.Test curtest = createTest(4, 3);
		curtest.addQuestion(createQuestion());
		curtest.addQuestion(createQuestion());
		curtest.addQuestion(createQuestion());
		curtest.addQuestion(createQuestion());
		curtest.addQuestion(createQuestion());
		TestsManager.getInstance().addTest(curtest);
		//TestsManager.getInstance().saveTests();
		
		curtest = createTest(3, 2);
		curtest.setName("bingo test #325");
		curtest.addQuestion(createQuestion());
		
		TestsManager.getInstance().addTest(curtest);
		//TestsManager.getInstance().saveTests();
		
		curtest = createTest(10, 5);
		curtest.setName("Really hard test #178");
		curtest.setDescription("There are no questions yet");
		
		List<TestSession> sessions = new ArrayList<TestSession>();
		
		TestSession s = new TestSession(
				TestsManager.getInstance().getTestById(1), 
				new Student("Вінстон", "Джеймс", "SOME DEFAULT GROUP"));
		
		s.setChosenAnswer(0, 1);
		s.setTimeStart(new Date(10000));
		s.setTimeFinish(new Date(20000));
		
		TestSessionsManager.getInstance().addTestSession(s);
		
		TestSessionsManager.getInstance().saveTestSessions();
		
		TestsManager.getInstance().addTest(curtest);
		TestsManager.getInstance().saveTests();
		
	}
	
	@Test
	public void example(){
		
	}
}
