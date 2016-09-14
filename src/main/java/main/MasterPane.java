package main;

import control.Navigation;
import Net.Query;
import control.Clock;
import control.SearchIO;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import view.Latest;


/**
 * Created by Afzalan on 9/11/2016.
 */
public class MasterPane extends Application {
    public static final Border regularBorder = new Border(new BorderStroke(Paint.valueOf("BLACK"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    private static Clock clock;
    private static Navigation nav;
    @Override
    public void start(Stage primaryStage) throws Exception {
        home = FXMLLoader.load(getClass().getResource("../home.fxml"));
        main = FXMLLoader.load(getClass().getResource("../main.fxml"));
        String[] navCaptions = {"فراگیر", "مدرس", "کلاس", "دوره", "لیست فرآگیران", "لیست مربیان", "لیست دوره ها", "گروه", "لیست گروهها"};
        String[] navKeys = {"trainee", "teacher", "class", "course", "trainees", "teachers", "courses", "group", "groups"};
        nav = new Navigation(navKeys, navCaptions);
        Scene scene = new Scene(createPane(), 1024, 800);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Bye/.");
                clock.interrupt();
                Net.Socket.get().close();
            }
        });
    }
    private static Pane createPane() {
        SearchIO searchIO = new SearchIO(Query.FindAll);
        searchIO.onLabelClicked = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.setCenter(nav.getView(((Hyperlink)event.getSource()).getText()));
            }
        };
        clock = new Clock();
        Latest latest = new Latest();
        main.setLeft(searchIO);
        main.setRight(clock);
        main.setBottom(latest);
        main.setCenter(home);
        main.setTop(nav);
        return main;
    }
    public static String[] getNavigation(){
        return nav.getKeys();
    }
    public static void main(String[] args) {
        launch(args);
    }
    private static Node home = null;
    private static BorderPane main = null;
}
