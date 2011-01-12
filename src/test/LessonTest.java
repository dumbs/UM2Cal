package test;

import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.dumbs.um2cal.models.Lesson;

public class LessonTest extends TestCase {

	private Lesson lesson;
	
	public LessonTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		lesson = new Lesson(10, false, false, false, 0, "Une Description", "Une location", "Un titre", "Un type", "Un groupe", new GregorianCalendar(), new GregorianCalendar());
	}
	
	public void testInit() {
		assertEquals(10, lesson.getId());
		assertFalse(lesson.isAllDay());
		assertFalse(lesson.isEditable());
		assertTrue(lesson.isReadOnly());
		assertEquals("Une Description", lesson.getDescription());
		assertEquals("Une location", lesson.getLocation());
		assertEquals("Un titre", lesson.getTitle());
		assertEquals("Un type", lesson.getType());
		assertEquals("Un groupe", lesson.getGroup());
		}

	protected void tearDown() throws Exception {
		super.tearDown();
		lesson = null;
	}
	
	public static Test suite(){
	      TestSuite suite = new TestSuite();
	      suite.addTest(new LessonTest("testInit"));
		return suite;
	}

}
