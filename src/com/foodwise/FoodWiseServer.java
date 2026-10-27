package com.foodwise;

import com.foodwise.dao.FoodWiseDAO;
import com.foodwise.model.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FOODWISE - Smart Hostel Meal Management System
 * Pure Java HTTP Web Application Server.
 * Uses standard Java com.sun.net.httpserver.HttpServer (Zero external dependencies).
 */
public class FoodWiseServer {
    private static final int PORT = 8080;
    private static final FoodWiseDAO dao = new FoodWiseDAO();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new LoginHandler());
        server.createContext("/student", new StudentHandler());
        server.createContext("/warden", new WardenHandler());
        server.createContext("/action/intent", new MealIntentHandler());
        server.createContext("/action/feedback", new FeedbackHandler());
        server.createContext("/action/cutoff", new CutoffHandler());
        server.createContext("/action/menu", new MenuUpdateHandler());

        server.setExecutor(null); // default executor
        System.out.println("==================================================================");
        System.out.println("  FOODWISE - Smart Hostel Meal Management System");
        System.out.println("  Server successfully started on: http://localhost:" + PORT);
        System.out.println("  Open your browser and navigate to: http://localhost:" + PORT);
        System.out.println("==================================================================");
        server.start();
    }

    // --- Utility Methods ---

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                try {
                    params.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8.name()),
                               URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name()));
                } catch (Exception ignored) {}
            }
        }
        return params;
    }

    private static Map<String, String> parseFormData(InputStream is) throws IOException {
        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";
        return parseQueryParams(body);
    }

    private static void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    // --- Stylesheet for Clean, Modern Presentation-Matching UI ---

    private static String getCommonStyles() {
        return "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<link href='https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap' rel='stylesheet'>" +
               "<style>" +
               "  * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Plus Jakarta Sans', sans-serif; }" +
               "  body { background-color: #F8FAFC; color: #1E293B; line-height: 1.5; }" +
               "  .navbar { background: #0F172A; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }" +
               "  .navbar-brand { font-size: 1.35rem; font-weight: 800; letter-spacing: 0.05em; display: flex; align-items: center; gap: 0.6rem; }" +
               "  .navbar-brand span { color: #38BDF8; }" +
               "  .user-badge { background: #1E293B; padding: 0.4rem 0.9rem; border-radius: 9999px; font-size: 0.85rem; border: 1px solid #334155; }" +
               "  .container { max-width: 1100px; margin: 2rem auto; padding: 0 1.25rem; }" +
               "  .card { background: white; border-radius: 1rem; border: 1px solid #E2E8F0; padding: 1.75rem; margin-bottom: 1.75rem; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }" +
               "  .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }" +
               "  .grid-4 { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; }" +
               "  .stat-card { background: white; border: 1px solid #E2E8F0; border-radius: 1rem; padding: 1.25rem; border-top: 4px solid #38BDF8; }" +
               "  .stat-card.confirmed { border-top-color: #3B82F6; }" +
               "  .stat-card.expected { border-top-color: #F59E0B; }" +
               "  .stat-card.recommended { border-top-color: #10B981; }" +
               "  .stat-card.status { border-top-color: #6366F1; }" +
               "  .stat-title { font-size: 0.75rem; font-weight: 700; color: #64748B; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.5rem; }" +
               "  .stat-value { font-size: 2rem; font-weight: 800; color: #0F172A; }" +
               "  .stat-sub { font-size: 0.8rem; color: #64748B; margin-top: 0.25rem; }" +
               "  table { width: 100%; border-collapse: collapse; margin-top: 1rem; }" +
               "  th { background: #F1F5F9; color: #475569; font-weight: 700; text-align: left; padding: 0.85rem 1rem; font-size: 0.85rem; text-transform: uppercase; border-bottom: 2px solid #E2E8F0; }" +
               "  td { padding: 1rem; border-bottom: 1px solid #E2E8F0; font-size: 0.95rem; vertical-align: middle; }" +
               "  .btn { display: inline-flex; align-items: center; justify-content: center; padding: 0.6rem 1.25rem; border-radius: 0.5rem; font-weight: 600; text-decoration: none; cursor: pointer; border: none; font-size: 0.9rem; transition: 0.15s ease; }" +
               "  .btn-brown-yes { background: #963D18; color: white; font-size: 1.5rem; font-weight: 800; padding: 1.2rem 2.5rem; border-radius: 0.75rem; border: 3px solid #732D0F; }" +
               "  .btn-brown-yes:hover { background: #7C3011; }" +
               "  .btn-brown-no { background: #963D18; color: white; font-size: 1.5rem; font-weight: 800; padding: 1.2rem 2.5rem; border-radius: 0.75rem; border: 3px solid #732D0F; }" +
               "  .btn-brown-no:hover { background: #7C3011; }" +
               "  .btn-primary { background: #0F172A; color: white; }" +
               "  .btn-primary:hover { background: #1E293B; }" +
               "  .badge { display: inline-block; padding: 0.35rem 0.75rem; border-radius: 9999px; font-size: 0.8rem; font-weight: 700; }" +
               "  .badge-yes { background: #DCFCE7; color: #166534; }" +
               "  .badge-no { background: #FEE2E2; color: #991B1B; }" +
               "  .badge-open { background: #E0E7FF; color: #3730A3; }" +
               "  .badge-closed { background: #F1F5F9; color: #475569; }" +
               "  .notice-banner { background: #FEF3C7; border: 1px solid #FCD34D; color: #92400E; padding: 0.75rem 1rem; border-radius: 0.5rem; font-size: 0.85rem; margin-top: 1rem; font-weight: 500; }" +
               "  .form-group { margin-bottom: 1rem; }" +
               "  .form-group label { display: block; font-size: 0.85rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem; }" +
               "  .form-control { width: 100%; padding: 0.65rem 0.85rem; border: 1px solid #CBD5E1; border-radius: 0.5rem; font-size: 0.95rem; }" +
               "  .feedback-box { border-left: 4px solid #38BDF8; background: #F8FAFC; padding: 1rem; border-radius: 0 0.5rem 0.5rem 0; margin-bottom: 0.75rem; }" +
               "</style>";
    }

    // --- 1. Login Handler ---

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange.getRequestBody());
                String username = form.getOrDefault("username", "").trim();
                String password = form.getOrDefault("password", "").trim();

                User user = dao.authenticate(username, password);
                if (user != null) {
                    if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                        redirect(exchange, "/student?user=" + user.getUsername());
                    } else {
                        redirect(exchange, "/warden?user=" + user.getUsername());
                    }
                    return;
                } else {
                    renderLoginPage(exchange, "Invalid username or password. Please try again.");
                    return;
                }
            }
            renderLoginPage(exchange, null);
        }

        private void renderLoginPage(HttpExchange exchange, String errorMessage) throws IOException {
            String html = "<!DOCTYPE html><html><head><title>FOODWISE - Login</title>" +
                    getCommonStyles() +
                    "</head><body>" +
                    "<div style='min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%); padding: 1.5rem;'>" +
                    "  <div style='background: white; border-radius: 1.25rem; max-width: 460px; width: 100%; padding: 2.5rem; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);'>" +
                    "    <div style='text-align: center; margin-bottom: 1.75rem;'>" +
                    "      <div style='display: inline-block; background: #0F172A; color: #38BDF8; font-size: 1.5rem; font-weight: 900; padding: 0.5rem 1.2rem; border-radius: 0.75rem; letter-spacing: 0.1em; margin-bottom: 0.75rem;'>FOODWISE</div>" +
                    "      <h2 style='font-size: 1.25rem; font-weight: 700; color: #0F172A;'>Smart Hostel Meal Management</h2>" +
                    "      <p style='font-size: 0.85rem; color: #64748B; margin-top: 0.25rem;'>Predict. Prepare. Prevent. Preserve.</p>" +
                    "    </div>" +
                    (errorMessage != null ? "<div style='background: #FEE2E2; border: 1px solid #FCA5A5; color: #991B1B; padding: 0.75rem; border-radius: 0.5rem; font-size: 0.85rem; margin-bottom: 1.25rem; text-align: center; font-weight: 600;'>" + errorMessage + "</div>" : "") +
                    "    <form method='POST' action='/'>" +
                    "      <div class='form-group'>" +
                    "        <label>Username</label>" +
                    "        <input type='text' name='username' class='form-control' placeholder='e.g., akhil or warden' required autofocus>" +
                    "      </div>" +
                    "      <div class='form-group'>" +
                    "        <label>Password</label>" +
                    "        <input type='password' name='password' class='form-control' placeholder='Enter password' required>" +
                    "      </div>" +
                    "      <button type='submit' class='btn btn-primary' style='width: 100%; padding: 0.75rem; font-size: 1rem; margin-top: 0.5rem;'>Sign In</button>" +
                    "    </form>" +
                    "    <div style='margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid #E2E8F0; text-align: center;'>" +
                    "      <p style='font-size: 0.75rem; color: #64748B; font-weight: 600; text-transform: uppercase; margin-bottom: 0.6rem;'>One-Click Demo Credentials</p>" +
                    "      <div style='display: flex; gap: 0.5rem; justify-content: center;'>" +
                    "        <button onclick=\"quickFill('akhil','pass123')\" class='btn' style='background: #EFF6FF; color: #1D4ED8; font-size: 0.8rem; padding: 0.4rem 0.8rem;'>Student (Akhil)</button>" +
                    "        <button onclick=\"quickFill('warden','warden123')\" class='btn' style='background: #F0FDF4; color: #15803D; font-size: 0.8rem; padding: 0.4rem 0.8rem;'>Warden Admin</button>" +
                    "      </div>" +
                    "    </div>" +
                    "    <div style='margin-top: 1.5rem; text-align: center; font-size: 0.72rem; color: #94A3B8;'>" +
                    "      Project Team: Amal Krishna K • Abin Roy • Andriya Aby • Anjana Roy" +
                    "    </div>" +
                    "  </div>" +
                    "</div>" +
                    "<script>" +
                    "  function quickFill(u, p) {" +
                    "    document.querySelector('input[name=username]').value = u;" +
                    "    document.querySelector('input[name=password]').value = p;" +
                    "  }" +
                    "</script>" +
                    "</body></html>";
            sendHtml(exchange, 200, html);
        }
    }

    // --- 2. Student Dashboard Handler (Replicating Slide 12) ---

    static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
            String username = query.getOrDefault("user", "akhil");
            User user = dao.authenticate(username, "pass123"); // retrieval
            if (user == null || !(user instanceof Student)) {
                user = new Student(username, "pass123", "Akhil Menon", "STU101");
            }
            Student student = (Student) user;

            List<FoodMenu> menus = dao.getAllMenus();
            Map<String, String> cutoffs = dao.getCutoffTimes();

            // Find current upcoming meal (e.g. Breakfast)
            FoodMenu upcomingMeal = menus.isEmpty() ? new FoodMenu("Breakfast", "Chapathi • Potato Curry • Coffee", "07:30 AM") : menus.get(0);
            String upcomingIntent = dao.getMealIntentStatus(student.getStudentId(), upcomingMeal.getMealType());
            String upcomingCutoff = cutoffs.getOrDefault(upcomingMeal.getMealType(), "06:00");

            MealResponse upcomingResponseObj = new MealResponse(student.getStudentId(), upcomingMeal.getMealType(), upcomingIntent, "", upcomingCutoff);
            boolean isCancellationOpen = upcomingResponseObj.isCancellationOpen();

            StringBuilder tableRows = new StringBuilder();
            for (FoodMenu m : menus) {
                String intent = dao.getMealIntentStatus(student.getStudentId(), m.getMealType());
                String cutoffTime = cutoffs.getOrDefault(m.getMealType(), "10:30");
                MealResponse resp = new MealResponse(student.getStudentId(), m.getMealType(), intent, "", cutoffTime);
                boolean canChange = resp.isCancellationOpen();

                String statusBadge = "YES".equalsIgnoreCase(intent) ?
                        "<span class='badge badge-yes'>YES</span>" :
                        "<span class='badge badge-no'>NO</span>";

                String actionButtons;
                if (canChange) {
                    actionButtons = "<form method='POST' action='/action/intent' style='display:inline; margin-right: 4px;'>" +
                            "<input type='hidden' name='user' value='" + student.getUsername() + "'>" +
                            "<input type='hidden' name='mealType' value='" + m.getMealType() + "'>" +
                            "<input type='hidden' name='status' value='YES'>" +
                            "<button type='submit' class='btn' style='background:" + ("YES".equalsIgnoreCase(intent) ? "#10B981; color:white;" : "#E2E8F0; color:#334155;") + " padding: 0.35rem 0.75rem; font-size: 0.8rem;'>YES</button>" +
                            "</form>" +
                            "<form method='POST' action='/action/intent' style='display:inline;'>" +
                            "<input type='hidden' name='user' value='" + student.getUsername() + "'>" +
                            "<input type='hidden' name='mealType' value='" + m.getMealType() + "'>" +
                            "<input type='hidden' name='status' value='NO'>" +
                            "<button type='submit' class='btn' style='background:" + ("NO".equalsIgnoreCase(intent) ? "#EF4444; color:white;" : "#E2E8F0; color:#334155;") + " padding: 0.35rem 0.75rem; font-size: 0.8rem;'>NO</button>" +
                            "</form>";
                } else {
                    actionButtons = "<span class='badge badge-closed' title='Cutoff time has passed'>🔒 Locked (" + cutoffTime + ")</span>";
                }

                tableRows.append("<tr>")
                        .append("<td><strong>").append(m.getMealType()).append("</strong><div style='font-size:0.75rem; color:#64748B;'>").append(m.getMealTime()).append("</div></td>")
                        .append("<td>").append(m.getFood()).append("</td>")
                        .append("<td>").append(statusBadge).append("</td>")
                        .append("<td>").append(actionButtons).append("</td>")
                        .append("</tr>");
            }

            String html = "<!DOCTYPE html><html><head><title>FOODWISE • Student Dashboard</title>" +
                    getCommonStyles() +
                    "</head><body>" +
                    "<nav class='navbar'>" +
                    "  <div class='navbar-brand'>FOODWISE <span>•</span> Student Dashboard</div>" +
                    "  <div style='display: flex; align-items: center; gap: 1rem;'>" +
                    "    <div class='user-badge'>Student: <strong>" + student.getName() + "</strong> (" + student.getStudentId() + ")</div>" +
                    "    <a href='/' class='btn' style='background: #334155; color: white; padding: 0.4rem 0.8rem; font-size: 0.8rem;'>Logout</a>" +
                    "  </div>" +
                    "</nav>" +

                    "<div class='container'>" +

                    // UPCOMING MEAL Card (Direct reproduction of Slide 12)
                    "  <div class='card' style='text-align: center; border-top: 5px solid #963D18;'>" +
                    "    <div style='font-size: 0.85rem; font-weight: 700; color: #963D18; text-transform: uppercase; letter-spacing: 0.1em;'>UPCOMING MEAL</div>" +
                    "    <h1 style='font-size: 2.25rem; font-weight: 800; color: #0F172A; margin: 0.25rem 0;'>" + upcomingMeal.getMealType() + "</h1>" +
                    "    <p style='font-size: 1.1rem; color: #475569; font-weight: 500; margin-bottom: 1.5rem;'>Food: <strong>" + upcomingMeal.getFood() + "</strong></p>" +

                    "    <div style='display: flex; justify-content: center; gap: 2rem; margin-bottom: 1.5rem;'>" +
                    (isCancellationOpen ?
                            "      <form method='POST' action='/action/intent'>" +
                            "        <input type='hidden' name='user' value='" + student.getUsername() + "'>" +
                            "        <input type='hidden' name='mealType' value='" + upcomingMeal.getMealType() + "'>" +
                            "        <input type='hidden' name='status' value='YES'>" +
                            "        <button type='submit' class='btn btn-brown-yes' style='" + ("YES".equalsIgnoreCase(upcomingIntent) ? "box-shadow: 0 0 0 4px #F59E0B;" : "opacity: 0.7;") + "'>YES</button>" +
                            "      </form>" +
                            "      <form method='POST' action='/action/intent'>" +
                            "        <input type='hidden' name='user' value='" + student.getUsername() + "'>" +
                            "        <input type='hidden' name='mealType' value='" + upcomingMeal.getMealType() + "'>" +
                            "        <input type='hidden' name='status' value='NO'>" +
                            "        <button type='submit' class='btn btn-brown-no' style='" + ("NO".equalsIgnoreCase(upcomingIntent) ? "box-shadow: 0 0 0 4px #EF4444;" : "opacity: 0.7;") + "'>NO</button>" +
                            "      </form>" :
                            "      <div style='background: #F1F5F9; padding: 1.5rem 3rem; border-radius: 1rem; border: 2px dashed #CBD5E1; color: #64748B; font-weight: 700;'>" +
                            "        🔒 Cancellation Closed for " + upcomingMeal.getMealType() + " (Past cutoff time: " + upcomingCutoff + ")" +
                            "      </div>"
                    ) +
                    "    </div>" +

                    "    <div class='notice-banner'>" +
                    "      " + upcomingMeal.getMealType() + " cancellation closes at " + upcomingCutoff + " · Changes allowed before cutoff." +
                    "    </div>" +

                    "    <div style='margin-top: 1.25rem; font-size: 1rem; font-weight: 700; color: #334155;'>" +
                    "      CURRENT STATUS: <span style='color: " + ("YES".equalsIgnoreCase(upcomingIntent) ? "#166534" : "#991B1B") + "; font-size: 1.2rem;'>" + upcomingIntent + "</span>" +
                    "    </div>" +
                    "  </div>" +

                    // UPCOMING MEALS Table (Slide 12 Table)
                    "  <div class='card'>" +
                    "    <h3 style='font-size: 1.25rem; font-weight: 700; color: #0F172A;'>UPCOMING MEALS</h3>" +
                    "    <p style='font-size: 0.85rem; color: #64748B; margin-top: 0.2rem;'>Set meal intent for today's scheduled institutional menu.</p>" +
                    "    <table>" +
                    "      <thead>" +
                    "        <tr>" +
                    "          <th>Meal</th>" +
                    "          <th>Food</th>" +
                    "          <th>Status</th>" +
                    "          <th>Action / Lock Status</th>" +
                    "        </tr>" +
                    "      </thead>" +
                    "      <tbody>" +
                    tableRows.toString() +
                    "      </tbody>" +
                    "    </table>" +
                    "  </div>" +

                    // Student Feedback Section
                    "  <div class='card'>" +
                    "    <h3 style='font-size: 1.25rem; font-weight: 700; color: #0F172A;'>Give Post-Meal Feedback</h3>" +
                    "    <p style='font-size: 0.85rem; color: #64748B; margin-top: 0.2rem;'>Your feedback directly feeds into the historical food preparation prediction algorithm.</p>" +
                    "    <form method='POST' action='/action/feedback' style='margin-top: 1.25rem;'>" +
                    "      <input type='hidden' name='user' value='" + student.getUsername() + "'>" +
                    "      <div class='grid-2'>" +
                    "        <div class='form-group'>" +
                    "          <label>Meal Type</label>" +
                    "          <select name='mealType' class='form-control'>" +
                    "            <option value='Breakfast'>Breakfast</option>" +
                    "            <option value='Lunch'>Lunch</option>" +
                    "            <option value='Tea/Coffee'>Tea/Coffee</option>" +
                    "            <option value='Dinner'>Dinner</option>" +
                    "          </select>" +
                    "        </div>" +
                    "        <div class='form-group'>" +
                    "          <label>Quality Rating (1 to 5 Stars)</label>" +
                    "          <select name='rating' class='form-control'>" +
                    "            <option value='5'>★★★★★ (5 - Excellent)</option>" +
                    "            <option value='4'>★★★★☆ (4 - Good)</option>" +
                    "            <option value='3'>★★★☆☆ (3 - Average)</option>" +
                    "            <option value='2'>★★☆☆☆ (2 - Below Average)</option>" +
                    "            <option value='1'>★☆☆☆☆ (1 - Poor)</option>" +
                    "          </select>" +
                    "        </div>" +
                    "      </div>" +
                    "      <div class='form-group'>" +
                    "        <label>Comments / Taste & Quantity Review</label>" +
                    "        <textarea name='comment' class='form-control' rows='3' placeholder='Share taste, quantity sufficiency, or wastage notes...' required></textarea>" +
                    "      </div>" +
                    "      <button type='submit' class='btn btn-primary'>Submit Meal Feedback</button>" +
                    "    </form>" +
                    "  </div>" +

                    "</div>" +
                    "</body></html>";

            sendHtml(exchange, 200, html);
        }
    }

    // --- 3. Warden Dashboard Handler (Replicating Slide 13) ---

    static class WardenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<FoodWiseDAO.DemandSummary> demandList = dao.getDemandSummary();
            List<Feedback> feedbackList = dao.getAllFeedback();
            List<FoodMenu> menus = dao.getAllMenus();
            Map<String, String> cutoffs = dao.getCutoffTimes();

            // Find lunch for the top highlight cards matching Slide 13
            FoodWiseDAO.DemandSummary lunch = demandList.stream()
                    .filter(d -> "Lunch".equalsIgnoreCase(d.mealType))
                    .findFirst()
                    .orElse(demandList.isEmpty() ? new FoodWiseDAO.DemandSummary("Lunch", 320, 335, 340, "10:30", "Cancellation Open") : demandList.get(0));

            StringBuilder demandRows = new StringBuilder();
            for (FoodWiseDAO.DemandSummary d : demandList) {
                String statusBadge = "Cancellation Open".equalsIgnoreCase(d.status) ?
                        "<span class='badge badge-open'>Cancellation Open</span>" :
                        "<span class='badge badge-closed'>Locked</span>";

                demandRows.append("<tr>")
                        .append("<td><strong>").append(d.mealType).append("</strong></td>")
                        .append("<td><span style='font-size:1.1rem; font-weight:700; color:#2563EB;'>").append(d.confirmed).append("</span></td>")
                        .append("<td><span style='font-size:1.1rem; font-weight:700; color:#D97706;'>").append(d.expected).append("</span></td>")
                        .append("<td><span style='font-size:1.1rem; font-weight:800; color:#059669;'>").append(d.recommended).append("</span></td>")
                        .append("<td>").append(d.cutoff).append("</td>")
                        .append("<td>").append(statusBadge).append("</td>")
                        .append("</tr>");
            }

            StringBuilder feedbackRows = new StringBuilder();
            for (Feedback fb : feedbackList) {
                feedbackRows.append("<div class='feedback-box'>")
                        .append("<div style='display:flex; justify-content:space-between; align-items:center; margin-bottom:0.25rem;'>")
                        .append("<strong>").append(fb.getStudentName()).append(" (").append(fb.getStudentId()).append(")</strong>")
                        .append("<span style='color:#F59E0B; font-weight:700;'>").append("★".repeat(fb.getRating())).append("☆".repeat(5 - fb.getRating())).append("</span>")
                        .append("</div>")
                        .append("<div style='font-size:0.8rem; color:#64748B; margin-bottom:0.4rem;'>Meal: ").append(fb.getMealType()).append(" • Date: ").append(fb.getDate()).append("</div>")
                        .append("<div style='font-size:0.9rem; color:#334155;'>\"").append(fb.getComment()).append("\"</div>")
                        .append("</div>");
            }

            String html = "<!DOCTYPE html><html><head><title>FOODWISE • Warden Dashboard</title>" +
                    getCommonStyles() +
                    "</head><body>" +
                    "<nav class='navbar'>" +
                    "  <div class='navbar-brand'>FOODWISE <span>•</span> Warden Dashboard</div>" +
                    "  <div style='display: flex; align-items: center; gap: 1rem;'>" +
                    "    <div class='user-badge'>Role: <strong>Hostel Warden / Mess Admin</strong></div>" +
                    "    <a href='/' class='btn' style='background: #334155; color: white; padding: 0.4rem 0.8rem; font-size: 0.8rem;'>Logout</a>" +
                    "  </div>" +
                    "</nav>" +

                    "<div class='container'>" +

                    // Top 4 Stat Cards directly matching Slide 13
                    "  <div class='grid-4' style='margin-bottom: 1.75rem;'>" +
                    "    <div class='stat-card confirmed'>" +
                    "      <div class='stat-title'>CONFIRMED LUNCH</div>" +
                    "      <div class='stat-value' style='color:#2563EB;'>" + lunch.confirmed + "</div>" +
                    "      <div class='stat-sub'>Students submitted YES</div>" +
                    "    </div>" +
                    "    <div class='stat-card expected'>" +
                    "      <div class='stat-title'>EXPECTED DEMAND</div>" +
                    "      <div class='stat-value' style='color:#D97706;'>" + lunch.expected + "</div>" +
                    "      <div class='stat-sub'>Prediction model estimate</div>" +
                    "    </div>" +
                    "    <div class='stat-card recommended'>" +
                    "      <div class='stat-title'>RECOMMENDED</div>" +
                    "      <div class='stat-value' style='color:#059669;'>" + lunch.recommended + "</div>" +
                    "      <div class='stat-sub'>Ideal kitchen preparation count</div>" +
                    "    </div>" +
                    "    <div class='stat-card status'>" +
                    "      <div class='stat-title'>STATUS</div>" +
                    "      <div class='stat-value' style='font-size: 1.45rem; color:#4F46E5; padding-top: 0.35rem;'>" + lunch.status + "</div>" +
                    "      <div class='stat-sub'>Lunch Cutoff: " + lunch.cutoff + "</div>" +
                    "    </div>" +
                    "  </div>" +

                    // MEAL DEMAND · TABLE (Slide 13)
                    "  <div class='card'>" +
                    "    <div style='display: flex; justify-content: space-between; align-items: center;'>" +
                    "      <div>" +
                    "        <h3 style='font-size: 1.25rem; font-weight: 700; color: #0F172A;'>MEAL DEMAND · LIVE VALUES</h3>" +
                    "        <p style='font-size: 0.85rem; color: #64748B;'>Confirmed vs Predicted vs Recommended meal counts across institutional kitchen shifts.</p>" +
                    "      </div>" +
                    "      <span class='badge' style='background:#DCFCE7; color:#166534;'>Active Strength: 500 Students</span>" +
                    "    </div>" +
                    "    <table>" +
                    "      <thead>" +
                    "        <tr>" +
                    "          <th>Meal</th>" +
                    "          <th>Confirmed</th>" +
                    "          <th>Expected</th>" +
                    "          <th>Recommended</th>" +
                    "          <th>Cutoff Time</th>" +
                    "          <th>Status</th>" +
                    "        </tr>" +
                    "      </thead>" +
                    "      <tbody>" +
                    demandRows.toString() +
                    "      </tbody>" +
                    "    </table>" +
                    "  </div>" +

                    // Prediction Insights & Weekly Waste Report (Slide 13)
                    "  <div class='grid-2'>" +
                    "    <div class='card'>" +
                    "      <h4 style='font-size: 1.1rem; font-weight: 700; color: #0F172A; margin-bottom: 0.5rem;'>Demand Prediction · Lunch</h4>" +
                    "      <p style='font-size: 1.2rem; font-weight: 800; color: #1E293B; margin-bottom: 0.75rem;'>" +
                    "        " + lunch.confirmed + " confirmed · " + lunch.expected + " expected · " + lunch.recommended + " recommended" +
                    "      </p>" +
                    "      <p style='font-size: 0.85rem; color: #64748B; line-height: 1.6;'>" +
                    "        <strong>Prediction Basis:</strong><br>" +
                    "        • Previous similar days average<br>" +
                    "        • Day of week patterns<br>" +
                    "        • Student cancellation velocity<br>" +
                    "        • Academic calendar (Holidays / exam days)<br>" +
                    "        • Special hostel events" +
                    "      </p>" +
                    "    </div>" +
                    "    <div class='card' style='background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%); color: white;'>" +
                    "      <h4 style='font-size: 1.1rem; font-weight: 700; color: #38BDF8; margin-bottom: 0.5rem;'>Weekly Waste Report</h4>" +
                    "      <p style='font-size: 0.85rem; color: #94A3B8; margin-bottom: 1rem;'>Measured kitchen excess post-implementation</p>" +
                    "      <div style='display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.75rem; border-bottom: 1px solid #334155; padding-bottom: 0.5rem;'>" +
                    "        <span style='color: #CBD5E1;'>Previous week</span>" +
                    "        <span style='font-size: 1.25rem; font-weight: 700;'>51 kg</span>" +
                    "      </div>" +
                    "      <div style='display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.75rem; border-bottom: 1px solid #334155; padding-bottom: 0.5rem;'>" +
                    "        <span style='color: #CBD5E1;'>This week</span>" +
                    "        <span style='font-size: 1.25rem; font-weight: 700; color: #34D399;'>42 kg</span>" +
                    "      </div>" +
                    "      <div style='display: flex; justify-content: space-between; align-items: baseline; padding-top: 0.25rem;'>" +
                    "        <span style='color: #38BDF8; font-weight: 700;'>Total Reduction</span>" +
                    "        <span style='font-size: 1.4rem; font-weight: 800; color: #38BDF8;'>9 kg · 17.6% less waste</span>" +
                    "      </div>" +
                    "    </div>" +
                    "  </div>" +

                    // Warden Controls: Cutoff Times & Menu Management
                    "  <div class='grid-2'>" +
                    "    <div class='card'>" +
                    "      <h4 style='font-size: 1.1rem; font-weight: 700; color: #0F172A; margin-bottom: 1rem;'>Smart Cancellation Cutoffs</h4>" +
                    "      <form method='POST' action='/action/cutoff'>" +
                    "        <div class='form-group'>" +
                    "          <label>Meal Type</label>" +
                    "          <select name='mealType' class='form-control'>" +
                    "            <option value='Breakfast'>Breakfast</option>" +
                    "            <option value='Lunch'>Lunch</option>" +
                    "            <option value='Tea/Coffee'>Tea/Coffee</option>" +
                    "            <option value='Dinner'>Dinner</option>" +
                    "          </select>" +
                    "        </div>" +
                    "        <div class='form-group'>" +
                    "          <label>New Cutoff Time (24h format HH:mm)</label>" +
                    "          <input type='text' name='cutoffTime' class='form-control' placeholder='e.g., 06:00, 10:30' required>" +
                    "        </div>" +
                    "        <button type='submit' class='btn btn-primary'>Update Cutoff Time</button>" +
                    "      </form>" +
                    "    </div>" +
                    "    <div class='card'>" +
                    "      <h4 style='font-size: 1.1rem; font-weight: 700; color: #0F172A; margin-bottom: 1rem;'>Manage Food Menu Items</h4>" +
                    "      <form method='POST' action='/action/menu'>" +
                    "        <div class='form-group'>" +
                    "          <label>Meal Type</label>" +
                    "          <select name='mealType' class='form-control'>" +
                    "            <option value='Breakfast'>Breakfast</option>" +
                    "            <option value='Lunch'>Lunch</option>" +
                    "            <option value='Tea/Coffee'>Tea/Coffee</option>" +
                    "            <option value='Dinner'>Dinner</option>" +
                    "          </select>" +
                    "        </div>" +
                    "        <div class='form-group'>" +
                    "          <label>Food Items Description</label>" +
                    "          <input type='text' name='food' class='form-control' placeholder='e.g., Chapathi • Potato Curry • Coffee' required>" +
                    "        </div>" +
                    "        <button type='submit' class='btn btn-primary'>Update Food Menu</button>" +
                    "      </form>" +
                    "    </div>" +
                    "  </div>" +

                    // Student Feedback Log
                    "  <div class='card'>" +
                    "    <h4 style='font-size: 1.1rem; font-weight: 700; color: #0F172A; margin-bottom: 1rem;'>Student Feedback & Rating Log</h4>" +
                    "    <div>" +
                    feedbackRows.toString() +
                    "    </div>" +
                    "  </div>" +

                    "</div>" +
                    "</body></html>";

            sendHtml(exchange, 200, html);
        }
    }

    // --- Action Handlers ---

    static class MealIntentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange.getRequestBody());
                String username = form.getOrDefault("user", "akhil");
                String mealType = form.getOrDefault("mealType", "Breakfast");
                String status = form.getOrDefault("status", "YES");

                User user = dao.authenticate(username, "pass123");
                String stuId = (user instanceof Student) ? ((Student) user).getStudentId() : "STU101";

                dao.updateMealIntent(stuId, mealType, status);
                redirect(exchange, "/student?user=" + username);
                return;
            }
            redirect(exchange, "/student");
        }
    }

    static class FeedbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange.getRequestBody());
                String username = form.getOrDefault("user", "akhil");
                String mealType = form.getOrDefault("mealType", "Breakfast");
                int rating = Integer.parseInt(form.getOrDefault("rating", "5"));
                String comment = form.getOrDefault("comment", "").trim();

                User user = dao.authenticate(username, "pass123");
                String stuId = (user instanceof Student) ? ((Student) user).getStudentId() : "STU101";
                String stuName = (user != null) ? user.getName() : "Student";

                dao.addFeedback(new Feedback(stuId, stuName, mealType, rating, comment, java.time.LocalDate.now().toString()));
                redirect(exchange, "/student?user=" + username);
                return;
            }
            redirect(exchange, "/student");
        }
    }

    static class CutoffHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange.getRequestBody());
                String mealType = form.getOrDefault("mealType", "Breakfast");
                String cutoffTime = form.getOrDefault("cutoffTime", "06:00").trim();

                dao.updateCutoffTime(mealType, cutoffTime);
                redirect(exchange, "/warden");
                return;
            }
            redirect(exchange, "/warden");
        }
    }

    static class MenuUpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseFormData(exchange.getRequestBody());
                String mealType = form.getOrDefault("mealType", "Breakfast");
                String food = form.getOrDefault("food", "").trim();

                dao.updateMenu(mealType, food);
                redirect(exchange, "/warden");
                return;
            }
            redirect(exchange, "/warden");
        }
    }
}
