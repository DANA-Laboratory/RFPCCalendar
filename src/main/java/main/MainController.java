package main;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 * Created by Afzalan on 9/14/2016.
 */
public class MainController {
    @FXML  public BorderPane mainPane;
    public void initialize() {
        mainPane.getStylesheets().add(getClass().getResource("../RFPCCalendar.css").toExternalForm());
    }

}
