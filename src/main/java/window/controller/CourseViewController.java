package window.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.students.Course;
import users.students.Grade;
import util.MathUtil;
import window.util.WindowUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller class responsible for course-view.fxml.
 */
public class CourseViewController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Label CRNLabel;
    @FXML
    private Label creditHoursLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label gpaLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private TableView<Grade> gradeTableView;
    @FXML
    private TableColumn<Grade, String> nameColumn;
    @FXML
    private TableColumn<Grade, String> typeColumn;
    @FXML
    private TableColumn<Grade, Double> scoreColumn;
    @FXML
    private Button closeButton;

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        WindowUtil.closeWindow(event);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    /**
     * Initializes course instance into window.
     *
     * @param course The course instance that will be initialized.
     */
    public void init(Course course) {
        String label = String.format("Name: %s", course.getName());
        nameLabel.setText(label);

        label = String.format("CRN: %s", course.getCRN());
        CRNLabel.setText(label);

        label = String.format("Credit Hours: %s", course.getCreditHours());
        creditHoursLabel.setText(label);

        if (course.getDateInterval() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
            LocalDate startDate = course.getDateInterval().getStart();
            LocalDate endDate = course.getDateInterval().getEnd();
            dateLabel.setText(String.format("Date: %s - %s", startDate.format(formatter), endDate.format(formatter)));
        }

        if (course.getTimeInterval() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
            LocalTime startTime = course.getTimeInterval().getStart();
            LocalTime endTime = course.getTimeInterval().getEnd();
            timeLabel.setText(String.format("Time: %s - %s", startTime.format(formatter), endTime.format(formatter)));
        }

        label = String.format("GPA: %s", course.getAverage());
        gpaLabel.setText(label);
        label = String.format("Score: %s / %s", MathUtil.round(course.getTotalScore(), 2), (double) course.getGrades().size() * 100);
        scoreLabel.setText(label);

        gradeTableView.getItems().addAll(course.getGrades());
    }
}
