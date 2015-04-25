package dev.vmykh.testingapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestSessionCorruptedException;

public class TestSessionsManager {
	private static TestSessionsManager instance;
	
	
	
	private List<TestSession> testSessions;
	
	private TestSessionsManager() throws TestSessionCorruptedException, PersistenceException{
		loadTestSessions();
		if (!AllTestIdsAreDifferent()) {
			throw new TestSessionCorruptedException("Two or more"
					+ "test sessions have same id.");
		}
	}
	
	public static TestSessionsManager getInstance() throws TestSessionCorruptedException, PersistenceException{
		if (instance == null) {
			instance = new TestSessionsManager();
		}
		return instance;
	}

//	
	
	public TestSession getTestSessionById(int id) throws NoSuchElementException {
		for(TestSession ts : testSessions) {
			if (ts.getId() == id) {
				return ts;
			}
		}
		throw new NoSuchElementException("TestSession with id " + id + " doesn't exist");
	}

	public void addTestSession(TestSession ts) {
		if (ts == null) {
			throw new NullPointerException("Attempt to add empty (null)"
					+ " test session");
		}
		ts.setId(findMaxId() + 1);
		testSessions.add(ts);		
	}

	public void removeTestSessionById(int id) {
		for(int i = 0; i < testSessions.size(); ++i) {
			if (testSessions.get(i).getId() == id) {
				testSessions.remove(i);
				return;
			}
		}
		throw new NoSuchElementException("TestSession with id " + id + " doesn't exist");
	}

	public void saveTestSessions() throws PersistenceException {
		PersistenceManager.getInstance().saveTestSessions(testSessions);
	}
	
	public void loadTestSessions() throws PersistenceException {
		testSessions = PersistenceManager.getInstance().loadTestsSessions();
		if (testSessions == null) {
			testSessions = new ArrayList<TestSession>();
		}
	}

	public List<TestSession> getAllTestSessions() {
		return testSessions;
	}
	
	private int findMaxId() {
		int max = 0;
		for (TestSession ts : testSessions) {
			if (ts.getId() > max) {
				max = ts.getId();
			}
		}
		return max;
	}
	
	public boolean testSessionExists(int id) {
		for (TestSession ts : testSessions) {
			if (ts.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public boolean AllTestIdsAreDifferent() {
		for(int i = 0; i < testSessions.size(); i++) {
			for(int j = 0; j < testSessions.size(); j++) {
				if (i != j) {
					if (testSessions.get(i) == testSessions.get(j)) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
