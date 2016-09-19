package control;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import main.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by AliReza on 9/16/2016.
 */
public class WeekKeeperTest extends GuiTest {
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
    @Test
    public void testHideDay() throws Exception {

        JSONArray arr = new JSONArray();
        arr.put(createEvent("برنامه نویسی جاوا", "204", "EL1", "طباطبایی", 0 , 1, 8, 30, 10, 00));
        arr.put(createEvent("ACCSESS 2010", "204", "EL1", "نبی زاده", 1 , 3, 10, 00, 13, 00));
        arr.put(createEvent("آزمایشگاه مکانیک", "204", "EL1", "افضلان", 0 , 4, 13, 00, 16, 00));
        arr.put(createEvent("Word & Windows", "204", "EL1", "حق گو", 5 , 5, 8, 30, 14, 00));
        arr.put(createEvent("SQL 2000", "201", "EL1", "نادری", 6 , 6, 14, 30, 16, 00));

        arr.put(createEvent("برنامه نویسی جاوا", "201", "EL2", "طباطبایی", 0 , 5, 8, 30, 10, 00));
        arr.put(createEvent("SQL 2000", "204", "EL2", "نادری", 6 , 6, 8, 30, 16, 00));

        arr.put(createEvent("برنامه نویسی جاوا", "202", "EL3", "طباطبایی", 0 , 3, 8, 30, 10, 30));
        arr.put(createEvent("ACCSESS 2010", "202", "EL3", "نبی زاده", 2 , 3, 11, 00, 13, 00));
        arr.put(createEvent("آزمایشگاه مکانیک","202", "EL3", "افضلان", 5 , 6, 13, 00, 16, 00));

        weekKeeper.yUnitProperty.setValue(110);
        weekKeeper.yAxisProperty.set("trainer");
        weekKeeper.dataProperty.set(arr);
        TimeUnit.SECONDS.sleep(4);
        weekKeeper.yAxisProperty.set("location");
        TimeUnit.SECONDS.sleep(4);
        weekKeeper.yAxisProperty.set("group");
        TimeUnit.SECONDS.sleep(4);
        weekKeeper.yUnitProperty.set(90);
        TimeUnit.SECONDS.sleep(4);
        weekKeeper.yMarginProperty.set(7);
        TimeUnit.SECONDS.sleep(2);
        weekKeeper.yUnitProperty.set(140);
        TimeUnit.SECONDS.sleep(2);
        weekKeeper.yUnitProperty.set(160);
        TimeUnit.SECONDS.sleep(2);
        weekKeeper.yUnitProperty.set(180);
        TimeUnit.SECONDS.sleep(2);
        weekKeeper.yUnitProperty.set(200);
    }

    @Test
    public void testUnHideDay() throws Exception {

    }
    WeekKeeper weekKeeper = new WeekKeeper(1395, 2);
    @Override
    protected Parent getRootNode() {
        weekKeeper.setPrefHeight(500);
        weekKeeper.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        return weekKeeper;
    }
}