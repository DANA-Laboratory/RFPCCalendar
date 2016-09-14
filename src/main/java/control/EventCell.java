package control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import converter.LocalDateStringConverter;

import org.json.JSONException;
import org.json.JSONObject;
import skin.YearMonth;
import java.util.Date;
import java.util.List;

/**
 * Created by Afzalan on 9/5/2016.
 */
public class EventCell extends DateCell {
    private LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter();
    private class EventData {
        EventData(JSONObject jsonData){

        }
    }
    private class ClassEventData extends EventData {
        ClassEventData(JSONObject jsonData) {
            super(jsonData);
            try {
                caption = jsonData.getString("caption");
                location = jsonData.getString("location");
                trainer = jsonData.getString("trainer");
                code = jsonData.getString("code");
                from = localDateStringConverter.fromString(jsonData.getString("from"));
                to =  localDateStringConverter.fromString(jsonData.getString("to"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String caption;
        Date from;
        Date to;
        String location;
        String trainer;
        String code;
    }
    private ClassEventData eventData;
    public EventCell(JSONObject jsonData, Bounds bounds1, Bounds bounds2) {
        super();
        eventData = new ClassEventData(jsonData);
        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        double minX = Math.min(bounds1.getMinX(), bounds2.getMinX());
        double minY = Math.min(bounds1.getMinY(), bounds2.getMinY());
        double height = Math.max(bounds1.getMaxY(), bounds2.getMaxY()) - minY;
        double width = Math.max(bounds1.getMaxX(), bounds2.getMaxX()) - minX;
        setPrefHeight(height);
        setPrefWidth(width);
        setLayoutX(minX + width);
        setLayoutY(minY);
        setText(eventData.caption);
        updateItem(eventData.from, false);

    }
    @Override public void updateItem(Date from, boolean empty) {
        super.updateItem(from, empty);
        eventData.from = from;
        Date today = new Date();
        getStyleClass().clear();
        getStyleClass().setAll("event-box");
        if(YearMonth.compare(today, from) == -1)
            getStyleClass().add("event-next");
        else
            if( YearMonth.compare(today, eventData.to) == 1)
                getStyleClass().add("event-previous");
            else
                getStyleClass().add("event-now");
    }
}
