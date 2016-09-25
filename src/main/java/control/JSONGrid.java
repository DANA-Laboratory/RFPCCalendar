package control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.json.JSONException;

import java.util.Iterator;

/**
 * Created by Afzalan on 9/24/2016.
 */
class JSONGrid extends GridPane {
    Button updateBtn;
    Button deleteBtn;
    Button addNewBtn;
    JSONGrid(){
        super();
        getStylesheets().add(getClass().getResource("../GanttChart.css").toExternalForm());
        updateBtn = new Button("\uf0c7");
        updateBtn.onActionProperty().setValue(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getChildren().clear();
            }
        });
        updateBtn.getStyleClass().add("icon");
        deleteBtn = new Button("\uf068");
        deleteBtn.onActionProperty().setValue(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //delete calendarEvent
                getChildren().clear();
            }
        });
        deleteBtn.getStyleClass().add("icon");
        addNewBtn = new Button("\uf067");
        addNewBtn.onActionProperty().setValue(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getChildren().forEach((e) -> {
                    if(GridPane.getColumnIndex(e) == 1)
                        ((TextField)(e)).setText("");
                });
            }
        });
        addNewBtn.getStyleClass().add("icon");
        GridPane.setColumnIndex(updateBtn, 2);
        GridPane.setColumnIndex(deleteBtn, 2);
        GridPane.setColumnIndex(addNewBtn, 2);
        GridPane.setRowIndex(updateBtn, 0);
        GridPane.setRowIndex(deleteBtn, 1);
        GridPane.setRowIndex(addNewBtn, 2);
    }
    public void setCalendarEvent(CalendarEvent ce){
        this.calendarEvent = ce;
        getChildren().clear();
        Iterator<String> iterator = calendarEvent.getKeys();
        int i = 0;
        while(iterator.hasNext())
            try {
                String key = iterator.next();
                Label label = new Label(key);
                TextField textField = new TextField( calendarEvent.getString(key));
                GridPane.setColumnIndex(label, 0);
                GridPane.setColumnIndex(textField, 1);
                GridPane.setRowIndex(label, i);
                GridPane.setRowIndex(textField, i++);
                getChildren().addAll(label, textField);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        getChildren().addAll(updateBtn, deleteBtn, addNewBtn);
    }
    private CalendarEvent calendarEvent;
}