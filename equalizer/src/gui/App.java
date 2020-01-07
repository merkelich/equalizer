package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class App extends Application {
     
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/res/icon.png")));
        stage.setTitle("Equalizer");
        stage.setScene(scene);
        stage.show();
    
    stage.setOnCloseRequest(event -> {
        FXMLController.closed = true;
        stage.close();
        System.exit(0);
    });
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
