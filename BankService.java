import java.sql.*;
import java.util.Scanner;

public class BankService {
    Scanner sc = new Scanner(System.in);

    // Create New Account
    public void createAccount() {
        System.out.println("=== Create New Account ===");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ Account created successfully!");
            } else {
                System.out.println("❌ Account creation failed.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // User Login
    public int login() {
        System.out.println("=== Login ===");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println("✅ Login successful! Welcome, " + username);
                return userId;
            } else {
                System.out.println("❌ Invalid credentials.");
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return -1;
        }
    }

    // Deposit Money
    public void depositMoney(int userId) {
        System.out.println("=== Deposit Money ===");
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        if (amount <= 0) {
            System.out.println("❌ Amount must be greater than 0.");
            return;
        }

        String updateBalanceSQL = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
        String insertTransactionSQL = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'deposit', ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction

            try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertTransactionSQL)) {

                // Update balance
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                // Insert transaction
                insertStmt.setInt(1, userId);
                insertStmt.setDouble(2, amount);
                insertStmt.executeUpdate();

                conn.commit(); // Commit transaction
                System.out.println("✅ Amount deposited successfully!");

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                System.out.println("❌ Deposit failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Withdraw Money
    public void withdrawMoney(int userId) {
        System.out.println("=== Withdraw Money ===");
        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        if (amount <= 0) {
            System.out.println("❌ Amount must be greater than 0.");
            return;
        }

        String getBalanceSQL = "SELECT balance FROM users WHERE user_id = ?";
        String updateBalanceSQL = "UPDATE users SET balance = balance - ? WHERE user_id = ?";
        String insertTransactionSQL = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'withdraw', ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement getBalanceStmt = conn.prepareStatement(getBalanceSQL)) {
                getBalanceStmt.setInt(1, userId);
                ResultSet rs = getBalanceStmt.executeQuery();

                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");

                    if (currentBalance >= amount) {
                        // Withdraw: update balance
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL);
                             PreparedStatement insertStmt = conn.prepareStatement(insertTransactionSQL)) {

                            updateStmt.setDouble(1, amount);
                            updateStmt.setInt(2, userId);
                            updateStmt.executeUpdate();

                            insertStmt.setInt(1, userId);
                            insertStmt.setDouble(2, amount);
                            insertStmt.executeUpdate();

                            conn.commit();
                            System.out.println("✅ Amount withdrawn successfully!");
                        }
                    } else {
                        System.out.println("❌ Insufficient balance.");
                        conn.rollback();
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("❌ Withdrawal failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // View Transaction History
    public void viewTransactionHistory(int userId) {
        System.out.println("=== Transaction History ===");

        String sql = "SELECT type, amount, timestamp FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            boolean hasTransactions = false;

            while (rs.next()) {
                hasTransactions = true;
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp time = rs.getTimestamp("timestamp");

                System.out.println(type.toUpperCase() + " | Amount: $" + amount + " | Time: " + time);
            }

            if (!hasTransactions) {
                System.out.println("No transactions found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Check Account Balance
    public void checkBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Your current balance is: $" + balance);
            } else {
                System.out.println("❌ User not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
