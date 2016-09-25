package control;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

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
    private static JSONGrid eg = new JSONGrid();
    public static JSONGrid getEventGrid() {
        return eg;
    }
    static final EventHandler<MouseEvent> onMouseClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            eg.setCalendarEvent(((EventLabel) event.getSource()).calendarEvent);
        }
    };
}
