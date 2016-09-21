package control;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.*;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import skin.YearMonth;

import java.util.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by AliReza on 9/15/2016.
 */


public class WeekScheduleDetail extends Pane {

    //Properties
    public SimpleDoubleProperty yMarginProperty = new SimpleDoubleProperty(5);
    public SimpleDoubleProperty yUnitProperty = new SimpleDoubleProperty(USE_COMPUTED_SIZE){
        @Override public double get(){
            if(super.get()==USE_COMPUTED_SIZE)
                return computeMinYUnit();
            else
                return super.get();
        }
    };

    private WeekSchedule weekSchedule;
    private final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    private final static DateFormat faFormatterFull = DateFormat.getDateInstance(DateFormat.FULL, faLocale);
    private final static DateFormat faFormatterHm = DateFormat.getPatternInstance("HH:mm", faLocale);
    private boolean[] hiddenDays = {false, false, false, false, false, true, true};

    private ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    private ArrayList<Label> labels = new ArrayList<Label>();
    private ArrayList<Line> lines = new ArrayList<Line>();

    public static String fromDateString;
    public static String toDateString;
    public static String fromHmString;
    public static String toHmString;

    static final TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();

    private double minForDay = 0;

    WeekScheduleDetail(WeekSchedule _weekSchedule) {
        super();
        weekSchedule = _weekSchedule;

        toDateString = faFormatterFull.format(weekSchedule.toDate);
        toHmString = faFormatterHm.format(weekSchedule.toDate);
        fromDateString = faFormatterFull.format(weekSchedule.fromDate);
        fromHmString = faFormatterHm.format(weekSchedule.fromDate);

        getStylesheets().add(getClass().getResource("../RFPCCalendar.css").toExternalForm());
        minForDay = YearMonth.getDateDiff(weekSchedule.fromDate, weekSchedule.toDate, TimeUnit.MINUTES);
        minForDay -= (60*24)*((int)(minForDay/(60*24)));

        yUnitProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && weekSchedule.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        yMarginProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && weekSchedule.sortedRowNamesProperty.get() != null) {
                    updateLayout();
                    updateLines();
                }
            }
        });
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && weekSchedule.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && weekSchedule.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        weekSchedule.sortedRowNamesProperty.addListener(new ChangeListener<SortedSet<String>>() {
            @Override
            public void changed(ObservableValue<? extends SortedSet<String>> observable, SortedSet<String> oldValue, SortedSet<String> newValue) {
                if(newValue != null) {

                    show();
                }
            }
        });
        if (weekSchedule.sortedRowNamesProperty.get() != null)
            show();
    }
    @Override protected double computePrefHeight(double width) {
        int rowCount = weekSchedule.sortedRowNamesProperty.get().size();
        double value = computeMinYUnit() * rowCount + (rowCount-1)* yMarginProperty.get();
        return value;
    }
    public void hideDay(int day){
        if(!hiddenDays[day]) {
            hiddenDays[day] = true;
            //TODO
        }
    };
    public void unHideDay(int day){
        if(hiddenDays[day]) {
            hiddenDays[day] = false;
            //TODO
        }
    }
    private void show() {
        createListLines();
        createLabels();
        updateSize();
        updateLayout();
        updateLines();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getChildren().clear();
                getChildren().addAll(labels);
                getChildren().addAll(lines);
                if(weekSchedule.getChildren().indexOf(WeekScheduleDetail.this)<0)
                    weekSchedule.getChildren().add(WeekScheduleDetail.this);
            }
        });
    }
    private void updateLayout() {
        long columnsCount = 1 + YearMonth.getDateDiff(weekSchedule.fromDate, weekSchedule.toDate, TimeUnit.DAYS);
        double xUnit = getWidth()/columnsCount;
        double yUnit = yUnitProperty.get();
        for (int i=0; i < labels.size(); i++) {
            //TODO
            CalendarEvent jData = weekSchedule.calendarEventsProperty.get().get(i);
            (labels.get(i)).setLayoutY(yUnit * (rects.get(i).getLayoutY() + weekSchedule.indexOf(jData.value)) + weekSchedule.indexOf(jData.value) * yMarginProperty.get());
            (labels.get(i)).setLayoutX(rects.get(i).getLayoutX() * xUnit);
        }
    }
    public double computeMinYUnit() {
        Group root = new Group();
        Scene scene = new Scene(root);
        Label label = new Label();
        double maxHeight = 0.0;
        for (int i=0; i < labels.size(); i++) {
            label.setFont(labels.get(i).getFont());
            label.setBorder(labels.get(i).getBorder());
            label.setText(labels.get(i).getText());
            root.getChildren().setAll(label);
            root.applyCss();
            root.layout();
            double newValue = label.getHeight()/rects.get(i).getHeight();
            if (maxHeight < newValue)
                maxHeight = newValue;
        }
        return maxHeight;
    }
    private void updateSize() {
        long columnsCount =  1+YearMonth.getDateDiff(weekSchedule.fromDate, weekSchedule.toDate, TimeUnit.DAYS);
        double xUnit = getWidth()/columnsCount;
        double yUnit = yUnitProperty.get();
        for (int i=0; i < labels.size(); i++) {
            (labels.get(i)).setPrefWidth(rects.get(i).getWidth() * xUnit);
            double prefHeight = rects.get(i).getHeight() * yUnit;
            (labels.get(i)).setPrefHeight(prefHeight);
        }
    }
    private void updateLines() {
        double yUnit = yUnitProperty.get();
        for (int i=0; i < lines.size(); i++) {
            lines.get(i).setStartX(0.0);
            lines.get(i).setEndX(getWidth());
            lines.get(i).setStartY((i+1) * (yUnit + yMarginProperty.get()) -  yMarginProperty.get() / 2.0);
            lines.get(i).setEndY(lines.get(i).getStartY());
        };
    }
    private void createListLines() {
        lines.clear();
        for (int i=0; i<weekSchedule.sortedRowNamesProperty.get().size() - 1; i++) {
            Line line = new Line();
            line.getStyleClass().add("week_events_line");
            lines.add(line);
        };
    }
    private void createLabels() {
        rects.clear();
        labels.clear();
        Date d1 = new Date();
        Date d2 = new Date();
        for (int i=0; i < weekSchedule.calendarEventsProperty.get().size(); i++) {
            CalendarEvent jEvent =  weekSchedule.calendarEventsProperty.get().get(i);
            d1.setTime(jEvent.from);
            d2.setTime(jEvent.to);
            rects.add(createRect(d1, d2));
            Label label = new Label(weekSchedule.calendarEventsProperty.get().get(i).caption);
            label.getStyleClass().add("week_events");
            labels.add(label);
        }
    }
    private Rectangle createRect(Date from, Date to){
        Rectangle rect = new Rectangle();
        double minToStart = YearMonth.getDateDiff(weekSchedule.fromDate, from, TimeUnit.MINUTES);
        double dayToStart = (int)minToStart/60/24;
        minToStart -= dayToStart*60*24;
        double minLength = YearMonth.getDateDiff(from, to, TimeUnit.MINUTES);
        double dayLength = (int)minLength/60/24;
        minLength -= dayLength*60*24;
        if(minLength>0)
            dayLength++;
        rect.setHeight(minLength/minForDay);
        rect.setWidth(dayLength);
        rect.setLayoutX(dayToStart);
        rect.setLayoutY(minToStart/minForDay);
        return  rect;
    }
}
