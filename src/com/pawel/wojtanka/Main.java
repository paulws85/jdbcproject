package com.pawel.wojtanka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.TimeZone;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviesrental";
    private static final String DB_USER = "pawel";
    private static final String DB_PASSWORD = "pawel";
    private static final String LOCAL_TIME_ZONE = TimeZone.getDefault().getID();

    public static void main(String[] args) {
        final String query = "SELECT title, releaseDate FROM moviesinfo";

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", DB_USER);
        connectionProperties.put("password", DB_PASSWORD);
        connectionProperties.put("serverTimezone", LOCAL_TIME_ZONE);
        try (Connection connection = DriverManager.getConnection(DB_URL, connectionProperties)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("Movies in our rental offer: ");
            System.out.println("Title:\t|\tReleaseDate:");

            while(rs.next()) {
                System.out.println(rs.getString("title") + "\t|\t" +
                    rs.getDate(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}