package org.example.zaj3ui2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, Color.rgb(30, 30, 40));

        Image icon = new Image(getClass().getResourceAsStream("/icons/theHorse.png"));
        stage.getIcons().add(icon);
        stage.setTitle("The Ultimate Horse Manager");
        //stage.setFullScreen(true);

        Text text = new Text();

        text.setText("LOGIN to your horse manager :D");
        text.setX(50);
        text.setY(50);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.WHITE);

        root.getChildren().add(text);

        stage.setScene(scene);
        stage.show();
    }
}
