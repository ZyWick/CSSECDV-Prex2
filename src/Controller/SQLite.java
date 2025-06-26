package Controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;

import Utils.AppLogger;

public class SQLite {

    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";

    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL,\n"
                + " name TEXT NOT NULL,\n"
                + " stock INTEGER DEFAULT 0,\n"
                + " timestamp TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " event TEXT NOT NULL,\n"
                + " username TEXT NOT NULL,\n"
                + " desc TEXT NOT NULL,\n"
                + " timestamp TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL UNIQUE,\n"
                + " stock INTEGER DEFAULT 0,\n"
                + " price REAL DEFAULT 0.00\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " role INTEGER DEFAULT 2,\n"
                + " locked INTEGER DEFAULT 0,\n"
                + " failed_attempts INTEGER DEFAULT 0,\n"
                + " locked_until INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (SQLException ex) {
            AppLogger.logError("createUserTable failed", ex);
        } 
    }

    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (SQLException ex) {
            AppLogger.logError("dropUserTable failed", ex);
        }
    }

    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','"
                + stock + "','" + timestamp + "')";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event, username, desc, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event);
            pstmt.setString(2, username);
            pstmt.setString(3, desc);
            pstmt.setString(4, timestamp);

            pstmt.executeUpdate();

        } catch (Exception ex) {
            AppLogger.logError("Failed to insert log entry", ex);
        }
    }


    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES('" + name + "','" + stock + "','" + price + "')";

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void addUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            // PREPARED STATEMENT EXAMPLE
            // String sql = "INSERT INTO users(username,password) VALUES(?,?)";
            // PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // pstmt.setString(1, username);
            // pstmt.setString(2, password);
            // pstmt.executeUpdate();
        } catch (SQLException ex) {
            AppLogger.logError("addUser failed", ex);
        }
    }

    public ArrayList<History> getHistory() {
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return histories;
    }

    public ArrayList<Logs> getLogs() {
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                        rs.getString("event"),
                        rs.getString("username"),
                        rs.getString("desc"),
                        rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }

    public ArrayList<Product> getProduct() {
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getFloat("price")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return products;
    }

    public ArrayList<User> getUsers() {
        String sql = "SELECT id, username, password, role, locked FROM users";
        ArrayList<User> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role"),
                        rs.getInt("locked")
                ));
            }

        } catch (SQLException ex) {
            AppLogger.logError("getUsers failed", ex);
        }
        return users;
    }

    public void addUser(String username, String password, int role) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, role);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            AppLogger.logError("addUser failed", ex);
        } 
    }

    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();
            System.out.println("User " + username + " has been deleted.");

        } catch (SQLException ex) {
            AppLogger.logError("removeUser failed", ex);
        }
    }

    public Product getProduct(String name) {
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            product = new Product(rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"));
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return product;
    }

    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        ACCOUNT_LOCKED
    }

    public class LoginResponse {
        public LoginResult result;
        public User user; // null if login fails
        public String message; // optional message (e.g., lockout duration)

        public LoginResponse(SQLite.LoginResult result, User user, String message) {
            this.result = result;
            this.user = user;
            this.message = message;
        }
    }

    public LoginResponse tryLogin(String username, String hashedPassword) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(driverURL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String dbPassword = rs.getString("password");
                int failedAttempts = rs.getInt("failed_attempts");
                long lockedUntil = rs.getLong("locked_until");
                long now = System.currentTimeMillis();
                
                if (now < lockedUntil) {
                    long remainingMillis = lockedUntil - now;
                    int minutes = (int) (remainingMillis / (60 * 1000));
                    int seconds = (int) (remainingMillis / 1000) % 60;
                    String msg = String.format("Account is locked. Try again in %d minute(s) and %d second(s).", minutes, seconds);
                    return new LoginResponse(LoginResult.ACCOUNT_LOCKED, null, msg);
                }
                
                if (dbPassword.equals(hashedPassword)) {
                    resetLoginFailures(conn, id);
                    User user = new User(
                            id,
                            rs.getString("username"),
                            dbPassword,
                            rs.getInt("role"),
                            rs.getInt("locked")
                    );
                    return new LoginResponse(LoginResult.SUCCESS, user, null);
                } else {
                    handleFailedLogin(conn, id, failedAttempts);
                    return new LoginResponse(LoginResult.INVALID_CREDENTIALS, null, null);
                }
            }
        } catch (SQLException ex) {
            AppLogger.logError("tryLogin failed", ex);
        } 
        
        return new LoginResponse(LoginResult.INVALID_CREDENTIALS, null, null);
    }

    private void resetLoginFailures(Connection conn, int userId) {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = 0 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            AppLogger.logError("resetLoginFailures failed", ex);
        }
    }

    private void handleFailedLogin(Connection conn, int userId, int currentAttempts) {
        final int MAX_ATTEMPTS = 5;
        final long LOCK_DURATION_MS = 5 * 60 * 1000;

        int attempts = currentAttempts + 1;
        long lockUntil = (attempts >= MAX_ATTEMPTS) ? System.currentTimeMillis() + LOCK_DURATION_MS : 0;

        String sql = "UPDATE users SET failed_attempts = ?, locked_until = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attempts);
            pstmt.setLong(2, lockUntil);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            AppLogger.logError("handleFailedLogin failed", ex);
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";

        try (Connection conn = DriverManager.getConnection(driverURL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            AppLogger.logError("usernameExists failed", ex);
        } finally {
            return false;
        }
    }
}
