package skin;
import Net.Socket;
import control.EventCell;
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
    private JSONArray calendarEvents;
    public ObjectProperty<Date> lastUpdateProperty = new SimpleObjectProperty<Date>();
    public ObjectProperty<Boolean> showEventsProperty = new SimpleObjectProperty<Boolean>();
    public DatePickerEventsPane() {
        super();
        showEventsProperty.setValue(true);
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
                        showEventsProperty.setValue(!showEventsProperty.get());
                        getChildren().forEach(n->{if(n instanceof EventCell) n.setVisible(showEventsProperty.get());});
                        break;
                    case DELETE:
                        //TODO emit delete
                }
            }
        });
    }
    public void addEvents(List<Node> eventNodes){
        eventNodes.forEach((en) -> {en.setVisible(showEventsProperty.get()); getChildren().add(en);});
    }
    public JSONArray getCalendarEvents(){
        return calendarEvents;
    }
    public void removeEvents(List<Node> eventNodes) {
        eventNodes.forEach((tf) -> getChildren().remove(tf));
    }
    private void socketConnect() throws URISyntaxException, InterruptedException {
        Socket.get().on(io.socket.client.Socket.EVENT_CONNECT, objects -> {
            System.out.println("I`m connected and send request for events");
            Socket.get().emit("requestEvents");
        });
        Socket.get().on("calendarEvent", args -> {
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
        Socket.get().connect();
    }
    public static void close() {
        if (Socket.get() != null) {
            if (Socket.get().connected())
                Socket.get().disconnect();
            Socket.get().close();
        }
    }
}
