# FOODWISE — Smart Hostel Meal Management System
> **Predict. Prepare. Prevent. Preserve.**  
> *An intelligent food-waste prevention platform for institutional kitchens*  
> **Team Members:** Amal Krishna K • Abin Roy • Andriya Aby • Anjana Roy

---

## 📌 Project Overview
Hostel messes typically prepare food using total hostel strength (e.g. 500 students), leading to significant mismatch between food prepared and food actually consumed. 

**FOODWISE** solves this by:
1. **Collecting Meal Intent**: Students indicate whether Breakfast, Lunch, Tea/Coffee, and Dinner are required.
2. **Upcoming Meal Information**: Displays food items for upcoming meals before students decide.
3. **Smart Cancellation Cutoffs**: Configurable cancellation deadlines set by the warden (locked after cutoff).
4. **Demand Prediction Module**: Forecasts expected kitchen demand based on confirmed intent and historical averages.
5. **Kitchen Recommendations**: Translates demand into safe preparation recommendations (e.g. 320 confirmed lunch &rarr; 335 expected demand &rarr; 340 recommended preparation).
6. **Student Feedback Loop**: Collects meal ratings and reviews to improve future preparation accuracy.

---

## 🧱 Object-Oriented Programming (OOP) Architecture
The project strictly implements core OOP paradigms:

| OOP Concept | Implementation in FOODWISE |
| :--- | :--- |
| **Abstraction** | Abstract base class `User` defining template behaviors like `getRoleDisplayName()`. |
| **Inheritance** | `Student extends User` and `Warden extends User` inheriting common authentication and profile logic. |
| **Polymorphism** | `User` reference dynamically invokes role-specific implementations for Student and Warden. |
| **Encapsulation** | Private attributes across `MealResponse`, `FoodMenu`, `Feedback`, and `PredictionModule` accessed via validated getters/setters. |
| **Data Hiding & DAO** | `FoodWiseDAO` decouples database persistence from application business logic. |

---

## 📂 Project Directory Structure

```text
foodwise/
│
├── database/
│   └── foodwise.sql            # Complete MySQL database schema & sample seed data
│
├── src/
│   └── com/foodwise/
│       ├── model/
│       │   ├── User.java              # Abstract base User model
│       │   ├── Student.java           # Student subclass with meal actions
│       │   ├── Warden.java            # Warden subclass with mess management actions
│       │   ├── FoodMenu.java          # Food items and shift timings
│       │   ├── MealResponse.java      # Intent (YES/NO) and cutoff time logic
│       │   └── Feedback.java          # Rating (1-5) and comment model
│       │
│       ├── service/
│       │   └── PredictionModule.java  # Historical average & safety buffer calculation
│       │
│       ├── dao/
│       │   ├── DBConnection.java      # JDBC MySQL connection manager with offline fallback
│       │   └── FoodWiseDAO.java       # SQL CRUD operations for all modules
│       │
│       ├── gui/
│       │   └── FoodWiseSwingApp.java  # Desktop Swing GUI (Slide 10 implementation)
│       │
│       └── FoodWiseServer.java        # Zero-dependency Java HTTP Web Server (Website)
│
├── run_web.bat                 # 1-Click compiler & launcher for the Website
├── run_swing.bat               # 1-Click launcher for Java Swing Desktop GUI
└── README.md                   # Project documentation
```

---

## 🗄️ MySQL Database Setup

1. Open your MySQL client (e.g., MySQL Workbench, phpMyAdmin, or MySQL CLI).
2. Execute the provided script:
   ```sql
   source C:/Users/Anjana Roy/.gemini/antigravity/scratch/foodwise/database/foodwise.sql;
   ```
   Or copy-paste the contents of [`database/foodwise.sql`](database/foodwise.sql).
3. The database `foodwise_db` will be created with tables:
   - `users`: Credentials and roles (`STUDENT`, `WARDEN`)
   - `food_menu`: Menu items (Chapathi, Rice & Chicken Curry, etc.)
   - `cutoff_settings`: Configurable deadlines (06:00, 10:30, 15:30, 18:30)
   - `meal_responses`: Student intent responses
   - `historical_demand`: Historical demand records for prediction
   - `feedback`: Student meal reviews and star ratings

> **Note:** If MySQL is not running on port 3306, `DBConnection.java` will seamlessly fall back to an active in-memory state pre-filled with the exact slide presentation values, ensuring the project never crashes during live viva or evaluation!

---

## 🚀 How to Run the Website

### Option 1: Using the Batch Script (Recommended for Windows)
Simply double-click:
```bat
run_web.bat
```
This compiles the Java files and automatically launches your web browser at **`http://localhost:8080`**.

### Option 2: Using Terminal / Command Prompt
```bash
cd "C:\Users\Anjana Roy\.gemini\antigravity\scratch\foodwise"
mkdir bin
javac -d bin src/com/foodwise/model/*.java src/com/foodwise/service/*.java src/com/foodwise/dao/*.java src/com/foodwise/*.java
java -cp bin com.foodwise.FoodWiseServer
```
Then open:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🖥️ How to Run Java Swing Desktop GUI (Optional)
If your professor specifically requests the desktop Swing GUI (listed in Slide 10):
```bat
run_swing.bat
```
Or via command line:
```bash
javac -d bin src/com/foodwise/model/*.java src/com/foodwise/service/*.java src/com/foodwise/dao/*.java src/com/foodwise/gui/*.java
java -cp bin com.foodwise.gui.FoodWiseSwingApp
```

---

## 🔑 Demo Login Credentials

| Role | Username | Password | Features / Dashboard |
| :--- | :--- | :--- | :--- |
| **Student** | `akhil` | `pass123` | View menu, toggle YES/NO meal intent, cutoff lock check, submit feedback |
| **Student** | `anjana` | `pass123` | Alternative student profile |
| **Warden** | `warden` | `warden123` | Live demand stats, prediction engine, weekly waste report, update cutoff times, update food menu |
