package panes;

import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by AliReza on 9/11/2016.
 */
public class BottomLatest extends AnchorPane {
    private ListView history = new ListView();
    public BottomLatest(){
        super();
        AnchorPane.setBottomAnchor(history ,0.0);
        AnchorPane.setTopAnchor(history ,0.0);
        AnchorPane.setLeftAnchor(history ,0.0);
        AnchorPane.setRightAnchor(history ,0.0);
        getChildren().add(history);
        setPrefHeight(100.0);
    }
}
