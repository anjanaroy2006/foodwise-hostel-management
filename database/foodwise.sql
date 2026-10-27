-- ========================================================
-- FOODWISE: Smart Hostel Meal Management System
-- Database Schema & Sample Data
-- Team: Amal Krishna K, Abin Roy, Andriya Aby, Anjana Roy
-- ========================================================

CREATE DATABASE IF NOT EXISTS foodwise_db;
USE foodwise_db;

-- 1. Users Table (Supports User, Student, and Warden inheritance)
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS meal_responses;
DROP TABLE IF EXISTS historical_demand;
DROP TABLE IF EXISTS food_menu;
DROP TABLE IF EXISTS cutoff_settings;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'WARDEN') NOT NULL,
    name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Food Menu Table
CREATE TABLE food_menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    meal_type ENUM('Breakfast', 'Lunch', 'Tea/Coffee', 'Dinner') UNIQUE NOT NULL,
    food_items VARCHAR(255) NOT NULL,
    meal_time VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Cutoff Settings Table (Configurable by Warden)
CREATE TABLE cutoff_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    meal_type ENUM('Breakfast', 'Lunch', 'Tea/Coffee', 'Dinner') UNIQUE NOT NULL,
    cutoff_time TIME NOT NULL,
    display_time VARCHAR(20) NOT NULL
);

-- 4. Meal Intent / Response Table
CREATE TABLE meal_responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    meal_type ENUM('Breakfast', 'Lunch', 'Tea/Coffee', 'Dinner') NOT NULL,
    status ENUM('YES', 'NO') NOT NULL DEFAULT 'YES',
    date DATE NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_student_meal_date (student_id, meal_type, date)
);

-- 5. Historical Demand Table (Used by Prediction Module)
CREATE TABLE historical_demand (
    id INT AUTO_INCREMENT PRIMARY KEY,
    meal_type ENUM('Breakfast', 'Lunch', 'Tea/Coffee', 'Dinner') NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    actual_count INT NOT NULL,
    date DATE NOT NULL
);

-- 6. Feedback Table
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    meal_type ENUM('Breakfast', 'Lunch', 'Tea/Coffee', 'Dinner') NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================================
-- Seed Data matching presentation slides
-- ========================================================

-- Insert Sample Users
INSERT INTO users (username, password, role, name, student_id) VALUES
('akhil', 'pass123', 'STUDENT', 'Akhil Menon', 'STU101'),
('anjana', 'pass123', 'STUDENT', 'Anjana Roy', 'STU102'),
('amal', 'pass123', 'STUDENT', 'Amal Krishna K', 'STU103'),
('abin', 'pass123', 'STUDENT', 'Abin Roy', 'STU104'),
('andriya', 'pass123', 'STUDENT', 'Andriya Aby', 'STU105'),
('warden', 'warden123', 'WARDEN', 'Chief Warden', NULL);

-- Insert Food Menu (from Slide 12)
INSERT INTO food_menu (meal_type, food_items, meal_time) VALUES
('Breakfast', 'Chapathi • Potato Curry • Coffee', '07:30 AM - 09:00 AM'),
('Lunch', 'Rice • Chicken Curry • Salad', '12:30 PM - 02:00 PM'),
('Tea/Coffee', 'Tea • Snack', '04:30 PM - 05:30 PM'),
('Dinner', 'Chapathi • Vegetable Curry', '07:30 PM - 09:00 PM');

-- Insert Configurable Cutoff Times (from Slides)
INSERT INTO cutoff_settings (meal_type, cutoff_time, display_time) VALUES
('Breakfast', '06:00:00', '6:00 AM'),
('Lunch', '10:30:00', '10:30 AM'),
('Tea/Coffee', '15:30:00', '3:30 PM'),
('Dinner', '18:30:00', '6:30 PM');

-- Insert Akhil Menon's Current Status (from Slide 12)
INSERT INTO meal_responses (student_id, meal_type, status, date) VALUES
('STU101', 'Breakfast', 'YES', CURDATE()),
('STU101', 'Lunch', 'NO', CURDATE()),
('STU101', 'Tea/Coffee', 'YES', CURDATE()),
('STU101', 'Dinner', 'YES', CURDATE());

-- Insert Historical Demand for prediction averaging
INSERT INTO historical_demand (meal_type, day_of_week, actual_count, date) VALUES
('Breakfast', 'Monday', 440, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
('Breakfast', 'Tuesday', 450, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('Lunch', 'Monday', 330, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
('Lunch', 'Tuesday', 340, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('Tea/Coffee', 'Monday', 390, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
('Tea/Coffee', 'Tuesday', 395, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
('Dinner', 'Monday', 420, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
('Dinner', 'Tuesday', 425, DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- Insert Sample Feedback
INSERT INTO feedback (student_id, student_name, meal_type, rating, comment, date) VALUES
('STU101', 'Akhil Menon', 'Breakfast', 5, 'Chapathi was hot and soft, curry was delicious!', CURDATE()),
('STU102', 'Anjana Roy', 'Dinner', 4, 'Good vegetable curry, but chapathi got over a bit early.', CURDATE()),
('STU103', 'Amal Krishna K', 'Lunch', 5, 'Chicken curry was very flavorful. Perfect quantity!', CURDATE());
