package com.foodwise.model;

/**
 * Warden class inheriting from User.
 * Represents the hostel mess administrator / warden.
 */
public class Warden extends User {

    public Warden(String username, String password, String name) {
        super(username, password, "WARDEN", name);
    }

    @Override
    public String getRoleDisplayName() {
        return "Hostel Warden / Mess Admin";
    }

    // Methods matching UML Class Diagram
    public boolean manageFood(FoodMenu menu, String newFoodItems) {
        if (newFoodItems != null && !newFoodItems.trim().isEmpty()) {
            menu.setFood(newFoodItems.trim());
            return true;
        }
        return false;
    }

    public boolean setCutoff(String mealType, String newCutoffTime) {
        return newCutoffTime != null && newCutoffTime.matches("\\d{2}:\\d{2}");
    }

    public String viewDemand(String mealType) {
        return "Displaying confirmed, expected, and recommended demand for " + mealType;
    }

    public String viewDashboard() {
        return "Accessing comprehensive FOODWISE Warden Dashboard";
    }
}
