package main;

import Net.Query;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import panes.BottomLatest;
import panes.LeftToDo;
import panes.RightSearchIO;
import panes.TopNavigation;

import java.io.IOException;

/**
 * Created by Afzalan on 9/14/2016.
 */
public class MainController {
    @FXML  public BorderPane mainPane;
    private static LeftToDo leftToDo;
    private static TopNavigation nav;

    public void initialize() {
        mainPane.getStylesheets().add(getClass().getResource("../RFPCCalendar.css").toExternalForm());
        String[] navCaptions = {"فراگیر", "مدرس", "کلاس", "دوره", "لیست فرآگیران", "لیست مربیان", "لیست دوره ها", "گروه", "لیست گروهها"};
        String[] navKeys = {"trainee", "teacher", "class", "course", "trainees", "teachers", "courses", "group", "groups"};
        try {
            nav = new TopNavigation(navKeys, navCaptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RightSearchIO rightSearchIO = new RightSearchIO(Query.FindAll);
        rightSearchIO.onLabelClicked = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainPane.setCenter(nav.getView(((Hyperlink)event.getSource()).getText()));
            }
        };
        leftToDo = new LeftToDo();
        BottomLatest bottomLatest = new BottomLatest();
        mainPane.setLeft(rightSearchIO);
        mainPane.setRight(leftToDo);
        mainPane.setBottom(bottomLatest);
        mainPane.setCenter(nav.getHome());
        mainPane.setTop(nav);

    }
    public static String[] getNavigation(){
        return nav.getKeys();
    }

}
