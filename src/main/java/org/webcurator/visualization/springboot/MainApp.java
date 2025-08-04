package org.webcurator.visualization.springboot;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainApp extends Application {
    public static void main(String[] args) {
        startRestApiServer();
        waitForServer(8080, 10000); // wait max 10 seconds
        launch(args);
    }

    @Override
    public void start(Stage stage) throws MalformedURLException {
        WebView webView = new WebView();
//        URL url = getClass().getResource("/index.html");
//        if (url == null) {
//            System.err.println("index.html not found");
//            System.exit(1);
//        }

        URL url = new URL("http://localhost:8080");
        webView.getEngine().load(url.toExternalForm());

        stage.setTitle("WAVA");
        stage.setScene(new Scene(webView));
//        stage.setFullScreen(true);
//        stage.show();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.show();
    }

    private static void startRestApiServer() {
        new Thread(() -> ApiApp.run(new String[]{})).start();
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
