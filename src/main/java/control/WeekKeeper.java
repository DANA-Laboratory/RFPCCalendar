package control;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.*;
import com.ibm.icu.util.Calendar;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import main.Main;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import skin.YearMonth;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by AliReza on 9/15/2016.
 */
public class WeekKeeper extends Pane {
    public SimpleDoubleProperty yUnitProperty = new SimpleDoubleProperty(50) {
        @Override public double get() {
            return Math.min(super.get(),(getHeight() - (sortedRowNames.size()-1)* yMarginProperty.get())/sortedRowNames.size());
        }
    };
    public SimpleDoubleProperty yMarginProperty = new SimpleDoubleProperty(5);
    public static String fromDateString;
    public static String toDateString;
    public static String fromHmString;
    public static String toHmString;
    private static final int startHour = 8;
    private static final int endHour = 16;
    private static final int startMinute = 30;
    private static final int endMinute = 0;
    private Date fromDate = null;
    private Date toDate = null;
    private final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    private final static DateFormat faFormatterFull = DateFormat.getDateInstance(DateFormat.FULL, faLocale);
    private final static DateFormat faFormatterHm = DateFormat.getPatternInstance("HH:mm", faLocale);
    private static Calendar faCalendar = Calendar.getInstance(faLocale);

    //Properties
    public SimpleStringProperty yAxisProperty = new SimpleStringProperty(null);
    public SimpleObjectProperty<JSONArray> dataProperty = new SimpleObjectProperty<JSONArray>(null);

    private boolean[] hiddenDays = {false, false, false, false, false, true, true};
    private SortedSet<String> sortedRowNames = new TreeSet<>();
    private ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    private ArrayList<Label> labels = new ArrayList<Label>();
    private ArrayList<Line> lines = new ArrayList<Line>();

    private double minForDay = 0;
    WeekKeeper(int year, int weekNumber) {
        this(calcWeekStart(year, weekNumber), calcWeekEnd(year, weekNumber));
    }
    WeekKeeper(Date d1, Date d2) {
        getStylesheets().add(getClass().getResource("../RFPCCalendar.css").toExternalForm());
        setDateAxis(d1, d2);
        minForDay = YearMonth.getDateDiff(fromDate, toDate, TimeUnit.MINUTES);
        minForDay -= (60*24)*((int)(minForDay/(60*24)));
        toDateString = faFormatterFull.format(toDate);
        toHmString = faFormatterHm.format(toDate);
        fromDateString = faFormatterFull.format(fromDate);
        fromHmString = faFormatterHm.format(fromDate);
        yUnitProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && yAxisProperty.get() != null && dataProperty.get() != null)
                    try {
                        updateLayout();
                        updateSize();
                        updateLines(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        yMarginProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && yAxisProperty.get() != null && dataProperty.get() != null)
                    try {
                        updateLayout();
                        updateLines(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        dataProperty.addListener(new ChangeListener<JSONArray>() {
            @Override
            public void changed(ObservableValue<? extends JSONArray> observable, JSONArray oldValue, JSONArray newValue) {
                if(newValue != null && yAxisProperty.get() != null)
                    try {
                        ArrayList<Line> _lines = updateSortedRowNames();
                        createLabels();
                        updateSize();
                        updateLayout();
                        updateLines(_lines);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                getChildren().clear();
                                getChildren().addAll(labels);
                                lines.clear();
                                lines.addAll(_lines);
                                getChildren().addAll(lines);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && yAxisProperty.get() != null && dataProperty.get() != null)
                    try {
                        updateLayout();
                        updateSize();
                        updateLines(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && yAxisProperty.get() != null && dataProperty.get() != null)
                    try {
                        updateLayout();
                        updateSize();
                        updateLines(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
        yAxisProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null && dataProperty.get() != null)
                    try {
                        ArrayList<Line> _lines = updateSortedRowNames();
                        updateLayout();
                        updateLines(_lines);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                getChildren().removeAll(lines);
                                lines.clear();
                                lines.addAll(_lines);
                                getChildren().addAll(lines);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
    }
    private static Date calcWeekStart(int year, int week){
        faCalendar.set(Calendar.YEAR, year);
        faCalendar.set(Calendar.WEEK_OF_YEAR, week);
        faCalendar.set(Calendar.DAY_OF_WEEK, 0);
        faCalendar.set(Calendar.HOUR_OF_DAY, startHour);
        faCalendar.set(Calendar.MINUTE, startMinute);
        faCalendar.set(Calendar.SECOND, 0);
        faCalendar.set(Calendar.MILLISECOND, 0);
        return faCalendar.getTime();
    }
    private static Date calcWeekEnd(int year, int week){
        faCalendar.set(Calendar.YEAR, year);
        faCalendar.set(Calendar.WEEK_OF_YEAR, week);
        faCalendar.set(Calendar.DAY_OF_WEEK, 6);
        faCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        faCalendar.set(Calendar.MINUTE, endMinute);
        faCalendar.set(Calendar.SECOND, 0);
        faCalendar.set(Calendar.MILLISECOND, 0);
        return faCalendar.getTime();
    }
    private void setDateAxis(Date d1, Date d2){
        if (d1.getTime() < d2.getTime()) {
            fromDate = d1;
            toDate = d2;
        } else {
            fromDate = d2;
            toDate = d1;
        }
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
    };
    private void updateLayout() throws JSONException {
        long columnsCount = 1 + YearMonth.getDateDiff(fromDate, toDate, TimeUnit.DAYS);
        double xUnit = getWidth()/columnsCount;
        for (int i=0; i < labels.size(); i++) {
            //TODO
            JSONObject jData = dataProperty.get().getJSONObject(i);
            (labels.get(i)).setLayoutY(yUnitProperty.get() * (rects.get(i).getLayoutY() + indexOf(jData.getString(yAxisProperty.get()))) + indexOf(jData.getString(yAxisProperty.get())) * yMarginProperty.get());
            (labels.get(i)).setLayoutX(rects.get(i).getLayoutX() * xUnit);
        }
    }
    private void updateSize() {
        long columnsCount =  1+YearMonth.getDateDiff(fromDate, toDate, TimeUnit.DAYS);
        double xUnit = getWidth()/columnsCount;
        for (int i=0; i < labels.size(); i++) {
            (labels.get(i)).setPrefWidth(rects.get(i).getWidth() * xUnit);
            (labels.get(i)).setPrefHeight(rects.get(i).getHeight() * yUnitProperty.get());
        }
    }
    private void updateLines(ArrayList<Line> _lines) {
        if(_lines == null)
            _lines = lines;
        for (int i=0; i < _lines.size(); i++) {
            _lines.get(i).setStartX(0.0);
            _lines.get(i).setEndX(getWidth());
            _lines.get(i).setStartY((i+1) * (yUnitProperty.get() + yMarginProperty.get()) -  yMarginProperty.get() / 2.0);
            _lines.get(i).setEndY(_lines.get(i).getStartY());
        };
    }
    private ArrayList<Line> updateSortedRowNames() throws JSONException {
        sortedRowNames.clear();
        for (int i=0; i<dataProperty.get().length(); i++) {
            JSONObject jData = dataProperty.get().getJSONObject(i);
            sortedRowNames.add(jData.getString(yAxisProperty.get()));
        }
        ArrayList<Line> _lines = new ArrayList<Line>();
        for (int i=0; i<sortedRowNames.size() - 1; i++) {
            Line line = new Line();
            line.getStyleClass().add("week_events_line");
            _lines.add(line);
        };
        return _lines;
    }
    private int indexOf(final String rowName) {
        Iterator<String> it = sortedRowNames.iterator();
        int i = 0;
        while(it.hasNext())
            if(rowName.compareTo(it.next()) == 0)
                return i;
            else
                i++;
        return -1;
    }
    private void createLabels() throws JSONException {
        rects.clear();
        labels.clear();
        Date d1 = new Date();
        Date d2 = new Date();
        for (int i=0; i < dataProperty.get().length(); i++) {
            JSONObject jEvent = null;
            jEvent = dataProperty.get().getJSONObject(i);
            d1.setTime(jEvent.getLong("from"));
            d2.setTime(jEvent.getLong("to"));
            rects.add(createRect(d1, d2));
            Label label = new Label(dataProperty.get().getJSONObject(i).getString("caption"));
            label.getStyleClass().add("week_events");
            labels.add(label);
        }
    }
    private Rectangle createRect(Date from, Date to){
        Rectangle rect = new Rectangle();
        double minToStart = YearMonth.getDateDiff(fromDate, from, TimeUnit.MINUTES);
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
