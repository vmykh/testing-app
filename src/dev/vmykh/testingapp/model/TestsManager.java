package dev.vmykh.testingapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import dev.vmykh.testingapp.model.exceptions.IdManagerNotFoundException;
import dev.vmykh.testingapp.model.exceptions.PersistenceException;
import dev.vmykh.testingapp.model.exceptions.TestCorruptedException;

public class TestsManager {
	private static TestsManager instance;
	
	private PersistenceManager persistenceManager;
	private IdManager idManager;
	private List<Test> tests;
	
	private TestsManager() throws TestCorruptedException, PersistenceException {
		persistenceManager = PersistenceManager.getInstance();
		loadTests();
		if (!AllTestIdsAreDifferent()) {
			throw new TestCorruptedException("Two or more tests have same id");
		}
		try {
			idManager = persistenceManager.getInstance().loadIdManager();
		} catch (IdManagerNotFoundException e) {
			e.printStackTrace();
			int nextId = findMaxId() + 1;
			idManager = new IdManager(nextId);
			System.out.println("New IdManager was created with start id = " + nextId);
		}
	}
	
	public static TestsManager getInstance() throws TestCorruptedException, PersistenceException {
		if (instance == null) {
			instance = new TestsManager();
		}
		return instance;
	}
	
	public Test getTestById(int id) throws NoSuchElementException {
		for(Test t : tests) {
			if (t.getId() == id) {
				return t;
			}
		}
		throw new NoSuchElementException("Test with id " + id + " doesn't exist");
	}
	
	public boolean testExists(int id) {
		for (Test t : tests) {
			if (t.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public void addTest(Test test) throws PersistenceException {
		if (test == null) {
			throw new NullPointerException("Attempt to add empty test (null)");
		}
		test.setId(idManager.getNextTestId());
		tests.add(test);
		
//		saveTests();
		persistenceManager.saveIdManager(idManager);
	}
	
	public boolean AllTestIdsAreDifferent() {
		for(int i = 0; i < tests.size(); i++) {
			for(int j = 0; j < tests.size(); j++) {
				if (i != j) {
					if (tests.get(i) == tests.get(j)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private int findMaxId() {
		int max = 0;
		for (Test t : tests) {
			if (t.getId() > max) {
				max = t.getId();
			}
		}
		return max;
	}

	public void removeTestById(int id) throws NoSuchElementException, PersistenceException {
		for (int i = 0; i < tests.size(); ++i) {
			if (tests.get(i).getId() == id) {
				tests.remove(i);
//				saveTests();
				return;
			}
		}
		throw new NoSuchElementException("Test with id " + id + " doesn't exist");
	}

	public void saveTests() throws PersistenceException {
		persistenceManager.saveTests(tests);
	}

	public void loadTests() throws PersistenceException {
		tests = persistenceManager.loadTests();
		if (tests == null) {
			tests = new ArrayList<Test>();
		}
	}

	public List<Test> getAllTests() {
		return tests;
	}
}
