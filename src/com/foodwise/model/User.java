package com.foodwise.model;

/**
 * Base User class demonstrating OOP Abstraction and Encapsulation.
 * Subclasses: Student, Warden (Inheritance)
 */
public abstract class User {
    private String username;
    private String password;
    private String role; // "STUDENT" or "WARDEN"
    private String name;

    public User(String username, String password, String role, String name) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Abstract method demonstrating Polymorphism
    public abstract String getRoleDisplayName();
}
