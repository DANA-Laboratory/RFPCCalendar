package skin;


import control.DatePicker;
import control.DatePickerExt;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;


import java.util.Date;

import static org.junit.Assert.*;

public class DatePickerContentExtTest extends ApplicationTest {

    private DatePickerContentExt datePickerContentExt;
    private StackPane root;
    @Override
    public void start(Stage stage) throws Exception {
        Button btn = new Button();
        root = new StackPane();
        DatePickerExt datePicker = new DatePickerExt();
        DatePickerSkinExt datePickerSkin = new DatePickerSkinExt(datePicker);
        datePicker.setSkin(datePickerSkin);
        root.getChildren().add(datePicker);
        Scene scene = new Scene(root, 300, 250);
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
        datePickerSkin.show();
        //datePickerContentExt = (DatePickerContentExt) datePickerSkin.getPopupContent();
    }
    @Test
    public void testisAlign() throws Exception {
        YearMonth yearMonth = new YearMonth(new Date());
        Date fri = new Date();
        fri.setTime(1472235021041L);
        Date tue = new Date();
        tue.setTime(1472144021041L);
        //Rectangle rect = datePickerContentExt.getRectDuration(fri, tue);
        //assertSame((new Rectangle()).getClass(), rect.getClass());
        //assertNull(datePickerContentExt.getRectDuration(yearMonth.plusMonths(1).atDay(1), new Date()));
        //assertNull(datePickerContentExt.getRectDuration(yearMonth.atDay(1), yearMonth.atDay(9)));
    }
}