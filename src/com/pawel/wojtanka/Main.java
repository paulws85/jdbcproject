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
    private static final String INSERT_MOVIE = "INSERT INTO moviesinfo(title, genre, releaseDate, description) VALUES(?, ?, ?, ?)";
    private static final String INSERT_MOVIE_COPY = "INSERT INTO moviescopies(movieInfoId, isRented, rentedTimes, rentedTo) VALUES(?, ?, ?, ?)";
    private static final String UPDATE_MOVIE_COPY = "UPDATE moviescopies SET isRented = ?, rentedTimes = ?, rentedTo = ? where copyId = ?";
    private static final String INSERT_CUSTOMER = "INSERT INTO customers(fullName, phone, email, address) VALUES(?, ?, ?, ?)";
    private static final String RENT_MOVIE = "INSERT INTO " +
        "rents(rentedMovieId, customer, status, rentPricePerDay, rentedDate) VALUES(?, ?, ?, ?, ?)";
    private static final String FIND_MOVIE = "SELECT movieInfoId FROM moviesinfo WHERE title = ? LIMIT 1";
    private static final String FIND_CUSTOMER = "SELECT customerId FROM customers WHERE email = ? LIMIT 1";
    private static final String READ_RENTED_TIMES = "SELECT rentedTimes FROM moviescopies WHERE copyId = ? LIMIT 1";
    private static final String FIND_FIRST_FREE_MOVIE_COPY = "SELECT moviescopies copyId where movieInfoId = ? AND isRented = 0 LIMIT 1";

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

//            DateInterval dateInterval = prepareDateInterval();
//            printMoviesReleasedBetween(dateInterval.getStartDate(), dateInterval.getEndDate());

//            System.out.println("Adding new movie. Result: " + addMovie(
//                "Szeregowiec Rayan",
//                "Drama",
//                LocalDate.parse("1998-07-06"),
//                "War movie"));
//            System.out.println("Adding new customer. Result: " + addCustomer(
//                "Jan Kowalski",
//                "456322333",
//                "jan.kowalski@gmail.com",
//                "Warszawa, ul. Powstańców 23"));

            System.out.println("Make movie possible to rent. Result: " + makeMoviePossibleToRent(
                "Szeregowiec Rayan",
                "Drama",
                LocalDate.parse("1998-07-06"),
                "War movie"));

            rentMovie("krzysztof.pawlak@gmail.com", "Szeregowiec Rayan", 10.0, 3);

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

    private static int addCustomer(String nameAndSurname, String phone, String email, String address) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(INSERT_CUSTOMER)) {
            stmt.setString(1, nameAndSurname);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.setString(4, address);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static Customer askCustomerAboutProfileData() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Give full name:");
        String fullName = scanner.next();
        System.out.println("Give phone:");
        String phone = scanner.next();
        System.out.println("Give email:");
        String email = scanner.next();
        System.out.println("Give address:");
        String address = scanner.next();

        return new Customer(fullName, phone, email, address);
    }

    private static int findCustomer(String email) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(FIND_CUSTOMER)) {
            stmt.setString(1, email);

            return stmt.executeQuery().getInt("customerId");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int addMovie(String title, String genre, LocalDate releaseDate, String description) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(INSERT_MOVIE)) {
            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setDate(3, Date.valueOf(releaseDate));
            stmt.setString(4, description);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int addNewMovieCopy(int movieInfoId) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(INSERT_MOVIE_COPY)) {
            stmt.setInt(1, movieInfoId);
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.setInt(4, 0);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int updateMovieCopy(int copyId, int isRented, int rentedTimes, int rentedTo) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(UPDATE_MOVIE_COPY)) {
            stmt.setInt(1, isRented);
            stmt.setInt(2, rentedTimes);
            stmt.setInt(3, rentedTo);
            stmt.setInt(4, copyId);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int readMovieRentedTimes(int copyId) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(READ_RENTED_TIMES)) {
            stmt.setInt(1, copyId);

            return stmt.executeQuery().getInt("rentedTimes");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int findFirstFreeMovieCopy(int movieInfoId) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(FIND_FIRST_FREE_MOVIE_COPY)) {
            stmt.setInt(1, movieInfoId);

            return stmt.executeQuery().getInt("copyId");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int makeMoviePossibleToRent(String title, String genre, LocalDate releaseDate, String description) {
        int movieId = findMovieId(title);
        int addMovieResult = movieId == 0
            ? 0
            : 1;
        if (movieId == 0) {
            addMovieResult = addMovie(title, genre, releaseDate, description);
        }
        int addNewMovieCopyResult = addNewMovieCopy(findMovieId(title));

        return addMovieResult * addNewMovieCopyResult;
    }

    private static int findMovieId(String movieTitle) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(FIND_MOVIE)) {
            stmt.setString(1, movieTitle);

            return stmt.executeQuery().getInt("movieInfoId");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int addNewRent(int rentedMovieId,
                                  int customer,
                                  String status,
                                  double rentPricePerDay,
                                  LocalDate rentedDate) {
        try (PreparedStatement stmt = DB_CONNECTION.prepareStatement(RENT_MOVIE)) {
            stmt.setInt(1, rentedMovieId);
            stmt.setInt(2, customer);
            stmt.setString(3, status);
            stmt.setDouble(4, rentPricePerDay);
            stmt.setDate(5, Date.valueOf(rentedDate));

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static int rentMovie(String email, String movieTitle, double rentPricePerDay, int rentPeriod) {
        int movieId = findMovieId(movieTitle);

        if (movieId == 0) {
            System.out.println("The movie doesn't exist!");
            return 0;
        }

        int rentedMovieId = findFirstFreeMovieCopy(movieId);

        if (rentedMovieId == 0) {
            System.out.println("All copies of movie are occupied!");
            return 0;
        }

        int customerId = findCustomer(email);
        int addCustomerResult = 0;
        if (customerId == 0) {
            Customer customer = askCustomerAboutProfileData();
            addCustomerResult = addCustomer(customer.getFullName(), customer.getPhone(), customer.getEmail(), customer.getAddress());
            customerId = findCustomer(email);
        }

        LocalDate rentedDate = LocalDate.now();

        int addNewRentResult = addNewRent(rentedMovieId, customerId, "In rent", rentPricePerDay, rentedDate);

        int rentedTimes = readMovieRentedTimes(rentedMovieId);

        int updateMovieCopyResult = updateMovieCopy(rentedMovieId, 1, rentedTimes, 1);

        return addCustomerResult * addNewRentResult * updateMovieCopyResult;
    }

}