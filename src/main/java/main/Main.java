package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayDeque;


/**
 * Created by Afzalan on 9/11/2016.
 */
public class Main extends Application {
    public static final Border regularBorder = new Border(new BorderStroke(Paint.valueOf("BLACK"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../main.fxml")), 1024, 800);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Bye/.");
                threads.forEach((t)->{t.interrupt();});
                Net.Socket.get().close();
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
    private static BorderPane main = null;
    private static ArrayDeque<Thread> threads = new ArrayDeque<>();
    public static Thread getNewThread(Runnable runnable){
        threads.addLast(new Thread(runnable));
        return threads.getLast();
    }
}
