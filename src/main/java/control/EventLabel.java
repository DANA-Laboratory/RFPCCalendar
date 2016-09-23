package control;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.json.JSONException;

import java.util.Iterator;

/**
 * Created by AliReza on 9/23/2016.
 */
class EventLabel extends Label {
    private CalendarEvent calendarEvent;
    EventLabel(CalendarEvent calendarEvent) {
        super(calendarEvent.caption);
        this.calendarEvent = calendarEvent;
        setOnMouseClicked(onMouseClicked);
    }
    static final EventHandler<MouseEvent> onMouseClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            CalendarEvent ce = ((EventLabel) event.getSource()).calendarEvent;
            Iterator<String> iterator = ce.getKeys();
            while(iterator.hasNext())
                try {
                    String key = iterator.next();
                    System.out.println(key + " : " + ce.getString(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    };
}
