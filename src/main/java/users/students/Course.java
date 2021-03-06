package users.students;

import time.DateInterval;
import time.TimeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class that represents a course that belongs to a student.
 */
public class Course {
    private int CRN;
    private String name;
    private List<Grade> grades;
    private int creditHours;
    private DateInterval dateInterval;
    private TimeInterval timeInterval;

    public Course(String name, int creditHours, int CRN) {
        this.name = name;
        this.creditHours = creditHours;
        this.CRN = CRN;
        this.grades = new ArrayList<>();
    }

    /**
     * Returns the course average
     *
     * @return The course average
     */
    public int getAverage() {
        double total = getTotalScore();

        total /= grades.size();
        if (total >= 90) {
            return 4;
        } else if (total >= 80) {
            return 3;
        } else if (total >= 70) {
            return 2;
        } else if (total >= 60) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Returns the current total score in the course.
     *
     * @return The total score.
     */
    public double getTotalScore() {
        return grades.stream()
                .mapToDouble(Grade::getScore)
                .sum();
    }

    public int getCRN() {
        return CRN;
    }

    public void setCRN(int CRN) {
        this.CRN = CRN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public DateInterval getDateInterval() {
        return dateInterval;
    }

    public void setDateInterval(DateInterval dateInterval) {
        this.dateInterval = dateInterval;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }
}
