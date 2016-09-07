package skin;
import control.EventCell;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Created by AliReza on 9/1/2016.
 */
public class DatePickerEventsPane extends Pane {
    static Socket socket = null;
    static final int PORT = 9291;
    private JSONArray calendarEvents;
    private boolean showEvents = true;
    public ObjectProperty<Date> lastUpdateProperty = new SimpleObjectProperty<Date>();
    public DatePickerEventsPane() {
        super();
        try {
            socketConnect();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        addEventHandler(KeyEvent.ANY, e -> {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (e.getCode()) {
                    case F9:
                        getChildren().forEach(n->{if(n instanceof DatePickerContentExt) n.requestFocus(); return;});
                        break;
                    case F10:
                        showEvents = !showEvents;
                        getChildren().forEach(n->{if(n instanceof EventCell) n.setVisible(showEvents);});
                        if(showEvents)
                            getChildren().forEach(n->{if(n instanceof EventCell) n.requestFocus(); return;});
                        break;
                }
            }
        });
    }
    public void addEvents(List<Node> eventNodes){
        eventNodes.forEach((en) -> {en.setVisible(showEvents); getChildren().add(en);});
    }
    public JSONArray getCalendarEvents(){
        return calendarEvents;
    }
    public void removeEvents(List<Node> eventNodes) {
        eventNodes.forEach((tf) -> getChildren().remove(tf));
    }
    private void socketConnect() throws URISyntaxException, InterruptedException {
        socket = IO.socket("http://localhost:" + PORT);
        socket.on(Socket.EVENT_CONNECT, objects -> {
            System.out.println("I`m connected and send request for events");
            socket.emit("requestEvents");
        });
        socket.on("calendarEvent", args -> {
            if (args.length > 0) {
                try {
                    if (calendarEvents == null)
                        calendarEvents = new JSONArray((String) args[0]);
                    else {
                        JSONArray jArr = new JSONArray((String) args[0]);
                        for (int i = 0; i < jArr.length(); i++)
                            calendarEvents.put(jArr.getJSONObject(i));
                    }
                    System.out.println(calendarEvents.length() + " calendar event(s) received.");
                    lastUpdateProperty.setValue(new Date());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.connect();
    }
    public static void close() {
        if (socket != null) {
            if (socket.connected())
                socket.disconnect();
            socket.close();
        }
    }
}
