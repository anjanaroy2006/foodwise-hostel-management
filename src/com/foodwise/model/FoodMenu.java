package com.foodwise.model;

/**
 * Model class for Food Menu items.
 */
public class FoodMenu {
    private String mealType;   // "Breakfast", "Lunch", "Tea/Coffee", "Dinner"
    private String food;       // e.g. "Chapathi • Potato Curry • Coffee"
    private String mealTime;   // e.g. "07:30 AM - 09:00 AM"

    public FoodMenu(String mealType, String food, String mealTime) {
        this.mealType = mealType;
        this.food = food;
        this.mealTime = mealTime;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
}
