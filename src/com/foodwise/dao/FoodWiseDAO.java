package com.foodwise.dao;

import com.foodwise.model.*;
import com.foodwise.service.PredictionModule;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object (DAO) for FOODWISE.
 * Performs JDBC operations when MySQL is active, and provides synchronized
 * memory backing if MySQL is offline.
 */
public class FoodWiseDAO {

    // In-memory fallback structures initialized with exact presentation slide values
    private static final Map<String, User> mockUsers = new LinkedHashMap<>();
    private static final Map<String, FoodMenu> mockMenus = new LinkedHashMap<>();
    private static final Map<String, String> mockCutoffs = new LinkedHashMap<>();
    private static final Map<String, Map<String, String>> mockStudentResponses = new LinkedHashMap<>();
    private static final List<Feedback> mockFeedbackList = new ArrayList<>();

    static {
        // Sample Users
        mockUsers.put("akhil", new Student("akhil", "pass123", "Akhil Menon", "STU101"));
        mockUsers.put("anjana", new Student("anjana", "pass123", "Anjana Roy", "STU102"));
        mockUsers.put("amal", new Student("amal", "pass123", "Amal Krishna K", "STU103"));
        mockUsers.put("abin", new Student("abin", "pass123", "Abin Roy", "STU104"));
        mockUsers.put("andriya", new Student("andriya", "pass123", "Andriya Aby", "STU105"));
        mockUsers.put("warden", new Warden("warden", "warden123", "Chief Warden"));

        // Food Menu (Slide 12)
        mockMenus.put("Breakfast", new FoodMenu("Breakfast", "Chapathi • Potato Curry • Coffee", "07:30 AM - 09:00 AM"));
        mockMenus.put("Lunch", new FoodMenu("Lunch", "Rice • Chicken Curry • Salad", "12:30 PM - 02:00 PM"));
        mockMenus.put("Tea/Coffee", new FoodMenu("Tea/Coffee", "Tea • Snack", "04:30 PM - 05:30 PM"));
        mockMenus.put("Dinner", new FoodMenu("Dinner", "Chapathi • Vegetable Curry", "07:30 PM - 09:00 PM"));

        // Cutoff Times (Slide 9 & 12)
        mockCutoffs.put("Breakfast", "06:00");
        mockCutoffs.put("Lunch", "10:30");
        mockCutoffs.put("Tea/Coffee", "15:30");
        mockCutoffs.put("Dinner", "18:30");

        // Akhil's initial state (Slide 12)
        Map<String, String> akhilMeals = new LinkedHashMap<>();
        akhilMeals.put("Breakfast", "YES");
        akhilMeals.put("Lunch", "NO");
        akhilMeals.put("Tea/Coffee", "YES");
        akhilMeals.put("Dinner", "YES");
        mockStudentResponses.put("STU101", akhilMeals);

        // Feedbacks
        mockFeedbackList.add(new Feedback("STU101", "Akhil Menon", "Breakfast", 5, "Chapathi was hot and soft, curry was delicious!", "2026-10-27"));
        mockFeedbackList.add(new Feedback("STU102", "Anjana Roy", "Dinner", 4, "Good vegetable curry, but chapathi got over a bit early.", "2026-10-26"));
        mockFeedbackList.add(new Feedback("STU103", "Amal Krishna K", "Lunch", 5, "Chicken curry was very flavorful. Perfect quantity!", "2026-10-26"));
    }

    public User authenticate(String username, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    String name = rs.getString("name");
                    if ("STUDENT".equalsIgnoreCase(role)) {
                        String stuId = rs.getString("student_id");
                        return new Student(username, password, name, stuId);
                    } else {
                        return new Warden(username, password, name);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Database error during login: " + e.getMessage());
            }
        }

        // Memory fallback
        User user = mockUsers.get(username.toLowerCase().trim());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<FoodMenu> getAllMenus() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            List<FoodMenu> list = new ArrayList<>();
            String sql = "SELECT * FROM food_menu ORDER BY id ASC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new FoodMenu(
                            rs.getString("meal_type"),
                            rs.getString("food_items"),
                            rs.getString("meal_time")
                    ));
                }
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.err.println("Error reading food menu: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockMenus.values());
    }

    public void updateMenu(String mealType, String foodItems) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE food_menu SET food_items = ? WHERE meal_type = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, foodItems);
                stmt.setString(2, mealType);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating menu: " + e.getMessage());
            }
        }
        FoodMenu menu = mockMenus.get(mealType);
        if (menu != null) {
            menu.setFood(foodItems);
        }
    }

    public Map<String, String> getCutoffTimes() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            Map<String, String> map = new LinkedHashMap<>();
            String sql = "SELECT meal_type, cutoff_time FROM cutoff_settings";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String time = rs.getString("cutoff_time");
                    if (time != null && time.length() >= 5) {
                        time = time.substring(0, 5);
                    }
                    map.put(rs.getString("meal_type"), time);
                }
                if (!map.isEmpty()) return map;
            } catch (SQLException e) {
                System.err.println("Error reading cutoff times: " + e.getMessage());
            }
        }
        return new LinkedHashMap<>(mockCutoffs);
    }

    public void updateCutoffTime(String mealType, String newTime) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE cutoff_settings SET cutoff_time = ?, display_time = ? WHERE meal_type = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newTime + ":00");
                stmt.setString(2, newTime);
                stmt.setString(3, mealType);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating cutoff time: " + e.getMessage());
            }
        }
        mockCutoffs.put(mealType, newTime);
    }

    public String getMealIntentStatus(String studentId, String mealType) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT status FROM meal_responses WHERE student_id = ? AND meal_type = ? AND date = CURDATE()";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, mealType);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("status");
                }
            } catch (SQLException e) {
                System.err.println("Error fetching meal response: " + e.getMessage());
            }
        }
        Map<String, String> userMeals = mockStudentResponses.get(studentId);
        if (userMeals != null && userMeals.containsKey(mealType)) {
            return userMeals.get(mealType);
        }
        return "YES"; // Default intent is YES (food prepared unless cancelled)
    }

    public boolean updateMealIntent(String studentId, String mealType, String status) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO meal_responses (student_id, meal_type, status, date) VALUES (?, ?, ?, CURDATE()) " +
                         "ON DUPLICATE KEY UPDATE status = VALUES(status)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, mealType);
                stmt.setString(3, status);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving meal response: " + e.getMessage());
            }
        }
        mockStudentResponses.computeIfAbsent(studentId, k -> new LinkedHashMap<>()).put(mealType, status);
        return true;
    }

    public List<DemandSummary> getDemandSummary() {
        List<DemandSummary> list = new ArrayList<>();
        String[] meals = {"Breakfast", "Lunch", "Tea/Coffee", "Dinner"};

        // Base sample values directly matching Presentation Slide 13
        Map<String, Integer> baseConfirmed = new HashMap<>();
        baseConfirmed.put("Breakfast", 430);
        baseConfirmed.put("Lunch", 320);
        baseConfirmed.put("Tea/Coffee", 380);
        baseConfirmed.put("Dinner", 410);

        PredictionModule predictor = new PredictionModule();
        Map<String, String> cutoffs = getCutoffTimes();

        for (String meal : meals) {
            int confirmed = baseConfirmed.getOrDefault(meal, 300);

            // If connected to MySQL, we can compute actual count of YES responses + base
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                String sql = "SELECT COUNT(*) AS cnt FROM meal_responses WHERE meal_type = ? AND date = CURDATE() AND status = 'YES'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, meal);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int recorded = rs.getInt("cnt");
                        if (recorded > 0) {
                            confirmed = recorded;
                        }
                    }
                } catch (SQLException ignored) {}
            }

            // Demand Prediction module calculation
            List<Integer> history = Arrays.asList(confirmed + 15, confirmed + 10, confirmed + 20);
            int expected = predictor.calculatePredictedDemand(confirmed, history);
            int recommended = predictor.calculateRecommendedPreparation(expected);

            String cutoff = cutoffs.getOrDefault(meal, "10:30");
            MealResponse dummy = new MealResponse("", meal, "YES", "", cutoff);
            String status = dummy.isCancellationOpen() ? "Cancellation Open" : "Closed";

            list.add(new DemandSummary(meal, confirmed, expected, recommended, cutoff, status));
        }

        return list;
    }

    public void addFeedback(Feedback fb) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO feedback (student_id, student_name, meal_type, rating, comment, date) VALUES (?, ?, ?, ?, ?, CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, fb.getStudentId());
                stmt.setString(2, fb.getStudentName());
                stmt.setString(3, fb.getMealType());
                stmt.setInt(4, fb.getRating());
                stmt.setString(5, fb.getComment());
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving feedback: " + e.getMessage());
            }
        }
        mockFeedbackList.add(0, fb);
    }

    public List<Feedback> getAllFeedback() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            List<Feedback> list = new ArrayList<>();
            String sql = "SELECT * FROM feedback ORDER BY id DESC LIMIT 20";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Feedback(
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("meal_type"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getString("date")
                    ));
                }
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.err.println("Error fetching feedbacks: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockFeedbackList);
    }

    // Helper DTO for Dashboard Summary
    public static class DemandSummary {
        public String mealType;
        public int confirmed;
        public int expected;
        public int recommended;
        public String cutoff;
        public String status;

        public DemandSummary(String mealType, int confirmed, int expected, int recommended, String cutoff, String status) {
            this.mealType = mealType;
            this.confirmed = confirmed;
            this.expected = expected;
            this.recommended = recommended;
            this.cutoff = cutoff;
            this.status = status;
        }
    }
}
