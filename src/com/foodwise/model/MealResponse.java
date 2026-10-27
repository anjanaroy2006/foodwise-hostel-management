package com.foodwise.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class for Student Meal Intent Response.
 * Implements Smart Cancellation cutoff check logic.
 */
public class MealResponse {
    private String studentId;
    private String mealType;
    private String status;      // "YES" or "NO"
    private String timestamp;
    private String cutoffTime;  // "06:00", "10:30", etc. (HH:mm)

    public MealResponse(String studentId, String mealType, String status, String timestamp, String cutoffTime) {
        this.studentId = studentId;
        this.mealType = mealType;
        this.status = status;
        this.timestamp = timestamp;
        this.cutoffTime = cutoffTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCutoffTime() {
        return cutoffTime;
    }

    public void setCutoffTime(String cutoffTime) {
        this.cutoffTime = cutoffTime;
    }

    /**
     * Checks if current time is strictly before the configured cutoff time.
     * Before cutoff: Change allowed.
     * After cutoff: Locked.
     */
    public boolean isCancellationOpen() {
        if (cutoffTime == null || cutoffTime.trim().isEmpty()) {
            return true;
        }
        try {
            // Cutoff time format: HH:mm (or HH:mm:ss)
            String timePart = cutoffTime.trim();
            if (timePart.length() > 5) {
                timePart = timePart.substring(0, 5);
            }
            LocalTime cutoff = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime now = LocalTime.now();
            return now.isBefore(cutoff);
        } catch (Exception e) {
            return true; // Default allow if time parsing fails
        }
    }
}
