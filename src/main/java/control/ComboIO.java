package control;

import Net.Query;
import Net.Socket;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by AliReza on 9/7/2016.
 */
public class ComboIO extends ComboBox {
    private final Query query;
    private String oldText = "";
    private boolean isInList = false;
    public ComboIO() {
        query = new Query(Query.FindAll, null);
        new ComboIO(query);
    }
    public ComboIO(Query query) {
        super();
        this.query = query;
        setEditable(true);
        nodeOrientationProperty().setValue(NodeOrientation.RIGHT_TO_LEFT);
        addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if(getEditor().getText().compareTo(oldText) != 0) {
                if(getItems().indexOf(getEditor().getText()) < 0) {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("query", query);
                        data.put("parameter", getEditor().getText());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Socket.get().emit("runQuery", data.toString());
                    isInList = false;
                } else
                    isInList = true;
                oldText = getEditor().getText();
            }
        });
        try {
            socketConnect();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void socketConnect() throws URISyntaxException, InterruptedException {
        Socket.get().on(io.socket.client.Socket.EVENT_CONNECT, objects -> {
            //System.out.println("I`m connected and send request for events");
        });
        Socket.get().on("items", args -> {
            JSONArray jItems = null;
            try {
                jItems = new JSONArray((String) (args[0]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONArray finalJItems = jItems;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    getItems().clear();
                    for (int i = 0; i < finalJItems.length(); i++)
                        try {
                            getItems().add(finalJItems.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    show();
                }
            });
        });
        Socket.get().connect();
    }
}
