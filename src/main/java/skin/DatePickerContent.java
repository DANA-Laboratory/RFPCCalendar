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

package skin;

import java.time.DateTimeException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import control.DatePicker;
import control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import com.sun.javafx.scene.control.skin.resources.ControlResources;

import static com.sun.javafx.PlatformUtil.*;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

/**
 * The full content for the DatePicker popup. This class could
 * probably be used more or less as-is with an embeddable type of date
 * picker that doesn't use a popup.
 */

public class DatePickerContent extends VBox {

    protected ULocale faLocale = new ULocale("fa_IR@calendar=persian");

    protected DatePicker datePicker;
    private Button backMonthButton;
    private Button forwardMonthButton;
    private Button backYearButton;
    private Button forwardYearButton;
    private Label monthLabel;
    private Label yearLabel;
    protected GridPane gridPane;
    protected int daysPerWeek = 7;
    private int firstDayOfWeek = 7;
    private int monthsPerYear = 12;

    private List<DateCell> dayNameCells = new ArrayList<DateCell>();
    protected List<DateCell> weekNumberCells = new ArrayList<DateCell>();
    protected List<DateCell> dayCells = new ArrayList<DateCell>();
    protected Date[] dayCellDates;
    private DateCell lastFocusedDayCell = null;

    final DateFormat faMonthFormatter =
        DateFormat.getPatternInstance("MMMM", faLocale);

    final DateFormat faMonthFormatterSO =
        DateFormat.getPatternInstance("LLLL", faLocale);

    final DateFormat faYearFormatter =
        DateFormat.getPatternInstance("y", faLocale);

    final DateFormat faYearWithEraFormatter =
        DateFormat.getPatternInstance("GGGGy", faLocale);

    final DateFormat faWeekNumberFormatter =
        DateFormat.getPatternInstance("w", faLocale);

    final DateFormat faWeekDayNameFormatter =
        DateFormat.getPatternInstance("ccc", faLocale);

    final DateFormat faDayCellFormatter =
        DateFormat.getPatternInstance("d", faLocale);

    static String getString(String key) {
        return ControlResources.getString("DatePicker."+key);
    }

    DatePickerContent(final DatePicker datePicker) {
        this.datePicker = datePicker;
        getStyleClass().add("date-picker-popup");

        {
            Date date = datePicker.getValue();
            displayedYearMonth.set((date != null) ? new YearMonth(date) : YearMonth.now());
        }

        displayedYearMonth.addListener((observable, oldValue, newValue) -> {
            updateValues();
        });


        getChildren().add(createMonthYearPane());

        gridPane = new GridPane() {
            @Override protected double computePrefWidth(double height) {
                final double width = super.computePrefWidth(height);
                // RT-30903: Make sure width snaps to pixel when divided by
                // number of columns. GridPane doesn't do this with percentage
                // width constraints. See GridPane.adjustColumnWidths().
                final int nCols = daysPerWeek + (datePicker.isShowWeekNumbers() ? 1 : 0);
                final double snaphgap = snapSpace(getHgap());
                final double left = snapSpace(getInsets().getLeft());
                final double right = snapSpace(getInsets().getRight());
                final double hgaps = snaphgap * (nCols - 1);
                final double contentWidth = width - left - right - hgaps;
                return ((snapSize(contentWidth / nCols)) * nCols) + left + right + hgaps;
            }

            @Override protected void layoutChildren() {
                // Prevent AssertionError in GridPane
                if (getWidth() > 0 && getHeight() > 0) {
                    super.layoutChildren();
                }
            }
        };
        gridPane.setFocusTraversable(true);
        gridPane.getStyleClass().add("calendar-grid");
        gridPane.setVgap(-1);
        gridPane.setHgap(-1);

        gridPane.focusedProperty().addListener((ov, t, hasFocus) -> {
            if (hasFocus) {
                if (lastFocusedDayCell != null) {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            lastFocusedDayCell.requestFocus();
                        }
                    });
                } else {
                    clearFocus();
                }
            }
        });

        // get the weekday labels starting with the weekday that is the
        // first-day-of-the-week according to the locale in the
        // displayed LocalDate
        for (int i = 0; i < daysPerWeek; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("day-name-cell");
            dayNameCells.add(cell);
        }

        // Week number column
        for (int i = 0; i < 6; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("week-number-cell");
            weekNumberCells.add(cell);
        }

        createDayCells();
        updateGrid();
        getChildren().add(gridPane);

        refresh();

        // RT-30511: This prevents key events from reaching the popup's owner.
        addEventHandler(KeyEvent.ANY, e -> {
            Node node = getScene().getFocusOwner();
            if (node instanceof DateCell) {
                lastFocusedDayCell = (DateCell)node;
            }

            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (e.getCode()) {
                  case HOME:
                      goToDate(new Date(), true);
                      e.consume();
                      break;


                  case PAGE_UP:
                      if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
                          if (!backYearButton.isDisabled()) {
                              forward(-1, Calendar.YEAR, true);
                          }
                      } else {
                          if (!backMonthButton.isDisabled()) {
                              forward(-1, Calendar.MONTH, true);
                          }
                      }
                      e.consume();
                      break;

                  case PAGE_DOWN:
                      if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
                          if (!forwardYearButton.isDisabled()) {
                              forward(1, Calendar.YEAR, true);
                          }
                      } else {
                          if (!forwardMonthButton.isDisabled()) {
                              forward(1, Calendar.MONTH, true);
                          }
                      }
                      e.consume();
                      break;
                }

                node = getScene().getFocusOwner();
                if (node instanceof DateCell) {
                    lastFocusedDayCell = (DateCell)node;
                }
            }

            // Consume all key events except those that control
            // showing the popup and traversal.
            switch (e.getCode()) {
              case ESCAPE:
              case F4:
              case F10:
              case UP:
              case DOWN:
              case LEFT:
              case RIGHT:
              case TAB:
                    break;

              default:
                e.consume();
            }
        });
    }

    private ObjectProperty<YearMonth> displayedYearMonth =
        new SimpleObjectProperty<YearMonth>(this, "displayedYearMonth");

    ObjectProperty<YearMonth> displayedYearMonthProperty() {
        return displayedYearMonth;
    }

    protected BorderPane createMonthYearPane() {
        BorderPane monthYearPane = new BorderPane();
        monthYearPane.getStyleClass().add("month-year-pane");

        // Month spinner

        HBox monthSpinner = new HBox();
        monthSpinner.getStyleClass().add("spinner");

        backMonthButton = new Button();
        backMonthButton.getStyleClass().add("left-button");

        forwardMonthButton = new Button();
        forwardMonthButton.getStyleClass().add("right-button");

        StackPane leftMonthArrow = new StackPane();
        leftMonthArrow.getStyleClass().add("left-arrow");
        leftMonthArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        backMonthButton.setGraphic(leftMonthArrow);

        StackPane rightMonthArrow = new StackPane();
        rightMonthArrow.getStyleClass().add("right-arrow");
        rightMonthArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        forwardMonthButton.setGraphic(rightMonthArrow);

        backMonthButton.setOnAction(t -> {
            forward(1, Calendar.MONTH, false);
        });

        monthLabel = new Label();
        monthLabel.getStyleClass().add("spinner-label");

        forwardMonthButton.setOnAction(t -> {
            forward(-1, Calendar.MONTH, false);
        });

        monthSpinner.getChildren().addAll(backMonthButton, monthLabel, forwardMonthButton);
        monthYearPane.setLeft(monthSpinner);

        // Year spinner

        HBox yearSpinner = new HBox();
        yearSpinner.getStyleClass().add("spinner");

        backYearButton = new Button();
        backYearButton.getStyleClass().add("left-button");

        forwardYearButton = new Button();
        forwardYearButton.getStyleClass().add("right-button");

        StackPane leftYearArrow = new StackPane();
        leftYearArrow.getStyleClass().add("left-arrow");
        leftYearArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        backYearButton.setGraphic(leftYearArrow);

        StackPane rightYearArrow = new StackPane();
        rightYearArrow.getStyleClass().add("right-arrow");
        rightYearArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        forwardYearButton.setGraphic(rightYearArrow);


        backYearButton.setOnAction(t -> {
            forward(1, Calendar.YEAR, false);
        });

        yearLabel = new Label();
        yearLabel.getStyleClass().add("spinner-label");

        forwardYearButton.setOnAction(t -> {
            forward(-1, Calendar.YEAR, false);
        });

        yearSpinner.getChildren().addAll(backYearButton, yearLabel, forwardYearButton);
        yearSpinner.setFillHeight(false);
        monthYearPane.setRight(yearSpinner);

        return monthYearPane;
    }

    private void refresh() {
        updateMonthLabelWidth();
        updateDayNameCells();
        updateValues();
    }

    void updateValues() {
        // Note: Preserve this order, as DatePickerHijrahContent needs
        // updateDayCells before updateMonthYearPane().
        updateWeeknumberDateCells();
        updateDayCells();
        updateMonthYearPane();
    }

    void updateGrid() {
        gridPane.getColumnConstraints().clear();
        gridPane.getChildren().clear();

        int nCols = daysPerWeek + (datePicker.isShowWeekNumbers() ? 1 : 0);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100); // Treated as weight
        for (int i = 0; i < nCols; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < daysPerWeek; i++) {
            gridPane.add(dayNameCells.get(i), i + nCols - daysPerWeek, 1);  // col, row
        }

        // Week number column
        if (datePicker.isShowWeekNumbers()) {
            for (int i = 0; i < 6; i++) {
                gridPane.add(weekNumberCells.get(i), 0, i + 2);  // col, row
            }
        }

        // setup: 6 rows of daysPerWeek (which is the maximum number of cells required in the worst case layout)
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                gridPane.add(dayCells.get(row*daysPerWeek+col), col + nCols - daysPerWeek, row + 2);
            }
        }
    }

    void updateDayNameCells() {
        Calendar calendar = Calendar.getInstance(faLocale); // 1395/5/29 is Friday
        calendar.set(1395, 4, 29 + firstDayOfWeek);
        for (int i = 0; i < daysPerWeek; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String faName = faWeekDayNameFormatter.format(calendar);
            dayNameCells.get(i).setText(titleCaseWord(faName));
        }
    }

    void updateWeeknumberDateCells() {
        if (datePicker.isShowWeekNumbers()) {
            final int maxWeeksPerMonth = 6;
            Calendar firstOfMonth = Calendar.getInstance(faLocale);
            firstOfMonth.setTime(displayedYearMonth.get().atDay(1));
            for (int i = 0; i < maxWeeksPerMonth; i++) {
                String faCellText = faWeekNumberFormatter.format(firstOfMonth);//TODO
                weekNumberCells.get(i).setText(faCellText);
                firstOfMonth.add(Calendar.WEEK_OF_MONTH, 1);
            }
        }
    }

    void updateDayCells() {
        int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();
        YearMonth curMonth = displayedYearMonth.get();

        // RT-31075: The following are now set in the try-catch block.
        YearMonth prevMonth = null;
        YearMonth nextMonth = null;
        int daysInCurMonth = -1;
        int daysInPrevMonth = -1;
        int daysInNextMonth = -1;

        for (int i = 0; i < 6 * daysPerWeek; i++) {
            DateCell dayCell = dayCells.get(i);
            dayCell.getStyleClass().setAll("cell", "date-cell", "day-cell");
            dayCell.setDisable(false);
            dayCell.setStyle(null);
            dayCell.setGraphic(null);
            dayCell.setTooltip(null);

            try {
                if (daysInCurMonth == -1) {
                    daysInCurMonth = curMonth.lengthOfMonth();
                }
                YearMonth month = curMonth;
                int day = i - firstOfMonthIdx + 1;
                //int index = firstOfMonthIdx + i - 1;
                if (i < firstOfMonthIdx) {
                    if (prevMonth == null) {
                        prevMonth = curMonth.minusMonths(1);
                        daysInPrevMonth = prevMonth.lengthOfMonth();
                    }
                    month = prevMonth;
                    day = i + daysInPrevMonth - firstOfMonthIdx + 1;
                    dayCell.getStyleClass().add("previous-month");
                } else if (i >= firstOfMonthIdx + daysInCurMonth) {
                    if (nextMonth == null) {
                        nextMonth = curMonth.plusMonths(1);
                        daysInNextMonth = nextMonth.lengthOfMonth();
                    }
                    month = nextMonth;
                    day = i - daysInCurMonth - firstOfMonthIdx + 1;
                    dayCell.getStyleClass().add("next-month");
                }
                Date date = month.atDay(day);
                dayCellDates[i] = date;

                dayCell.setDisable(false);

                if (isToday(date)) {
                    dayCell.getStyleClass().add("today");
                }

                if (YearMonth.equals(date, datePicker.getValue())) {
                    dayCell.getStyleClass().add("selected");
                }

                String faCellText = faDayCellFormatter.format(date);//TODO
                dayCell.setText(faCellText);
                dayCell.updateItem(date, false);
            } catch (DateTimeException ex) {
                // Date is out of range.
                // System.err.println(dayCellDate(dayCell) + " " + ex);
                dayCell.setText(" ");
                dayCell.setDisable(true);
            }
        }
    }

    private void updateMonthLabelWidth() {
        Calendar calendar = Calendar.getInstance(faLocale);
        YearMonth yearMonth = displayedYearMonth.get();
        calendar.set(Calendar.MONTH, yearMonth.getMonthValue());
        calendar.set(Calendar.YEAR, yearMonth.getYear());
        if (monthLabel != null) {
            double width = 0;
            for (int i = 0; i < monthsPerYear; i++) {
                String faName = faMonthFormatterSO.format(calendar);//TODO
                calendar.add(Calendar.MONTH, 1);
                if (Character.isDigit(faName.charAt(0))) {
                    faName = yearMonth.getMonth();
                }
                width = Math.max(width, Utils.computeTextWidth(monthLabel.getFont(), faName, 0));
            }
            monthLabel.setMinWidth(width);
        }
    }

    protected void updateMonthYearPane() {
        YearMonth yearMonth = displayedYearMonth.get();
        String str = formatMonth(yearMonth);
        monthLabel.setText(str);

        str = formatYear(yearMonth);
        yearLabel.setText(str);
        double width = Utils.computeTextWidth(yearLabel.getFont(), str, 0);
        if (width > yearLabel.getMinWidth()) {
            yearLabel.setMinWidth(width);
        }

        Calendar firstDayOfMonth = Calendar.getInstance(faLocale);
        firstDayOfMonth.setTime(yearMonth.atDay(1));
        backMonthButton.setDisable(!isValidDate(firstDayOfMonth, -1, Calendar.MONTH));
        forwardMonthButton.setDisable(!isValidDate(firstDayOfMonth, +1, Calendar.MONTH));
        backYearButton.setDisable(!isValidDate(firstDayOfMonth, -1, Calendar.YEAR));
        forwardYearButton.setDisable(!isValidDate(firstDayOfMonth, +1, Calendar.YEAR));
    }

    private String formatMonth(YearMonth yearMonth) {
        try {
            Date cDate = yearMonth.atDay(1);

            String faStr = faMonthFormatterSO.format(cDate);
            if (Character.isDigit(faStr.charAt(0))) {
                // Fallback. The standalone format returned a number, so use standard format instead.
                faStr = faMonthFormatter.format(cDate);
            }
            return titleCaseWord(faStr);
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    private String formatYear(YearMonth yearMonth) {
        try {
            Date cDate = yearMonth.atDay(1);
            String faStr = faYearWithEraFormatter.format(cDate); //TODO String faStr = faYearFormatter.format(cDate);
            return faStr;
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    // Ensures that month and day names are titlecased (capitalized).
    private String titleCaseWord(String str) {
        if (str.length() > 0) {
            int firstChar = str.codePointAt(0);
            if (!Character.isTitleCase(firstChar)) {
                str = new String(new int[] { Character.toTitleCase(firstChar) }, 0, 1) +
                      str.substring(Character.offsetByCodePoints(str, 0, 1));
            }
        }
        return str;
    }

    /**
     * determine on which day of week idx the first of the months is
     */
    private int determineFirstOfMonthDayOfWeek() {
        // determine with which cell to start
        Calendar calendar = Calendar.getInstance(faLocale);
        calendar.setTime(displayedYearMonth.get().atDay(1));
        int firstOfMonthIdx = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek; //TODO may buggy
        if (firstOfMonthIdx < 0) {
            firstOfMonthIdx += daysPerWeek;
        }
        return firstOfMonthIdx;
    }

    private boolean isToday(Date localDate) {
        return YearMonth.isToday(localDate);
    }

    protected Date dayCellDate(DateCell dateCell) {
        assert (dayCellDates != null);
        return dayCellDates[dayCells.indexOf(dateCell)];
    }

    // public for behavior class
    public void goToDayCell(DateCell dateCell, int offset, int unit, boolean focusDayCell) {
        Calendar calendar = Calendar.getInstance(faLocale);
        calendar.setTime(dayCellDate(dateCell));
        calendar.add(unit, offset);
        goToDate(calendar.getTime(), focusDayCell);
    }

    protected void forward(int offset, int unit, boolean focusDayCell) {
        YearMonth yearMonth = displayedYearMonth.get();
        DateCell dateCell = lastFocusedDayCell;
        if (dateCell == null || !yearMonth.isInSameMonth(dayCellDate(dateCell))) {
            dateCell = findDayCellForDate(yearMonth.atDay(1));
        }
        goToDayCell(dateCell, offset, unit, focusDayCell);
    }

    // public for behavior class
    public void goToDate(Date date, boolean focusDayCell) {
        if (isValidDate(date)) {
            displayedYearMonth.set(new YearMonth(date));
            if (focusDayCell) {
                findDayCellForDate(date).requestFocus();
            }
        }
    }

    // public for behavior class
    public void selectDayCell(DateCell dateCell) {
        datePicker.setValue(dayCellDate(dateCell));
        datePicker.hide();
    }

    protected DateCell findDayCellForDate(Date date) {
        for (int i = 0; i < dayCellDates.length; i++) {
            if (YearMonth.equals(date, dayCellDates[i])) {
                return dayCells.get(i);
            }
        }
        return null;//return dayCells.get(dayCells.size()/2+1);
    }

    void clearFocus() {
        Date focusDate = datePicker.getValue();
        if (focusDate == null) {
            focusDate = new Date();
        }
        if (new YearMonth(focusDate).equals(displayedYearMonth.get())) {
            // focus date
            goToDate(focusDate, true);
        } else {
            // focus month spinner (should not happen)
            backMonthButton.requestFocus();
        }

        // RT-31857
        if (backMonthButton.getWidth() == 0) {
            backMonthButton.requestLayout();
            forwardMonthButton.requestLayout();
            backYearButton.requestLayout();
            forwardYearButton.requestLayout();
        }
    }

    protected void createDayCells() {
        final EventHandler<MouseEvent> dayCellActionHandler = ev -> {
            if (ev.getButton() != MouseButton.PRIMARY) {
                return;
            }

            DateCell dayCell = (DateCell)ev.getSource();
            selectDayCell(dayCell);
            lastFocusedDayCell = dayCell;
        };

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                DateCell dayCell = createDayCell();
                dayCell.addEventHandler(MouseEvent.MOUSE_CLICKED, dayCellActionHandler);
                dayCells.add(dayCell);
            }
        }

        dayCellDates = new Date[6 * daysPerWeek];
    }

    private DateCell createDayCell() {
        DateCell cell = null;
        if (datePicker.getDayCellFactory() != null) {
            cell = datePicker.getDayCellFactory().call(datePicker);
        }
        if (cell == null) {
            cell = new DateCell();
        }

        return cell;
    }
    /**
     * The primary chronology for display. This may be overridden to
     * be different than the DatePicker chronology. For example
     * DatePickerHijrahContent uses ISO as primary and Hijrah as a
     * secondary chronology.
     */

    protected boolean isValidDate(Date date) {
        return true; //TODO ane validation
    }

    protected boolean isValidDate(Calendar date, int offset, int field) {
        if (date != null) {
            try {
                date.add(field, offset);
                return true;
            } catch (IllegalArgumentException  ex) {
            }
        }
        return false;
    }
}
