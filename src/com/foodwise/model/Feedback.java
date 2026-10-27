package com.foodwise.model;

/**
 * Model class for Student Meal Feedback.
 */
public class Feedback {
    private String studentId;
    private String studentName;
    private String mealType;
    private int rating;         // 1 to 5
    private String comment;
    private String date;

    public Feedback(String studentId, String studentName, String mealType, int rating, String comment, String date) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mealType = mealType;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
