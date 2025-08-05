package org.webcurator.visualization.springboot;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

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

        Scene scene = new Scene(root, 800, 600);
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
}
