package com.foodwise.gui;

import com.foodwise.dao.FoodWiseDAO;
import com.foodwise.model.FoodMenu;
import com.foodwise.model.MealResponse;
import com.foodwise.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Java Swing GUI implementation of FOODWISE (Slide 10 specification).
 * Demonstrates reusability of OOP model and DAO architecture across Web & Desktop GUI.
 */
public class FoodWiseSwingApp extends JFrame {

    private final FoodWiseDAO dao = new FoodWiseDAO();
    private final Student currentStudent = new Student("akhil", "pass123", "Akhil Menon", "STU101");

    public FoodWiseSwingApp() {
        setTitle("FOODWISE - Smart Hostel Meal Management System (Swing GUI)");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Student Dashboard (Slide 12)", createStudentPanel());
        tabbedPane.addTab("Warden Dashboard (Slide 13)", createWardenPanel());

        add(tabbedPane);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("FOODWISE • Student Dashboard", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel subtitle = new JLabel("Student: Akhil Menon | Role: Student", JLabel.LEFT);
        subtitle.setForeground(Color.GRAY);
        headerPanel.add(title);
        headerPanel.add(subtitle);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Upcoming Meal Card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createTitledBorder("UPCOMING MEAL: Breakfast"));

        JLabel foodLabel = new JLabel("Food: Chapathi • Potato Curry • Coffee");
        foodLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        foodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton yesBtn = new JButton("YES");
        yesBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
        yesBtn.setBackground(new Color(150, 61, 24));
        yesBtn.setForeground(Color.WHITE);
        yesBtn.setPreferredSize(new Dimension(140, 60));

        JButton noBtn = new JButton("NO");
        noBtn.setFont(new Font("SansSerif", Font.BOLD, 22));
        noBtn.setBackground(new Color(150, 61, 24));
        noBtn.setForeground(Color.WHITE);
        noBtn.setPreferredSize(new Dimension(140, 60));

        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);

        JLabel cutoffLabel = new JLabel("Breakfast cancellation closes at 6:00 AM · Changes allowed before cutoff.");
        cutoffLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        cutoffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("CURRENT STATUS: YES");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(new Color(22, 101, 52));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(foodLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(btnPanel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(cutoffLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Upcoming Meals Table
        String[] cols = {"Meal", "Food", "Status"};
        List<FoodMenu> menus = dao.getAllMenus();
        Object[][] data = new Object[menus.size()][3];
        for (int i = 0; i < menus.size(); i++) {
            FoodMenu m = menus.get(i);
            data[i][0] = m.getMealType();
            data[i][1] = m.getFood();
            data[i][2] = dao.getMealIntentStatus(currentStudent.getStudentId(), m.getMealType());
        }

        JTable table = new JTable(new DefaultTableModel(data, cols));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("UPCOMING MEALS"));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(cardPanel);
        centerPanel.add(scrollPane);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Actions
        yesBtn.addActionListener(e -> {
            dao.updateMealIntent(currentStudent.getStudentId(), "Breakfast", "YES");
            statusLabel.setText("CURRENT STATUS: YES");
            statusLabel.setForeground(new Color(22, 101, 52));
            table.setValueAt("YES", 0, 2);
            JOptionPane.showMessageDialog(this, "Intent recorded: Attending Breakfast!");
        });

        noBtn.addActionListener(e -> {
            dao.updateMealIntent(currentStudent.getStudentId(), "Breakfast", "NO");
            statusLabel.setText("CURRENT STATUS: NO (Cancelled)");
            statusLabel.setForeground(new Color(153, 27, 27));
            table.setValueAt("NO", 0, 2);
            JOptionPane.showMessageDialog(this, "Breakfast cancelled successfully before cutoff.");
        });

        return panel;
    }

    private JPanel createWardenPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Summary Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.add(createStatCard("CONFIRMED LUNCH", "320", Color.BLUE));
        statsPanel.add(createStatCard("EXPECTED DEMAND", "335", new Color(217, 119, 6)));
        statsPanel.add(createStatCard("RECOMMENDED", "340", new Color(5, 150, 105)));
        statsPanel.add(createStatCard("STATUS", "Open", new Color(79, 70, 229)));
        panel.add(statsPanel, BorderLayout.NORTH);

        // Demand Table
        String[] cols = {"Meal", "Confirmed", "Expected", "Recommended", "Cutoff Time", "Status"};
        List<FoodWiseDAO.DemandSummary> list = dao.getDemandSummary();
        Object[][] data = new Object[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            FoodWiseDAO.DemandSummary d = list.get(i);
            data[i][0] = d.mealType;
            data[i][1] = d.confirmed;
            data[i][2] = d.expected;
            data[i][3] = d.recommended;
            data[i][4] = d.cutoff;
            data[i][5] = d.status;
        }

        JTable table = new JTable(new DefaultTableModel(data, cols));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("MEAL DEMAND · LIVE VALUES"));

        // Weekly Waste Report
        JPanel wastePanel = new JPanel(new GridLayout(3, 1));
        wastePanel.setBorder(BorderFactory.createTitledBorder("Weekly Waste Report"));
        wastePanel.add(new JLabel("Previous week: 51 kg"));
        wastePanel.add(new JLabel("This week: 42 kg"));
        wastePanel.add(new JLabel("Reduced: 9 kg · 17.6% less waste"));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(wastePanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String val, Color color) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JLabel t = new JLabel(title, JLabel.CENTER);
        t.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel v = new JLabel(val, JLabel.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 20));
        v.setForeground(color);
        p.add(t);
        p.add(v);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FoodWiseSwingApp app = new FoodWiseSwingApp();
            app.setVisible(true);
        });
    }
}
