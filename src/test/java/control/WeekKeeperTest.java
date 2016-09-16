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

    @Test
    public void testHideDay() throws Exception {

        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        faCalendar.set(Calendar.YEAR, 1395);
        faCalendar.set(Calendar.WEEK_OF_YEAR, 2);
        faCalendar.set(Calendar.DAY_OF_WEEK, 2);
        faCalendar.set(Calendar.HOUR_OF_DAY, 8);
        faCalendar.set(Calendar.MINUTE, 30);
        faCalendar.set(Calendar.SECOND, 0);
        faCalendar.set(Calendar.MILLISECOND, 0);
        obj.put("from", faCalendar.getTime().getTime());
        faCalendar.set(Calendar.DAY_OF_WEEK, 3);
        faCalendar.set(Calendar.HOUR_OF_DAY, 16);
        faCalendar.set(Calendar.MINUTE, 0);
        obj.put("to", faCalendar.getTime().getTime());
        obj.put("location", "204");
        arr.put(obj);
        weekKeeper.yAxisProperty.set("location");
        weekKeeper.dataProperty.set(arr);
        TimeUnit.SECONDS.sleep(50);
    }

    @Test
    public void testUnHideDay() throws Exception {

    }
    WeekKeeper weekKeeper = new WeekKeeper(1395, 2);
    @Override
    protected Parent getRootNode() {
        weekKeeper.setPrefSize(400, 200);
        weekKeeper.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        weekKeeper.setBorder(Main.regularBorder);
        return weekKeeper;
    }
}