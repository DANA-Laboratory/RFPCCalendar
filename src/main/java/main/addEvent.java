package main;

import control.ComboIO;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by AliReza on 9/8/2016.
 */
public class AddEvent extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../newEvent.fxml"));
        primaryStage.setTitle("رویداد جدید");
        primaryStage.setScene(new Scene(root, 800, 50));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("time to close threads");
                Net.Socket.get().close();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
