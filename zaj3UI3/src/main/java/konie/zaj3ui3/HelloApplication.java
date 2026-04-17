package konie.zaj3ui3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root =  FXMLLoader.load(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(root);


        Image icon = new Image(getClass().getResourceAsStream("/icons/theHorse.png"));
        stage.getIcons().add(icon);
        stage.setTitle("The Ultimate Horse Manager");


        stage.setScene(scene);
        stage.show();
    }
}