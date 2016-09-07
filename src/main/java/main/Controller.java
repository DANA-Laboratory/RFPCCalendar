package main;

import control.DatePickerExt;

public class Controller {
    public DatePickerExt dp;
    public void initialize() {
        dp.getStylesheets().add(getClass().getResource("../RFPCCalendar.css").toExternalForm());
    }
}
