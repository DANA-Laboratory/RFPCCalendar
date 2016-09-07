package skin;

import control.DatePickerExt;
import javafx.scene.Node;
/**
 * Created by AliReza on 8/26/2016.
 */
public class DatePickerSkinExt extends DatePickerSkin {

    public DatePickerSkinExt(DatePickerExt datePicker) {
        super(datePicker);
        registerChangeListener(((DatePickerExt)datePicker).showCalendarEventsProperty(), "SHOW_CALENDAR_EVENTS");
    }
    @Override public Node getPopupContent() {
        if (datePickerContent == null) {
            datePickerContent = new DatePickerContentExt(datePicker);
        }
        return ((DatePickerContentExt)datePickerContent).getDatePickerEventsPane();
    }
    @Override protected void handleControlPropertyChanged(String p) {
        if ("SHOW_CALENDAR_EVENTS".equals(p)) {
            if (datePickerContent != null)
                datePickerContent.updateDayCells();
        } else
            super.handleControlPropertyChanged(p);
    }
}
