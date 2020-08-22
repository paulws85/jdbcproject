package com.pawel.wojtanka;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviesrental";
    private static final String DB_USER = "pawel";
    private static final String DB_PASSWORD = "pawel";
    private static final String LOCAL_TIME_ZONE = TimeZone.getDefault().getID();
    private static final String QUERY = "SELECT title, releaseDate FROM moviesinfo";
    private static final String PARAMETRIZED_QUERY = "SELECT title, releaseDate " +
        "FROM moviesinfo " +
        "WHERE releaseDate BETWEEN ? AND ?";

    private static Connection DB_CONNECTION;

    public static void main(String[] args) {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", DB_USER);
        connectionProperties.put("password", DB_PASSWORD);
        connectionProperties.put("serverTimezone", LOCAL_TIME_ZONE);
        try {
            DB_CONNECTION = DriverManager.getConnection(DB_URL, connectionProperties);

//            Statement stmt = DB_CONNECTION.createStatement();
//
//            ResultSet rs = stmt.executeQuery(QUERY);
//
//            System.out.println("Movies in our rental offer: ");
//            System.out.println("Title:\t|\tReleaseDate:");
//
//            while (rs.next()) {
//                System.out.println(rs.getString("title") + "\t|\t" +
//                    rs.getDate(2));
//            }

            DateInterval dateInterval = prepareDateInterval();
            printMoviesReleasedBetween(dateInterval.getStartDate(), dateInterval.getEndDate());

//            stmt.close();
            DB_CONNECTION.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMoviesReleasedBetween(final LocalDate from, final LocalDate to) {
        try (PreparedStatement stmst = DB_CONNECTION.prepareStatement(PARAMETRIZED_QUERY)) {
            stmst.setDate(1, Date.valueOf(from));
            stmst.setDate(2, Date.valueOf(to));

            ResultSet rs = stmst.executeQuery();

            printMovies(from, to, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMovies(LocalDate from, LocalDate to, ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(String.format("Movies in our rental offer (released between %s and %s)", from, to));
            System.out.println(rs.getString(1) + " >>> " + rs.getDate(2));
        }
    }

    private static DateInterval prepareDateInterval() {
        System.out.println("Give start date:");
        LocalDate from = giveDate();
        System.out.println("Give end date:");
        LocalDate to = giveDate();

        return new DateInterval(from, to);
    }

    private static LocalDate giveDate() {
        Scanner scanner = new Scanner(System.in);
        return LocalDate.parse(scanner.next());
    }

}