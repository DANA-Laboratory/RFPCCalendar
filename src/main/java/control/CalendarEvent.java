package control;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AliReza on 9/19/2016.
 */
class CalendarEvent {
    static ArrayList<CalendarEvent> parseArray(JSONArray arr) throws JSONException {
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
        for(int i=0; i<arr.length(); i++)
            calendarEvents.add(CalendarEvent.parseObject(arr.getJSONObject(i)));
        return calendarEvents;
    }
    static private CalendarEvent parseObject(JSONObject obj) throws JSONException {
        CalendarEvent event = new CalendarEvent();
        event.to = obj.getLong("to");
        event.from = obj.getLong("from");
        event.caption = obj.getString("caption");
        event.inner = obj;
        return event;
    }
    private String getString(String key) throws JSONException {
        return inner.getString(key);
    }
    void setKey(String key) throws JSONException {
        value = inner.getString(key);
    }
    long from;
    long to;
    String caption;
    String value;
    private JSONObject inner;
}
