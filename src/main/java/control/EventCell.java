package control;

import javafx.geometry.NodeOrientation;

import skin.YearMonth;
import java.util.Date;

/**
 * Created by Afzalan on 9/5/2016.
 */
public class EventCell extends DateCell {
    private Date _from = null;
    private Date _to = null;
    public EventCell(Date from, Date to) {
        super();

        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        _from = from;
        _to = to;
    }
    @Override public void updateItem(Date today, boolean empty) {
        super.updateItem(today, empty);
        getStyleClass().clear();
        getStyleClass().setAll("event-box");
        if(YearMonth.compare(today, _from) == -1)
            getStyleClass().add("event-next");
        else
            if( YearMonth.compare(today, _to) == 1)
                getStyleClass().add("event-previous");
            else
                getStyleClass().add("event-now");
    }
}
