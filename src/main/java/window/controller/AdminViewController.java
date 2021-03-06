package window.controller;

import database.SQLConnection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import users.Admin;
import users.User;
import users.students.Student;
import window.util.WindowUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class responsible for admin-view.fxml.
 */
public class AdminViewController implements Initializable {
    private Admin loggedInAdmin;
    private ObservableList<Student> students;
    private StudentViewController studentViewController;
    private boolean loggingOut = false;

    @FXML
    private SplitPane splitPane;
    @FXML
    private Button logoutButton;
    @FXML
    private ButtonBar toolButtonBar;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student, Integer> idTableColumn;
    @FXML
    private TableColumn<Student, String> usernameTableColumn;
    @FXML
    private TableColumn<Student, String> lastLoginTableColumn;
    @FXML
    private TableColumn<Student, String> firstNameTableColumn;
    @FXML
    private TableColumn<Student, String> lastNameTableColumn;
    @FXML
    private TableColumn<Student, String> majorTableColumn;
    @FXML
    private TableColumn<Student, String> GPATableColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button commitButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label studentCountLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label displayedStudentCountLabel;

    @FXML
    private void handleLogoutButtonAction(ActionEvent event){
        loggingOut = true;
        WindowUtil.closeWindow(event);
    }

    /**
     * Handles add button pressed action.
     * Displays create student window. Adds the new student object to the
     * table and database if a student was returned.
     *
     * @param event The event thrown.
     */
    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        EditStudentController controller = WindowUtil.displayCreateStudent();

        // Make sure student object was returned
        if (controller != null && controller.getStudent().isPresent()) {
            // Confirm user decision.
            ConfirmationController confirmationController = WindowUtil.displayConfirmation(loggedInAdmin.getPassword());

            if (confirmationController != null && confirmationController.isConfirmed()) {
                // Commence add procedure
                Student student = controller.getStudent().get();
                students.add(student);
                try (SQLConnection connection = new SQLConnection()) {
                    connection.addUser(student);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles remove button pressed action.
     * Removes selected students from table view and from database.
     *
     * @param event The event thrown.
     */
    @FXML
    private void handleRemoveButtonAction(ActionEvent event) {
        List<Student> selectedStudents = studentTableView.getSelectionModel().getSelectedItems();
        if (selectedStudents.size() > 0) {
            // Confirm user decision.
            ConfirmationController controller = WindowUtil.displayConfirmation(loggedInAdmin.getPassword());
            if (controller != null && controller.isConfirmed()) {
                // Execute remove procedure
                try (SQLConnection connection = new SQLConnection()) {
                    selectedStudents.forEach(student -> {
                        connection.removeUserById(student.getID());
                        // Checks if a removed student is being displayed.
                        // Sets student display to nothing if so.
                        if (studentViewController != null) {
                            if (studentViewController.getDisplayedStudent()
                                    .getUserName()
                                    .equals(student.getUserName())) {
                                splitPane.getItems().set(1, new AnchorPane());
                            }
                        }
                    });
                    students.removeAll(selectedStudents);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles refresh button pressed action.
     * Clears table and re-syncs with database.
     *
     * @param event The event thrown.
     */
    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        students.clear();
        pullStudents();
    }

    /**
     * Creates AdminViewController and initializes student list.
     */
    public AdminViewController() {
        students = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Allows cells to determine where each student property should be placed.
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        usernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        lastLoginTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastLoginTime"));
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        majorTableColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        GPATableColumn.setCellValueFactory(new PropertyValueFactory<>("GPA"));

        studentTableView.setRowFactory(row -> buildRowWithEvent());
        studentTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        studentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayStudentView(studentTableView.getSelectionModel().getSelectedItem());
            }
        });

        // Updates student count label on list change.
        students.addListener((ListChangeListener<? super Student>)
                (c -> studentCountLabel.setText(
                        String.format("Number of Students: %d", students.size()))));

        pullStudents();

        FilteredList<Student> filteredData = new FilteredList<>(students, p -> true);
        // Initialize displayed student label count.
        displayedStudentCountLabel.setText(String.format(
                "Number of Students Being Displayed: %s",
                filteredData.size()));
        filteredData.addListener((ListChangeListener<? super Student>)
                (c -> displayedStudentCountLabel.setText(
                        String.format("Number of Students Being Displayed: %s", filteredData.size()))));

        searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(student -> {
                    // If search bar is empty, all elements will be displayed.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return student.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                            student.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                            student.getUserName().toLowerCase().contains(lowerCaseFilter) ||
                            String.valueOf(student.getID()).contains(lowerCaseFilter) ||
                            student.getMajor().toString().toLowerCase().contains(lowerCaseFilter);
                })
        );

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTableView.comparatorProperty());
        studentTableView.setItems(sortedData);
    }

    /**
     * Initializes any data the controller needs before launch.
     *
     * @param admin The admin object that will be logged into the application.
     */
    public void init(Admin admin) {
        this.loggedInAdmin = admin;
        String label = String.format("Username: %s", admin.getUserName());
        usernameLabel.setText(label);
    }

    /**
     * Builds row with mouse event attached that listens
     * for double click.
     *
     * @return The newly built row.
     */
    private TableRow<Student> buildRowWithEvent() {
        TableRow<Student> row = new TableRow<>();
        row.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1 && !row.isEmpty()) {
                displayStudentView(row.getItem());
            }
        });
        return row;
    }

    /**
     * Loaded student view with given student into right split pane.
     *
     * @param student The student instance that is loaded.
     */
    private void displayStudentView(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student-view.fxml"));
            AnchorPane root = loader.load();
            loader.<StudentViewController>getController().init(student, loggedInAdmin);
            studentViewController = loader.getController();
            splitPane.getItems().set(1, root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Pull all student objects from database and
     * adds them to student observable list.
     */
    private void pullStudents() {
        // Populates table on load.
        try (SQLConnection connection = new SQLConnection()) {
            connection.getAllUsers()
                    .get("students")
                    .forEach(user -> students.add((Student) user));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public User getLoggedInAdmin() {
        return this.loggedInAdmin;
    }

    public boolean isLoggingOut(){
        return this.loggingOut;
    }

}
