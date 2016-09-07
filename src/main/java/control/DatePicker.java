/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package control;

// editor and converter code in sync with ComboBox 4858:e60e9a5396e6

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.util.Callback;
import javafx.util.StringConverter;
import converter.LocalDateStringConverter;

import com.sun.javafx.css.converters.BooleanConverter;
import skin.DatePickerSkin;

import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/**
 * The DatePicker control allows the user to enter a date as text or
 * to select a date from a calendar popup. The calendar is based on
 * either the standard ISO-8601 chronology or any of the other
 * chronology classes defined in the java.time.chrono package.
 *
 * <p>The {@link #valueProperty() value} property represents the
 * currently selected {@link java.time.LocalDate}.  An initial date can
 * be set via the {@link #DatePicker(java.util.Date) constructor}
 * or by calling {@link #setValue(java.util.Date) setValue()}.  The
 * default value is null.
 *
 * <pre><code>
 * final DatePicker datePicker = new DatePicker();
 * datePicker.setOnAction(new EventHandler() {
 *     public void handle(Event t) {
 *         LocalDate date = datePicker.getValue();
 *         System.err.println("Selected date: " + date);
 *     }
 * });
 * </code></pre>
 *
 */
public class DatePicker extends ComboBoxBase<Date> {

    private Date lastValidDate = null;

    /**
     * Creates a default DatePicker instance with a <code>null</code> date value set.
     */
    public DatePicker() {
        this(null);

        valueProperty().addListener(observable -> {
            Date date = getValue();

            if (validateDate(date)) {
                lastValidDate = date;
            } else {
                System.err.println("Restoring value to " +
                            ((lastValidDate == null) ? "null" : getConverter().toString(lastValidDate)));
                setValue(lastValidDate);
            }
        });

    }

    private boolean validateDate(Date date) {
        return true; //TODO any validation
    }

    /**
     * Creates a DatePicker instance and sets the
     * {@link #valueProperty() value} to the given date.
     *
     * @param localDate to be set as the currently selected date in the DatePicker. Can be null.
     */
    public DatePicker(Date localDate) {
        setValue(localDate);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setAccessibleRole(AccessibleRole.DATE_PICKER);
        setEditable(true);
    }


    /***************************************************************************
     *                                                                         *
     * Properties                                                                 *
     *                                                                         *
     **************************************************************************/


    /**
     * A custom cell factory can be provided to customize individual
     * day cells in the DatePicker popup. Refer to {@link javafx.scene.control.DateCell}
     * and {@link javafx.scene.control.Cell} for more information on cell factories.
     * Example:
     *
     * <pre><code>
     * final Callback&lt;DatePicker, DateCell&gt; dayCellFactory = new Callback&lt;DatePicker, DateCell&gt;() {
     *     public DateCell call(final DatePicker datePicker) {
     *         return new DateCell() {
     *             &#064;Override public void updateItem(LocalDate item, boolean empty) {
     *                 super.updateItem(item, empty);
     *
     *                 if (MonthDay.from(item).equals(MonthDay.of(9, 25))) {
     *                     setTooltip(new Tooltip("Happy Birthday!"));
     *                     setStyle("-fx-background-color: #ff4444;");
     *                 }
     *                 if (item.equals(LocalDate.now().plusDays(1))) {
     *                     // Tomorrow is too soon.
     *                     setDisable(true);
     *                 }
     *             }
     *         };
     *     }
     * };
     * datePicker.setDayCellFactory(dayCellFactory);
     * </code></pre>
     *
     * @defaultValue null
     */
    private ObjectProperty<Callback<DatePicker, DateCell>> dayCellFactory;
    public final void setDayCellFactory(Callback<DatePicker, DateCell> value) {
        dayCellFactoryProperty().set(value);
    }
    public final Callback<DatePicker, DateCell> getDayCellFactory() {
        return (dayCellFactory != null) ? dayCellFactory.get() : null;
    }
    public final ObjectProperty<Callback<DatePicker, DateCell>> dayCellFactoryProperty() {
        if (dayCellFactory == null) {
            dayCellFactory = new SimpleObjectProperty<Callback<DatePicker, DateCell>>(this, "dayCellFactory");
        }
        return dayCellFactory;
    }



    /**
     * The calendar system used for parsing, displaying, and choosing
     * dates in the DatePicker control.
     *
     * <p>The default value is returned from a call to
     * {@code Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT))}.
     * The default is usually {@link java.time.chrono.IsoChronology} unless
     * provided explicitly in the {@link java.util.Locale} by use of a
     * Locale calendar extension.
     *
     * Setting the value to <code>null</code> will restore the default
     * chronology.
     */

    /**
     * Whether the DatePicker popup should display a column showing
     * week numbers.
     *
     * <p>The default value is specified in a resource bundle, and
     * depends on the country of the current locale.
     */
    public final BooleanProperty showWeekNumbersProperty() {
        if (showWeekNumbers == null) {
            boolean localizedDefault = true;
            showWeekNumbers = new StyleableBooleanProperty(localizedDefault) {
                @Override public CssMetaData<DatePicker,Boolean> getCssMetaData() {
                    return StyleableProperties.SHOW_WEEK_NUMBERS;
                }

                @Override public Object getBean() {
                    return DatePicker.this;
                }

                @Override public String getName() {
                    return "showWeekNumbers";
                }
            };
        }
        return showWeekNumbers;
    }
    private BooleanProperty showWeekNumbers;
    public final void setShowWeekNumbers(boolean value) {
        showWeekNumbersProperty().setValue(value);
    }
    public final boolean isShowWeekNumbers() {
        return showWeekNumbersProperty().getValue();
    }
    public final ObjectProperty<StringConverter<Date>> converterProperty() { return converter; }
    private ObjectProperty<StringConverter<Date>> converter =
            new SimpleObjectProperty<StringConverter<Date>>(this, "converter", null);
    public final void setConverter(StringConverter<Date> value) { converterProperty().set(value); }
    public final StringConverter<Date> getConverter() {
        StringConverter<Date> converter = converterProperty().get();
        if (converter != null) {
            return converter;
        } else {
            return defaultConverter;
        }
    }

    // Create a symmetric (format/parse) converter with the default locale.
    private StringConverter<Date> defaultConverter =
            new LocalDateStringConverter();


    // --- Editor
    /**
     * The editor for the DatePicker.
     *
     * @see javafx.scene.control.ComboBox#editorProperty
     */
    private ReadOnlyObjectWrapper<TextField> editor;
    public final TextField getEditor() {
        return editorProperty().get();
    }
    public final ReadOnlyObjectProperty<TextField> editorProperty() {
        if (editor == null) {
            editor = new ReadOnlyObjectWrapper<TextField>(this, "editor");
            editor.set(new ComboBoxListViewSkin.FakeFocusTextField());
        }
        return editor.getReadOnlyProperty();
    }

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new DatePickerSkin(this);
    }


    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "date-picker";

     /**
      * @treatAsPrivate implementation detail
      */
    protected static class StyleableProperties {
        protected static final CssMetaData<DatePicker, Boolean> SHOW_WEEK_NUMBERS =
              new CssMetaData<DatePicker, Boolean>("-fx-show-week-numbers",
                   BooleanConverter.getInstance(), true) {
            @Override public boolean isSettable(DatePicker n) {
                return n.showWeekNumbers == null || !n.showWeekNumbers.isBound();
            }

            @Override public StyleableProperty<Boolean> getStyleableProperty(DatePicker n) {
                return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.showWeekNumbersProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                SHOW_WEEK_NUMBERS
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    /***************************************************************************
     *                                                                         *
     * Accessibility handling                                                  *
     *                                                                         *
     **************************************************************************/

    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case DATE: return getValue();
            case TEXT: {
                String accText = getAccessibleText();
                if (accText != null && !accText.isEmpty()) return accText;

                Date date = getValue();
                StringConverter<Date> c = getConverter();
                if (date != null && c != null) {
                    return c.toString(date);
                }
                return "";
            }
            default: return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

}
