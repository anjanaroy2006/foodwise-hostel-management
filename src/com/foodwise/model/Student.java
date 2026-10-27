package com.foodwise.model;

/**
 * Student class inheriting from User.
 * Represents a student actor in FOODWISE system.
 */
public class Student extends User {
    private String studentId;

    public Student(String username, String password, String name, String studentId) {
        super(username, password, "STUDENT", name);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public String getRoleDisplayName() {
        return "Student (" + studentId + ")";
    }

    // Business methods matching UML Class Diagram
    public String viewMeals() {
        return "Viewing upcoming breakfast, lunch, tea/coffee, dinner for " + getName();
    }

    public boolean setIntent(String mealType, String status) {
        // Business logic handler for marking intent
        return "YES".equalsIgnoreCase(status) || "NO".equalsIgnoreCase(status);
    }

    public boolean cancelMeal(String mealType, boolean isBeforeCutoff) {
        // Business rule: smart cancellation only permitted before cutoff
        return isBeforeCutoff;
    }

    public Feedback giveFeedback(String mealType, int rating, String comment) {
        return new Feedback(studentId, getName(), mealType, rating, comment, java.time.LocalDate.now().toString());
    }
}
