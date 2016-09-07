package control;

import com.sun.javafx.css.converters.BooleanConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Afzalan on 9/6/2016.
 */
public class DatePickerExt extends DatePicker {
    public final BooleanProperty showCalendarEventsProperty() {
        if (showCalendarEvents == null) {
            boolean localizedDefault = false;
            showCalendarEvents = new StyleableBooleanProperty(localizedDefault) {
                @Override public CssMetaData<DatePicker,Boolean> getCssMetaData() {
                    return StyleablePropertiesExt.SHOW_CALENDAR_EVENTS;
                }

                @Override public Object getBean() {
                    return DatePickerExt.this;
                }

                @Override public String getName() {
                    return "showCalendarEvents";
                }
            };
        }
        return showCalendarEvents;
    }
    private BooleanProperty showCalendarEvents;
    public final void setShowCalendarEvents(boolean value) {
        showCalendarEventsProperty().setValue(value);
    }
    public final boolean isShowCalendarEvents() {
        return showCalendarEventsProperty().getValue();
    }
    protected static class StyleablePropertiesExt extends StyleableProperties {
        private static final CssMetaData<DatePicker, Boolean> SHOW_CALENDAR_EVENTS =
                new CssMetaData<DatePicker, Boolean>("-fx-show-calendar-events",
                        BooleanConverter.getInstance(), true) {
                    @Override public boolean isSettable(DatePicker n) {
                        return ((DatePickerExt)n).showCalendarEvents == null || !((DatePickerExt)n).showCalendarEvents.isBound();
                    }

                    @Override public StyleableProperty<Boolean> getStyleableProperty(DatePicker n) {
                        return (StyleableProperty<Boolean>)(WritableValue<Boolean>)((DatePickerExt)n).showCalendarEventsProperty();
                    }
                };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    SHOW_WEEK_NUMBERS, SHOW_CALENDAR_EVENTS
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleablePropertiesExt.STYLEABLES;
    }
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
