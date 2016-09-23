package control;

import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import skin.YearMonth;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by AliReza on 9/15/2016.
 */


public class GanttChartPlot extends Pane {
    //Properties
    private final GanttChart ganttChart;
    private boolean[] hiddenDays = {false, false, false, false, false, true, true};
    private ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    private ArrayList<Label> labels = new ArrayList<Label>();
    private ArrayList<Line> lines = new ArrayList<Line>();
    static final TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    GanttChartPlot(GanttChart _ganttChart) {
        super();
        ganttChart = _ganttChart;
        getStylesheets().add(getClass().getResource("../GanttChart.css").toExternalForm());
        ganttChart.yUnitProperty = new SimpleDoubleProperty(USE_COMPUTED_SIZE){
            @Override public double get(){
                if(super.get()==USE_COMPUTED_SIZE)
                    return computeMinYUnit();
                else
                    return super.get();
            }
        };
        ganttChart.yUnitProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                computedMinYUnit = -1;
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        ganttChart.yMarginProperty = new SimpleDoubleProperty(5);
        ganttChart.yMarginProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateLayout();
                    updateLines();
                }
            }
        });
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                computedMinYUnit = -1;
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateSize();
                    updateLayout();
                    updateLines();
                }
            }
        });
        ganttChart.sortedRowNamesProperty.addListener(new ChangeListener<SortedSet<String>>() {
            @Override
            public void changed(ObservableValue<? extends SortedSet<String>> observable, SortedSet<String> oldValue, SortedSet<String> newValue) {
                computedMinYUnit = -1;
                if(newValue != null) {
                    show();
                }
            }
        });
        if (ganttChart.sortedRowNamesProperty.get() != null)
            show();
    }
    @Override protected double computePrefHeight(double width) {
        int rowCount = ganttChart.sortedRowNamesProperty.get().size();
        double value = computeMinYUnit() * rowCount + (rowCount-1)* ganttChart.yMarginProperty.get();
        return value;
    }
    public void hideDay(int day){
        if(!hiddenDays[day]) {
            hiddenDays[day] = true;
            //TODO
        }
    };
    public void unHideDay(int day){
        if(hiddenDays[day]) {
            hiddenDays[day] = false;
            //TODO
        }
    }
    private void show() {
        createListLines();
        createLabels();
        updateSize();
        updateLayout();
        updateLines();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getChildren().clear();
                getChildren().addAll(labels);
                getChildren().addAll(lines);
                if(ganttChart.getChildren().indexOf(GanttChartPlot.this)<0)
                    ganttChart.setCenter(GanttChartPlot.this);
            }
        });
    }
    private void updateLayout() {
        double xUnit = getWidth()/ganttChart.getColumnsCount();
        double yUnit = ganttChart.yUnitProperty.get();
        for (int i=0; i < labels.size(); i++) {
            //TODO
            CalendarEvent jData = ganttChart.calendarEventsProperty.get().get(i);
            (labels.get(i)).setLayoutY(yUnit * (rects.get(i).getLayoutY() + ganttChart.indexOf(jData.value)) + ganttChart.indexOf(jData.value) * ganttChart.yMarginProperty.get());
            (labels.get(i)).setLayoutX(rects.get(i).getLayoutX() * xUnit);
        }
    }
    double computedMinYUnit = -1;//invalid
    public double computeMinYUnit() {
        if(computedMinYUnit == -1) {
            Group root = new Group();
            Scene scene = new Scene(root);
            root.getStylesheets().add(getClass().getResource("../GanttChart.css").toExternalForm());
            Label label = new Label();
            computedMinYUnit = 0.0;
            for (int i = 0; i < labels.size(); i++) {
                label.setFont(labels.get(i).getFont());
                label.setBorder(labels.get(i).getBorder());
                label.setText(labels.get(i).getText());
                root.getChildren().setAll(label);
                root.applyCss();
                root.layout();
                double newValue = label.getHeight() / rects.get(i).getHeight();
                if (computedMinYUnit < newValue)
                    computedMinYUnit = newValue;
            }
        }
        return computedMinYUnit;
    }
    private void updateSize() {
        double xUnit = getWidth()/ganttChart.getColumnsCount();
        double yUnit = ganttChart.yUnitProperty.get();
        for (int i = 0; i < labels.size(); i++) {
            (labels.get(i)).setPrefWidth(rects.get(i).getWidth() * xUnit);
            double prefHeight = rects.get(i).getHeight() * yUnit;
            (labels.get(i)).setPrefHeight(prefHeight);
        }
    }
    private void updateLines() {
        double yUnit = ganttChart.yUnitProperty.get();
        for (int i=0; i < lines.size(); i++) {
            lines.get(i).setStartX(0.0);
            lines.get(i).setEndX(getWidth());
            lines.get(i).setStartY((i+1) * (yUnit + ganttChart.yMarginProperty.get()) -  ganttChart.yMarginProperty.get() / 2.0);
            lines.get(i).setEndY(lines.get(i).getStartY());
        };
    }
    private void createListLines() {
        lines.clear();
        for (int i = 0; i< ganttChart.sortedRowNamesProperty.get().size() - 1; i++) {
            Line line = new Line();
            line.getStyleClass().add("h_line");
            lines.add(line);
        };
    }
    private void createLabels() {
        rects.clear();
        labels.clear();
        Date d1 = new Date();
        Date d2 = new Date();
        for (int i = 0; i < ganttChart.calendarEventsProperty.get().size(); i++) {
            CalendarEvent jEvent =  ganttChart.calendarEventsProperty.get().get(i);
            d1.setTime(jEvent.from);
            d2.setTime(jEvent.to);
            rects.add(createRect(d1, d2));
            Label label = new Label(ganttChart.calendarEventsProperty.get().get(i).caption);
            label.getStyleClass().add("events");
            labels.add(label);
        }
    }
    private Rectangle createRect(Date from, Date to){
        Rectangle rect = new Rectangle();
        double minToStart = YearMonth.getDateDiff(ganttChart.fromDate, from, TimeUnit.MINUTES);
        double dayToStart = (int)minToStart/60/24;
        minToStart -= dayToStart*60*24;
        double minLength = YearMonth.getDateDiff(from, to, TimeUnit.MINUTES);
        double dayLength = (int)minLength/60/24;
        minLength -= dayLength*60*24;
        if(minLength>0)
            dayLength++;
        rect.setHeight(minLength/ganttChart.getMinutesOfDay());
        rect.setWidth(dayLength);
        rect.setLayoutX(dayToStart);
        rect.setLayoutY(minToStart/ganttChart.getMinutesOfDay());
        return  rect;
    }
}
