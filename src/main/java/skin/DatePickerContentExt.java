package skin;


import behavior.DateCellBehaviorExt;
import com.ibm.icu.util.Calendar;
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
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
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
    private LocalDateStringConverter localDateStringConverter;
    DatePickerContentExt(DatePicker datePicker) {
        super(datePicker);
        gridPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        datePickerEventsPane = new DatePickerEventsPane();
        datePickerEventsPane.getChildren().add(this);
        localDateStringConverter = new LocalDateStringConverter();
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
                dayCell.getStyleClass().add("date_cell_big");
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
    private void createEventCells(JSONArray calendarEvents) {
        EventCell eventCell = null;
        Date today  = Calendar.getInstance(faLocale).getTime();
        for (int i = 0; i<calendarEvents.length(); i++) {
            try {
                JSONObject calendarEvent = calendarEvents.getJSONObject(i);
                eventCell = createEventCell(calendarEvent.getString("from"), calendarEvent.getString("to"));
                if (eventCell != null) {
                    eventCell.setLayoutY(eventCell.getLayoutY() + gridPane.getLayoutY());
                    String caption = calendarEvents.getJSONObject(i).getString("caption");
                    String trainer = calendarEvents.getJSONObject(i).getString("trainer");
                    String location = calendarEvents.getJSONObject(i).getString("location");
                    eventCell.setText(calendarEvents.getJSONObject(i).getString("caption"));
                    eventCell.updateItem(today, false);
                    eventNodes.add(eventCell);
                } else {
                    //TODO
                }
            } catch (JSONException err) {
                err.printStackTrace();
            }
        }
    }

    private EventCell createEventCell(String from, String to) {
        Date _from = localDateStringConverter.fromString(from);
        Date _to =  localDateStringConverter.fromString(to);
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
                double minX = Math.min(bounds1.getMinX(), bounds2.getMinX());
                double minY = Math.min(bounds1.getMinY(), bounds2.getMinY());
                double height = Math.max(bounds1.getMaxY(), bounds2.getMaxY()) - minY;
                double width = Math.max(bounds1.getMaxX(), bounds2.getMaxX()) - minX;
                EventCell eventCell = new EventCell(_from, _to);
                eventCell.setPrefHeight(height);
                eventCell.setPrefWidth(width);
                eventCell.setLayoutX(getWidth()- minX - width);
                eventCell.setLayoutY(minY);
                return eventCell;
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
        System.out.println("goToEventCell " + unit);
        if(requestFocus) {
            if(unit == DateCellBehaviorExt.DAILY)
                try{
                    eventNodes.get(eventNodes.indexOf(eventCell) + forward).requestFocus();
                } catch (IndexOutOfBoundsException err){

                }
            else if(unit == DateCellBehaviorExt.WEEKLY) {
                YearMonth yearMonth = new YearMonth(eventCell.getItem());
                int weekValue = yearMonth.getWeekValue();
                EventCell ec = eventCell;
                try {
                    while (weekValue == yearMonth.setDate(ec.getItem()).getWeekValue()) {
                        ec = (EventCell) (eventNodes.get(eventNodes.indexOf(ec) + forward));
                        System.out.println(yearMonth.setDate(ec.getItem()).getWeekValue());
                    }
                    ec.requestFocus();
                } catch (IndexOutOfBoundsException err){

                }
            }
        }
    }
}
