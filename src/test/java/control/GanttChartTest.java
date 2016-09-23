package control;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by AliReza on 9/16/2016.
 */
public class GanttChartTest extends GuiTest {
    private final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    private static Calendar faCalendar = Calendar.getInstance(faLocale);
    private JSONObject createEvent(String caption, String location, String group, String trainer, int start, int end, int startHour, int startMin, int endHour, int endMin) throws Exception {
        JSONObject obj = new JSONObject();
        faCalendar.set(Calendar.YEAR, 1395);
        faCalendar.set(Calendar.WEEK_OF_YEAR, 2);
        faCalendar.set(Calendar.DAY_OF_WEEK, start);
        faCalendar.set(Calendar.HOUR_OF_DAY, startHour);
        faCalendar.set(Calendar.MINUTE, startMin);
        faCalendar.set(Calendar.SECOND, 0);
        faCalendar.set(Calendar.MILLISECOND, 0);
        obj.put("from", faCalendar.getTime().getTime());
        faCalendar.set(Calendar.DAY_OF_WEEK, end);
        faCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        faCalendar.set(Calendar.MINUTE, endMin);
        obj.put("to", faCalendar.getTime().getTime());
        obj.put("location", location);
        obj.put("group", group);
        obj.put("trainer", trainer);
        obj.put("caption", caption);
        return obj;
    }

    JSONArray arr = new JSONArray();
    @Before
    public void before() throws Exception{
        arr.put(createEvent("برنامه نویسی جاوا", "204", "EL1", "طباطبایی", 0 , 1, 8, 30, 10, 00));
        arr.put(createEvent("ACCSESS 2010", "204", "EL1", "نبی زاده", 2 , 3, 10, 00, 13, 00));
        arr.put(createEvent("آزمایشگاه مکانیک", "204", "EL1", "افضلان", 1 , 4, 13, 00, 16, 00));
        arr.put(createEvent("Word & Windows", "204", "EL1", "حق گو", 5 , 5, 8, 30, 14, 00));
        arr.put(createEvent("SQL 2000", "201", "EL1", "نادری", 6 , 6, 14, 00, 16, 00));

        arr.put(createEvent("برنامه نویسی جاوا", "201", "EL2", "طباطبایی", 1 , 5, 8, 30, 10, 00));
        arr.put(createEvent("SQL 2000", "204", "EL2", "نادری", 6 , 6, 8, 30, 10, 00));

        arr.put(createEvent("برنامه نویسی جاوا", "202", "EL3", "طباطبایی", 0 , 3, 8, 30, 9, 30));
        arr.put(createEvent("ACCSESS 2010", "202", "EL3", "نبی زاده", 3 , 5, 9, 30, 10, 30));
        arr.put(createEvent("آزمایشگاه مکانیک","202", "EL3", "افضلان", 5 , 6, 10, 30, 11, 30));

    }
    @Test
    public void testHideDay() throws InterruptedException {
        ganttChart.yAxisProperty.set("trainer");
        ganttChart.dataProperty.set(arr);
        ganttChartPlot = new GanttChartPlot(ganttChart);
        TimeUnit.MILLISECONDS.sleep(2000);
        ganttChartRight = new GanttChartRight(ganttChart);
        TimeUnit.MILLISECONDS.sleep(2000);
        ganttChartTop = new GanttChartTop(ganttChart);
        //ganttChart.setVisible(true);
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yAxisProperty.set("location");
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yAxisProperty.set("group");
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yMarginProperty.set(20);
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yMarginProperty.set(10);
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yUnitProperty.set(140);
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yUnitProperty.set(160);
        TimeUnit.SECONDS.sleep(2);
        ganttChart.yUnitProperty.set(ganttChartPlot.computeMinYUnit());
        TimeUnit.SECONDS.sleep(8);
        ganttChart.yUnitProperty.set(-1);
        TimeUnit.SECONDS.sleep(8);
    }
    @Test
    public void testUnHideDay() throws Exception {
    }
    GanttChart ganttChart;
    GanttChartTop ganttChartTop;
    GanttChartRight ganttChartRight;
    GanttChartPlot ganttChartPlot;
    @Override
    protected Parent getRootNode() {
        ganttChart = new GanttChart(1395, 2);
        ganttChart.setPrefHeight(800);
        ganttChart.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        return ganttChart;
    }
}