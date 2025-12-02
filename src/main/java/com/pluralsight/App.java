package com.pluralsight;

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

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            menuOptions(connection);
        } catch (SQLException e) {

        }
    }

    public static void menuOptions(Connection connection) {
        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("""
                    1) Display all products
                    """);
            int menuChoice = scanner.nextInt();
            scanner.nextLine();
            switch (menuChoice) {
                case 1:
                    displayProducts(connection);
                break;
            }
        }
    }

    public static void displayProducts(Connection connection) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement("""
                    SELECT *
                    FROM Products;
                    """);
            ResultSet set = prepStatement.executeQuery();
            printResults(set);

        } catch (SQLException e) {

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
                System.out.println(columnName + ": " + value + " ");
            }

            //print an empty line to make the results prettier
            System.out.println();
        }
    }
}
