package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        //did we pass in a username and password
        //if not, the application must die
        if (args.length != 2) {
            //display a message to the user
            System.out.println("Application needs two args to run: A username and a password for the db");
            //exit the app due to failure because we dont have a username and password from the command line
            System.exit(1);
        }

        //get the username and password from args[]
        String username = args[0];
        String password = args[1];
        String url = "jdbc:mysql://localhost:3306/northwind";

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try (Connection connection = dataSource.getConnection()) {
            menuOptions(connection);
        } catch (SQLException e) {
            System.out.println("An oopsie" + e.getMessage());
        }
    }

    public static void menuOptions(Connection connection) {
        boolean run = true;
        while (run) {
            System.out.println("What do you want to do?");
            System.out.println("""
                    1) Display all products
                    2) Display all customers
                    3) Display all categories
                    4) Display products based on category ID
                    0) Exit
                    Select an option:
                    """);
            int menuChoice = scanner.nextInt();
            scanner.nextLine();
            switch (menuChoice) {
                case 1:
                    displayProducts(connection);
                break;
                case 2:
                    displayCustomers(connection);
                    break;
                case 3:
                    displayCategories(connection);
                    break;
                case 4:
                    productCatSearch(connection);
                case 0:
                    run = false;
            }
        }
    }

    public static void displayProducts(Connection connection) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement("""
                    SELECT ProductID,
                    ProductName,
                    UnitPrice,
                    UnitsInStock
                    FROM Products;
                    """);
            ResultSet set = prepStatement.executeQuery();
            printResults(set);

        } catch (SQLException e) {
            System.out.println("An oppsie" +  e.getMessage());
        }
    }

    public static void displayCustomers(Connection connection) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement("""
                    SELECT ContactName,
                    CompanyName,
                    City,
                    Country,
                    Phone
                    FROM Customers
                    ORDER BY Country;
                    """);
            ResultSet set = prepStatement.executeQuery();
            printResults(set);
        } catch (SQLException e) {
            System.out.println("An oopsie" + e.getMessage());

        }
    }

    public static void displayCategories(Connection connection) {
        try
            (PreparedStatement prepStatement = connection.prepareStatement("""
                    SELECT CategoryID,
                    CategoryName
                    FROM
                    Categories
                    ORDER BY CategoryID;
                    """);
            ) {
            ResultSet set = prepStatement.executeQuery();
            printResults(set);
        } catch(SQLException e) {
            System.out.println("Oopsie daisy" + e.getMessage());
        }
    }

    public static void productCatSearch(Connection connection) {
        //prep statement must be in try
        try
            ( PreparedStatement prepStatement = connection.prepareStatement("""
                    SELECT productID,
                    productName,
                    unitPrice,
                    unitsInStock
                    FROM
                    Products p
                    JOIN Categories c ON p.CategoryID = c.CategoryID
                    WHERE p.CategoryID = ?
                    """);
            ){
            System.out.println("Enter the category ID you wish to search for");
            String catID = scanner.nextLine();
            prepStatement.setString(1, catID);
            //result set must be in try
            try(ResultSet set = prepStatement.executeQuery()){
                printResults(set);
            }
        } catch (SQLException e) {
            System.out.println("Woospsie" + " " + e.getMessage());
        }
    }

    public static void printResults(ResultSet results) throws SQLException {
        //gets info about types and properties of columns in result set
        ResultSetMetaData metaData = results.getMetaData();

        int columnCount = metaData.getColumnCount();

        //this is looping over all the results from the DB
        while(results.next()) {

            //loop over each column in the rown and display the data
            for (int i = 1; i <= columnCount; i++) {
                //gets the current colum name
                String columnName = metaData.getColumnName(i);
                //get the current column value
                String value = results.getString(i);
                //print out the column name and column value
                System.out.printf("%-15s: %-20s%n", columnName, value);
            }

            //print an empty line to make the results prettier
            System.out.println("--------------------------------");
        }
    }
}
