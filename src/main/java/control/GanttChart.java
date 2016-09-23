package control;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import skin.YearMonth;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Afzalan on 9/19/2016.
 */
public class GanttChart extends BorderPane{

    //properties
    public SimpleObjectProperty<JSONArray> dataProperty = new SimpleObjectProperty<JSONArray>(null) {
        @Override public void set(JSONArray newVal) {
            super.set(newVal);
        }
    };
    public SimpleStringProperty yAxisProperty = new SimpleStringProperty(null)  {
       @Override public void set(String newVal) {
           super.set(newVal);
       }
    };
    public SimpleObjectProperty<SortedSet<String>> sortedRowNamesProperty = new SimpleObjectProperty<SortedSet<String>>(null);
    public SimpleObjectProperty<ArrayList<CalendarEvent>> calendarEventsProperty = new SimpleObjectProperty<ArrayList<CalendarEvent>>();
    //plot area
    public SimpleDoubleProperty yUnitProperty = null;
    public SimpleDoubleProperty yMarginProperty = null;
    //private WeekScheduleHeader header;
    private final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    private static com.ibm.icu.util.Calendar faCalendar = com.ibm.icu.util.Calendar.getInstance(faLocale);

    private static final int startHour = 8;
    private static final int endHour = 16;
    private static final int startMinute = 30;
    private static final int endMinute = 0;

    private double minutesOfDay = 0;
    public double getMinutesOfDay (){
        return minutesOfDay;
    }
    private long columnsCount = 0;
    public long getColumnsCount() {
        return columnsCount;
    }
    GanttChart(int year, int weekNumber) {
        this(calcWeekStart(year, weekNumber), calcWeekEnd(year, weekNumber));
    }
    GanttChart(Date d1, Date d2) {
        super();
        setDateAxis(d1, d2);
        columnsCount = 1 + YearMonth.getDateDiff(fromDate, toDate, TimeUnit.DAYS);
        minutesOfDay = YearMonth.getDateDiff(fromDate, toDate, TimeUnit.MINUTES);
        minutesOfDay -= (60*24)*((int)(minutesOfDay/(60*24)));

        dataProperty.addListener(new ChangeListener<JSONArray>() {
            @Override
            public void changed(ObservableValue<? extends JSONArray> observable, JSONArray oldValue, JSONArray newValue) {
                try {
                    calendarEventsProperty.set(CalendarEvent.parseArray(newValue));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        calendarEventsProperty.addListener(new ChangeListener<ArrayList<CalendarEvent>>() {
            @Override
            public void changed(ObservableValue<? extends ArrayList<CalendarEvent>> observable, ArrayList<CalendarEvent> oldValue, ArrayList<CalendarEvent> newValue) {
                if(yAxisProperty.get() != null) {
                    newValue.forEach((e) -> {
                        try {
                            e.setKey(yAxisProperty.get());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            return;
                        }
                    });
                    updateSortedRowNames();
                }
            }
        });
        yAxisProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(calendarEventsProperty.get() != null) {
                    calendarEventsProperty.get().forEach((e) -> {
                        try {
                            e.setKey(newValue);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            return;
                        }
                    });
                    updateSortedRowNames();
                }
            }
        });
    }

    public Date fromDate = null;
    public Date toDate = null;
    private void updateSortedRowNames() {
        SortedSet<String> sortedRowNames = new TreeSet<String>();
        sortedRowNames.clear();
        for (int i=0; i<calendarEventsProperty.get().size(); i++) {
            CalendarEvent jData = calendarEventsProperty.get().get(i);
            sortedRowNames.add(jData.value);
        }
        sortedRowNamesProperty.set(sortedRowNames);
    }
    public int indexOf(final String rowName) {
        Iterator<String> it = sortedRowNamesProperty.get().iterator();
        int i = 0;
        while(it.hasNext())
            if(rowName.compareTo(it.next()) == 0)
                return i;
            else
                i++;
        return -1;
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

}
