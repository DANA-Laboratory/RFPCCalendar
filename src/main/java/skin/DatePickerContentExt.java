package skin;


import behavior.DateCellBehaviorExt;
import control.DateCell;
import control.DatePicker;
import control.DatePickerExt;
import control.EventCell;
import converter.LocalDateStringConverter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by AliReza on 8/26/2016.
 */
public class DatePickerContentExt extends DatePickerContent {
    private List<Node> eventNodes = null;
    private DatePickerEventsPane datePickerEventsPane;
    private LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter();
    DatePickerContentExt(DatePicker datePicker) {
        super(datePicker);
        gridPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        datePickerEventsPane = new DatePickerEventsPane();
        datePickerEventsPane.getChildren().add(this);
        datePickerEventsPane.lastUpdateProperty.addListener(new ChangeListener<Date>() {
            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
                System.out.println("event pane updated");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateEventRectangles();
                    }
                });
            }
        });
        datePickerEventsPane.showEventsProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lastFocusedEventCell.requestFocus();
                        }
                    });
            }
        });
    }
    @Override
    void updateWeeknumberDateCells() {
        super.updateWeeknumberDateCells();
        if(((DatePickerExt)datePicker).isShowCalendarEvents() && datePicker.isShowWeekNumbers()) {
            final int maxWeeksPerMonth = 6;
            for (int i = 0; i < maxWeeksPerMonth; i++) {
                weekNumberCells.get(i).getStyleClass().add("week_number_cell_big");
            }
        }
    }
    @Override void updateDayCells() {
        super.updateDayCells();
        if(((DatePickerExt)datePicker).isShowCalendarEvents()) {
            for (int i = 0; i < 6 * daysPerWeek; i++) {
                DateCell dayCell = dayCells.get(i);
                dayCell.getStyleClass().add("day_cell_big");
            }
            updateEventRectangles();
        }
    }
    public DatePickerEventsPane getDatePickerEventsPane() {
        return datePickerEventsPane;
    }
    private static final Comparator<Node> comprator = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            if (o1 instanceof EventCell && o2 instanceof EventCell) {
                return YearMonth.compare(((EventCell)(o1)).getItem(), ((EventCell)(o2)).getItem());
            }
            return 0;
        }
    };
    private void updateEventRectangles() {
        if (datePickerEventsPane != null)
        {
            if (eventNodes == null) {
                eventNodes = new ArrayList<Node>();
            } else {
                datePickerEventsPane.removeEvents(eventNodes);
                eventNodes.clear();
            }
            if (((DatePickerExt)datePicker).isShowCalendarEvents()) {
                JSONArray calendarEvents = datePickerEventsPane.getCalendarEvents();
                if (calendarEvents != null)
                {
                    createEventCells(calendarEvents);
                    eventNodes.sort(comprator);
                    datePickerEventsPane.addEvents(eventNodes);
                }
            }
        }
    }
    private EventCell lastFocusedEventCell = null;
    private void createEventCells(JSONArray calendarEvents) {
        EventCell eventCell = null;
        for (int i = 0; i< calendarEvents.length(); i++) {
            eventCell = null;
            try {
                eventCell = createEventCell(calendarEvents.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (eventCell != null) {
                eventCell.setLayoutY(eventCell.getLayoutY() + gridPane.getLayoutY());
                eventNodes.add(eventCell);
            } else {
                //TODO
            }
        }
        if(eventNodes.size() > 0)
            lastFocusedEventCell = (EventCell) (eventNodes.get(0));
    }
    private EventCell createEventCell(JSONObject jsonCalendarEvent) {
        final EventHandler<MouseEvent> eventCellActionHandler = ev -> {
            if (ev.getButton() != MouseButton.PRIMARY) {
                return;
            }
            EventCell eventCell = (EventCell)ev.getSource();
            lastFocusedEventCell = eventCell;
            eventCell.requestFocus();
            if(ev.getClickCount()==2){
                selectDayCell(eventCell);
            }
        };
        Date _from;
        Date _to;
        try {
            _from = localDateStringConverter.fromString(jsonCalendarEvent.getString("from"));
            _to =  localDateStringConverter.fromString(jsonCalendarEvent.getString("to"));
        } catch (JSONException err) {
            err.printStackTrace();
            return null;
        }
        DateCell cd1 = findDayCellForDate(_from);
        DateCell cd2 = findDayCellForDate(_to);
        if (cd1 == null && cd2 != null)
            cd1 = findNearestDayCellForDate(_from);
        else
            if (cd1 != null && cd2 == null)
                cd2 = findNearestDayCellForDate(_to);
        if ((cd1 != null) && (cd2 != null)) {
            Bounds bounds1 = cd1.getBoundsInParent();
            Bounds bounds2 = cd2.getBoundsInParent();
            if (bounds1.getMinY() == bounds2.getMinY()) {
                EventCell eventCell = new EventCell(jsonCalendarEvent, bounds1, bounds2);
                eventCell.setLayoutX(getWidth() - eventCell.getLayoutX());
                eventCell.setOnMouseClicked(eventCellActionHandler);
                return eventCell;
            } else {
                //TODO distribute event over weeks
            }
        }
        return null;
    }
    private DateCell findNearestDayCellForDate(Date date) {
        if (date.getTime() < dayCellDates[0].getTime())
            return dayCells.get(0);
        if (date.getTime() >= dayCellDates[dayCellDates.length-1].getTime())
            return dayCells.get(dayCellDates.length-1);
        return null;
    }
    public void goToEventCell(EventCell eventCell, int forward, int unit, boolean requestFocus) {
        if(requestFocus) {
            if(unit == DateCellBehaviorExt.DAILY)
                try{
                    EventCell ec = (EventCell) eventNodes.get(eventNodes.indexOf(eventCell) + forward);
                    ec.requestFocus();
                    lastFocusedEventCell = ec;
                } catch (IndexOutOfBoundsException err){

                }
            else if(unit == DateCellBehaviorExt.WEEKLY) {
                YearMonth yearMonth = new YearMonth(eventCell.getItem());
                int weekValue = yearMonth.getWeekValue();
                EventCell ec = eventCell;
                try {
                    while (weekValue == yearMonth.setDate(ec.getItem()).getWeekValue()) {
                        ec = (EventCell) (eventNodes.get(eventNodes.indexOf(ec) + forward));
                    }
                    ec.requestFocus();
                    lastFocusedEventCell = ec;
                } catch (IndexOutOfBoundsException err){

                }
            }
        }
    }
    @Override
    public void selectDayCell(DateCell dateCell) {
        if(dateCell instanceof EventCell) {
            System.out.println("Select Event Cell");
            return;
        }
        super.selectDayCell(dateCell);
    }
}
