package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.sql.*;
import java.util.Scanner;

public class Main {

    private static void createAccount(Connection connection,Scanner sc) {
        try {
            System.out.print("Enter your name: ");
            sc.nextLine(); // Consume newline
            String name = sc.nextLine();
            System.out.print("Enter initial deposit amount: ");
            double balance = sc.nextDouble();

            String sql = "INSERT INTO Accounts (Name, Balance) VALUES (?, ?)";
//            PreparedStatement pstmt = connection.prepareStatement(sql);

            PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setDouble(2, balance);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int accountId = generatedKeys.getInt(1); // Get the first generated key
                    System.out.println("Account created successfully! Your Account ID is: " + accountId);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void depositMoney(Connection connection,Scanner sc) {
        try {
            System.out.print("Enter Account ID: ");
            int accId = sc.nextInt();
            System.out.print("Enter amount to deposit: ");
            double amount = sc.nextDouble();

            String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Money deposited successfully!");
            } else {
                System.out.println("Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void withdrawMoney(Connection connection, Scanner sc){
        try{
            System.out.println("Enter account id : ");
            int accId = sc.nextInt();
            System.out.println("Enter amount to be withdrow");
            double amount = sc.nextDouble();

            String query = "select balance from Accounts where id = ? ";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, accId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance >= amount) {
                    String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setDouble(1, amount);
                    pstmt.setInt(2, accId);
                    pstmt.executeUpdate();
                    System.out.println("Money withdrawn successfully!");
                } else {
                    System.out.println("Insufficient balance!");
                }
            } else {
                System.out.println("Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private static void checkBalance(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter account id : ");
            int accId = scanner.nextInt();

            String sql = "Select balance from accounts where id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, accId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                System.out.println("Current balance is : "+rs.getDouble("balance"));
            }
            else{
                System.out.println("Account not fount");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewAccount(Connection connection, Scanner sc){
        try{
            System.out.println("Enter Account id : ");
            int accId = sc.nextInt();
            String sql = "SELECT * from accounts where id = ? ";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, accId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.printf("%-10s %-20s %-10s%n", "  ID", "  Name", "  Balance");
                System.out.printf("%-10s %-20s %-10s%n", "----------", "--------------------", "----------");
                System.out.printf("%-10d %-20s %-10.2f%n", rs.getInt("id"), rs.getString("name"), rs.getDouble("balance"));
            } else {
                System.out.println("Account not found");
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Database URL, username, and password
        String url = "jdbc:mysql://localhost:3306/bankdb"; // Replace <database_name> with your DB name
        String username = "root"; // Replace with your DB username
        String password = "ashu@123"; // Replace with your DB password


        try {

            Connection connection = DriverManager.getConnection(url, username, password);
//            System.out.println("Connection established successfully!");
            System.out.println("Welcome to Bank Application....!");
            Scanner sc = new Scanner(System.in);

            while(true){
                System.out.println("\nMenu:");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Check Balance");
                System.out.println("5. View Account");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        createAccount(connection, sc);
                        break;
                    case 2:
                        depositMoney(connection, sc);
                        break;
                    case 3:
                        withdrawMoney(connection, sc);
                        break;
                    case 4:
                        checkBalance(connection, sc);
                        break;
                    case 5:
                        viewAccount(connection, sc);
                        break;
                    case 6:
                        System.out.println("Thank you for using the bank application!");
                        connection.close();
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            }

        }  catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
