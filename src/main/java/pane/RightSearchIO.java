package pane;

import Net.Query;
import Net.Socket;
import control.ComboIO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AliReza on 9/12/2016.
 */
public class RightSearchIO extends BorderPane {
    private final ComboIO comboIO;
    private final  VBox types;
    public EventHandler<ActionEvent> onLabelClicked;
    public String getSelectedValue(){
        return (String) comboIO.getSelectionModel().selectedItemProperty().getValue();
    }
    public RightSearchIO(Query query) {
        super();
        setBorder(Main.regularBorder);
        comboIO = new ComboIO(query);
        types = new VBox();
        setBottom(types);
        setTop(comboIO);
        comboIO.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                //System.out.println("new selected value = " + newValue);
                if (newValue != null) {
                    JSONObject jData = new JSONObject();
                    try {
                        jData.put("query", Query.FindTypes.toString());
                        jData.put("parameter", (String) newValue);
                        Socket.get().emit("runQuery", jData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Socket.get().on("types", (t) -> {
            Platform.runLater(()->{
                try {
                    JSONArray arr = new JSONArray((String) t[0]);
                    types.getChildren().clear();
                    for (int i = 0; i<arr.length(); i++) {
                        Hyperlink hyperlink = new Hyperlink(arr.getString(i));
                        hyperlink.onActionProperty().set(onLabelClicked);
                        types.getChildren().add(
                            new TextFlow(
                                    new Text("نمایش "), hyperlink
                            )
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
