package main.java.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.users.Admin;
import main.java.users.User;
import main.java.users.students.Student;
import main.java.util.security.Hash;
import main.java.util.security.HashingUtil;

import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a connection to a sql database.
 */
// TODO: Clean up code, optimize. NOTE: Functions with written documentation will be considered clean and stable.
public class SQLConnection {
    private Connection connection;
    private String host;
    private String password;
    private String dbName;

    public SQLConnection(String host, String password, String dbName) {
        this.host = host;
        this.password = password;
        this.dbName = dbName;

        this.connection = establishConnection(host, password, dbName);
    }

    /**
     * Use to obtain an sql connection.
     * NOTE: This function is temporary.
     * WARNING: MAY RETURN NULL
     *
     * @return The sql connection.
     */
    public static Connection establishConnection(final String host,
                                                 final String password,
                                                 final String dbName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No MySQL JDBC Driver Detected!");
            e.printStackTrace();
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(host, password, dbName);
        } catch (SQLException e) {
            System.err.println("Connection Failed!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Determines the next unique ID in the database.
     *
     * @return The next unique ID.
     */
    // TODO: Determine if we need to check for both ID's being -1.
    public int getNextID() {
        int nextStudentID = getAutoIncrement("students");
        int nextAdminID = getAutoIncrement("administrators");
        return Math.max(nextStudentID, nextAdminID);
    }

    /**
     * Helper function for getNextID, given a table query.
     * Returns -1 if the table does not exist.
     *
     * @param tableName The name of the table that will be access for its auto increment.
     * @return The next id available.
     */
    private int getAutoIncrement(String tableName) {
        String query = String.format(
                "SELECT `auto_increment` FROM INFORMATION_SCHEMA.TABLES WHERE table_name = '%s'",
                tableName);
        int nextID;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            nextID = resultSet.getInt("auto_increment");
        } catch (SQLException e) {
            nextID = -1;
        }
        return nextID;
    }


    /**
     * Adds user data to the correct data depending on whether the user
     * is an Admin or Student.
     *
     * @param newUser The user instance that will be added.
     */
    public void addUser(User newUser) {
        Gson gson = new GsonBuilder().create();
        String userData = gson.toJson(newUser);

        String type;
        if (newUser instanceof Student) {
            type = "students";
        } else {
            type = "administrators";
        }

        String query = String.format(
                "INSERT INTO `txscypaa_agilerecords`.`%s` (`id`,`username`, `studentData`) VALUES  (?,?,?)",
                type);

        // Prepares statement and executes.
        try {
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            preparedStmt.setInt(1, getNextID());
            preparedStmt.setString(2, newUser.getUserName());
            preparedStmt.setString(3, userData);

            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User getTableMember(String query, String data, Type type){
        User user = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.isBeforeFirst()) {
                Gson gson = new GsonBuilder().create();
                String userData = resultSet.getString(data);
                user = gson.fromJson(userData, type);
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        return user;
    }

    public User getUser(int id) {
        String query = String.format("SELECT `studentData` FROM `txscypaa_agilerecords`.`students`  WHERE  `id` = '%d'", id);
        User user = getTableMember(query, "studentData", Student.class);
        if (user != null){
            return user;
        }

        query = String.format("SELECT `adminData` FROM `txscypaa_agilerecords`.`administrators`  WHERE  `id` = '%d'", id);
        user = getTableMember(query, "adminData", Student.class);
        if (user != null){
            return user;
        }

        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> allStudents = new ArrayList<>();

        try {
            String query = "SELECT `studentData` FROM `txscypaa_agilerecords`.`students`  WHERE  1";

            // create the mysql insert prepared statement
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            Gson gson = new GsonBuilder().create();
            while (rs.next()) {
                String studentData = rs.getString("studentData");
                allStudents.add(gson.fromJson(studentData, Student.class));
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        return allStudents;
    }

    public boolean updateStudent(int id, Student student) {
        try {
            Gson gson = new GsonBuilder().create();
            String studentData = gson.toJson(student);

            String query = "UPDATE `txscypaa_agilerecords`.`students` SET `studentData` = ? WHERE `students`.`id` = ?";

            // create the mysql insert prepared statement
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            // preparedStmt.setString (1, "");
            preparedStmt.setString(1, studentData);
            preparedStmt.setInt(2, id);

            // execute the prepared statement
            return preparedStmt.execute();
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        return false;
    }

    /*
        TODO : Is Username Unique

        public boolean isUniqueUsername(String username)
     */

    public User attemptLogin(String username, String password) throws FailedLoginException {
        try {
            String query = String.format("SELECT `studentData` FROM `txscypaa_agilerecords`.`students`  WHERE  `username` = '%s'", username);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            Gson gson = new GsonBuilder().create();
            if (resultSet.isBeforeFirst()) {
                String studentData = resultSet.getString("studentData");
                Student theStudent = gson.fromJson(studentData, Student.class);

                if (checkPassword(theStudent.getPassword(), password)) {
                    return theStudent;
                } else {
                    throw new FailedLoginException("Login failed, incorrect student password");
                }
            }

            query = String.format("SELECT `adminData` FROM `txscypaa_agilerecords`.`administrators`  WHERE  `username` = '%s'", username);
            resultSet = statement.executeQuery(query);

            if (resultSet.isBeforeFirst()) {
                String adminData = resultSet.getString("adminData");
                Admin theAdmin = gson.fromJson(adminData, Admin.class);

                if (checkPassword(theAdmin.getPassword(), password)) {
                    return theAdmin;
                } else {
                    throw new FailedLoginException("Login failed, incorrect admin password");
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

       throw new FailedLoginException("Login failed, username does not exist");
    }

    private boolean checkPassword(Hash hash, String password) {
        boolean pass = false;
        try {
            pass = hash.equals(HashingUtil.hash(password, hash.getAlgorithm(), hash.getSalt()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pass;
    }

    /*

     TODO: Login Method

     public User attemptLogin(String username, String password)

     password needs to be hashed and compared to the hash in the DB

     getSalt

     Go until you find the username

     add username field to database

      */

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
