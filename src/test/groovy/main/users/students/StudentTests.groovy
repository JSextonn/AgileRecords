package main.users.students

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import security.util.HashingUtil
import users.students.Course
import users.students.Grade
import users.students.GradeType
import users.students.Student

/**
 * Class that will contain all tests regarding the Student class.
 */
class StudentTests extends GroovyTestCase {
    static Student getTestStudent() {
        // Create test course one.
        Course courseOne = new Course("MathUtil 2302", 4, 12345)
        courseOne.getGrades().add(new Grade(60, GradeType.Test))
        courseOne.getGrades().add(new Grade(70, GradeType.Test))
        courseOne.getGrades().add(new Grade(50, GradeType.Test))
        courseOne.getGrades().add(new Grade(40, GradeType.Test))
        courseOne.getGrades().add(new Grade(30, GradeType.Test))
        courseOne.getGrades().add(new Grade(20, GradeType.Test))
        courseOne.getGrades().add(new Grade(10, GradeType.Test))
        courseOne.getGrades().add(new Grade(60, GradeType.Test))

        // Create test course two.
        Course courseTwo = new Course("MathUtil 2304", 3, 54321)
        courseOne.getGrades().add(new Grade(50, GradeType.Test))
        courseOne.getGrades().add(new Grade(40, GradeType.Test))
        courseOne.getGrades().add(new Grade(30, GradeType.Test))
        courseOne.getGrades().add(new Grade(52, GradeType.Test))
        courseOne.getGrades().add(new Grade(68, GradeType.Test))
        courseOne.getGrades().add(new Grade(26, GradeType.Test))
        courseOne.getGrades().add(new Grade(30, GradeType.Test))
        courseOne.getGrades().add(new Grade(10, GradeType.Test))

        // Create test course three.
        Course courseThree = new Course("MathUtil 2305", 4, 53621)
        courseOne.getGrades().add(new Grade(20, GradeType.Test))
        courseOne.getGrades().add(new Grade(30, GradeType.Test))
        courseOne.getGrades().add(new Grade(40, GradeType.Test))
        courseOne.getGrades().add(new Grade(60, GradeType.Test))
        courseOne.getGrades().add(new Grade(50, GradeType.Test))
        courseOne.getGrades().add(new Grade(30, GradeType.Test))
        courseOne.getGrades().add(new Grade(20, GradeType.Test))
        courseOne.getGrades().add(new Grade(65, GradeType.Test))

        // Add them to list.
        List<Course> courses = new ArrayList<>()
        courses.add(courseOne)
        courses.add(courseTwo)
        courses.add(courseThree)

        // Create test student and return the object.
        Student testStudent = new Student("testStudent", HashingUtil.hash("username", "SHA-256"))
        testStudent.setCourses(courses)
        return testStudent
    }

    // Passed
    void testGpaAverage() {
        assertEquals(getTestStudent().getGPA(), 0.0)
    }
}
