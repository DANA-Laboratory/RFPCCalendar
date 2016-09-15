package main;

import Net.Query;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import pane.BottomLatest;
import pane.LeftToDo;
import pane.RightSearchIO;
import pane.TopNavigation;

import java.io.IOException;

/**
 * Created by Afzalan on 9/14/2016.
 */
public class MainController {
    @FXML  private BorderPane mainPane;
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
        RightSearchIO rightSearchIO = new RightSearchIO(new Query(Query.FindAll, null));
        rightSearchIO.onLabelClicked = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nav.setNewLocation(
                        new Query(((Hyperlink)event.getSource()).getText(), rightSearchIO.getSelectedValue())
                        ,((Hyperlink)event.getSource()).getText()
                );
                mainPane.setCenter(nav.getCurrentView());
            }
        };
        nav.currentLocation.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mainPane.setCenter(nav.getCurrentView());
            }
        });
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
