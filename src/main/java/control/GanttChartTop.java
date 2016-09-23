package control;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.SortedSet;

/**
 * Created by Afzalan on 9/19/2016.
 */
public class GanttChartTop extends HBox {
    private final String fromDateString;
    private final String toDateString;
    private final String fromHmString;
    private final String toHmString;
    private final ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    private final DateFormat faFormatterFull = DateFormat.getDateInstance(DateFormat.FULL, faLocale);
    private final DateFormat faFormatterDay = DateFormat.getPatternInstance(DateFormat.WEEKDAY, faLocale);
    private final DateFormat faFormatterHm = DateFormat.getPatternInstance("HH:mm", faLocale);

    final GanttChart ganttChart;
    GanttChartTop(GanttChart ganttChart){
        super();
        getStylesheets().add(getClass().getResource("../GanttChart.css").toExternalForm());
        this.ganttChart = ganttChart;
        toDateString = faFormatterFull.format(ganttChart.toDate);
        toHmString = faFormatterHm.format(ganttChart.toDate);
        fromDateString = faFormatterFull.format(ganttChart.fromDate);
        fromHmString = faFormatterHm.format(ganttChart.fromDate);
        ganttChart.sortedRowNamesProperty.addListener(new ChangeListener<SortedSet<String>>() {
            @Override
            public void changed(ObservableValue<? extends SortedSet<String>> observable, SortedSet<String> oldValue, SortedSet<String> newValue) {
                if(newValue != null) {
                    show();
                }
            }
        });
        ((Pane)ganttChart.getLeft()).widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateSize();
            }
        });
        ((Pane)ganttChart.getCenter()).widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateSize();
            }
        });
        if (ganttChart.sortedRowNamesProperty.get() != null)
            show();
    }
    private ArrayList<Label> columnCaptions = new ArrayList<Label>();
    private void show() {
        createColumnCaptions();
        updateSize();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getChildren().setAll(columnCaptions);
                if(ganttChart.getChildren().indexOf(GanttChartTop.this)<0)
                    ganttChart.setTop(GanttChartTop.this);
            }
        });
    }
    private void createColumnCaptions() {
        columnCaptions.clear();
        int index = 0;
        Calendar cal = Calendar.getInstance(faLocale);
        cal.setTime(ganttChart.fromDate);
        columnCaptions.add(new Label());
        while(cal.getTime().before(ganttChart.toDate)) {
            Label label = new Label(faFormatterDay.format(cal.getTime()));
            label.setAlignment(Pos.CENTER);
            label.getStyleClass().add("column_caption");
            columnCaptions.add(label);
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
    }
    private void updateSize() {
        double a = ganttChart.getWidth();
        double b = ((Pane)ganttChart.getCenter()).getWidth();
        double c = ((Pane)ganttChart.getLeft()).getWidth();
        double xUnit = ((Pane)ganttChart.getCenter()).getWidth()/ganttChart.getColumnsCount();
        columnCaptions.get(0).setPrefWidth(((Pane)ganttChart.getLeft()).getWidth());
        for (int i = 1; i < columnCaptions.size(); i++)
            (columnCaptions.get(i)).setPrefWidth(xUnit);
    }
}
