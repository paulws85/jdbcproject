package com.pawel.wojtanka;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Properties;
import java.util.TimeZone;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviesrental";
    private static final String DB_USER = "pawel";
    private static final String DB_PASSWORD = "pawel";
    private static final String LOCAL_TIME_ZONE = TimeZone.getDefault().getID();
    private static final String QUERY = "SELECT title, releaseDate FROM moviesinfo";
    private static final String PARAMETRIZED_QUERY = "SELECT title, releaseDate FROM moviesinfo WHERE releaseDate BETWEEN ? AND ?";

    public static void main(String[] args) {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", DB_USER);
        connectionProperties.put("password", DB_PASSWORD);
        connectionProperties.put("serverTimezone", LOCAL_TIME_ZONE);
        try (Connection connection = DriverManager.getConnection(DB_URL, connectionProperties);
             Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(QUERY);

            System.out.println("Movies in our rental offer: ");
            System.out.println("Title:\t|\tReleaseDate:");

            while (rs.next()) {
                System.out.println(rs.getString("title") + "\t|\t" +
                    rs.getDate(2));
            }

            printMoviesReleasedBetween(LocalDate.parse("2017-02-18"), LocalDate.parse("2020-02-18"), connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMoviesReleasedBetween(final LocalDate from, final LocalDate to, final Connection connection) {
        try (PreparedStatement stmst = connection.prepareStatement(PARAMETRIZED_QUERY)) {
            stmst.setDate(1, Date.valueOf(from));
            stmst.setDate(2, Date.valueOf(to));

            ResultSet rs = stmst.executeQuery();

            while (rs.next()) {
                System.out.println(String.format("Movies in our rental offer (released between %s and %s)", from, to));
                System.out.println(rs.getString(1) + " >>> " + rs.getDate(2));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}