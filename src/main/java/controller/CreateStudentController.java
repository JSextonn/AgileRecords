package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.database.SQLConnection;
import main.java.users.students.Course;
import main.java.users.students.Major;
import main.java.users.students.Student;
import main.java.util.security.Hash;
import main.java.util.security.HashingUtil;
import main.java.util.window.WindowUtil;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

// TODO: Configure CreateStudent with CreateCourse and CreateGrade

/**
 * Controller responsible for CreateStudent.fxml backend and logic.
 */
public class CreateStudentController implements Initializable {
    private Student createdStudent;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TableView<Course> courseTableView;
    @FXML
    private TableColumn<Course, Integer> crnTableColumn;
    @FXML
    private TableColumn<Course, String> nameTableColumn;
    @FXML
    private TableColumn<Course, String> creditsTableColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ComboBox<String> majorComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Allows cells to determine where each student property should be placed.
        crnTableColumn.setCellValueFactory(new PropertyValueFactory<>("CRN"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsTableColumn.setCellValueFactory(new PropertyValueFactory<>("creditHours"));

        Arrays.asList(Major.values())
                .forEach(value -> majorComboBox.getItems().add(value.toString()));
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        // TODO: Add functionality
    }

    @FXML
    private void handleRemoveButtonAction(ActionEvent event) {
        courseTableView.getItems()
                .removeAll(courseTableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void handleCreateButtonAction(ActionEvent event) {
        // Make sure required fields are filled.
        if (usernameTextField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                majorComboBox.getSelectionModel().isEmpty()) {
            displayErrorLabel("Required Field(s) Blank");
        } else {
            if (usernameIsAvailable()) {
                createStudent();
                WindowUtil.closeWindow(event);
            } else {
                displayErrorLabel("Username Taken");
            }
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        createdStudent = null;
        WindowUtil.closeWindow(event);
    }

    /**
     * Builds student object then sets the createdStudent object.
     */
    private void createStudent() {
        try {
            String salt = HashingUtil.generateSalt(20, new Random());
            Hash hash = HashingUtil.hash(passwordField.getText(), "SHA-512", salt);
            createdStudent = new Student(usernameTextField.getText(), hash);
            createdStudent.setFirstName(firstNameTextField.getText());
            createdStudent.setLastName(lastNameTextField.getText());
            createdStudent.setEmail(emailTextField.getText());
            createdStudent.setMajor(Major.valueOf(majorComboBox.getValue()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void displayErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private boolean usernameIsAvailable() {
        String username = usernameTextField.getText();
        if (!username.isEmpty()) {
            try (SQLConnection connection = new SQLConnection()) {
                return connection.isUnique(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public Optional<Student> getCreatedStudent() {
        return Optional.ofNullable(createdStudent);
    }
}
