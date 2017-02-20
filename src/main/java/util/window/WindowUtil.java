package main.java.util.window;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contains tools for window management.
 */
public abstract class WindowUtil {
    /**
     * Uses a .fxml path and stage to display a window.
     *
     * @param path  Path to the .fxml file.
     * @param stage The stage that will be used to display the window.
     * @return The controller of the window.
     */

    public static <T> T showWindowAndWait(final String path, final Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowUtil.class.getResource(path));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            return fxmlLoader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Uses a .fxml path, stage and custom controller to display a window.
     *
     * @param path       Path to the .fxml file.
     * @param stage      The stage that will be sed to display the window.
     * @param controller The controller that will be used to display the given stage.
     * @param <T>
     * @return The controller of the stage.
     */
    public static <T> T showWindowAndWait(final String path, final Stage stage, final T controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowUtil.class.getResource(path));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            return fxmlLoader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Closes a window given a source event.
     *
     * @param event The source event.
     */
    public static void closeWindow(final Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
