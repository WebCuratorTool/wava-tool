package org.webcurator.visualization.springboot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final WebView webView = new WebView();
    private final ProgressIndicator spinner = new ProgressIndicator();

    public static void main(String[] args) {
        startRestApiServer();
        waitForServer(8080, 10000); // wait max 10 seconds
        launch(args);
    }

    @Override
    public void start(Stage stage) throws MalformedURLException {
        BorderPane root = new BorderPane();

        // Top toolbar
        ToolBar toolBar = new ToolBar();
        Button selectBtn = new Button("Select File/Folder");
        // toolBar.getItems().add(selectBtn);
        Label pathLabel = new Label("No selection");
        pathLabel.setMaxWidth(Double.MAX_VALUE);
        toolBar.getItems().addAll(selectBtn, new Separator(), pathLabel);
        root.setTop(toolBar);

        StackPane centerPane = new StackPane();
        centerPane.getChildren().addAll(webView, spinner);
        spinner.setVisible(false);
        webView.setVisible(false);
        root.setCenter(centerPane);

        // File chooser action
        selectBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selected = fileChooser.showOpenDialog(stage);
            if (selected != null) {
                pathLabel.setText(selected.getAbsolutePath());
                processFile(selected);
            }
        });

        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.show();
    }

    private static void startRestApiServer() {
        new Thread(() -> ApiApp.run(new String[] {})).start();
    }

    private static void waitForServer(int port, int timeoutMillis) {
        long start = System.currentTimeMillis();
        while (true) {
            try (Socket socket = new Socket("localhost", port)) {
                return; // success
            } catch (IOException ignored) {
                if (System.currentTimeMillis() - start > timeoutMillis) {
                    throw new RuntimeException("Spring Boot server failed to start within timeout");
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void processFile(File file) {
        spinner.setVisible(true);
        webView.setVisible(false);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(3000); // Simulate processing
                } catch (InterruptedException ignored) {
                }
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    // webView.getEngine().loadContent("<html><body><h2>Processed: " +
                    // file.getName() + "</h2></body></html>");
                    // URL url = getClass().getResource("/web/index.html");
                    webView.getEngine().load("http://localhost:8080");
                    spinner.setVisible(false);
                    webView.setVisible(true);
                });
            }
        };

        new Thread(task).start();
    }
}
