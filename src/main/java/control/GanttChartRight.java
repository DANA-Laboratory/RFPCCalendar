package control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.SortedSet;

/**
 * Created by AliReza on 9/23/2016.
 */
public class GanttChartRight extends VBox {
    private final GanttChart ganttChart;
    private ArrayList<Label> rowCaptions = new ArrayList<Label>();
    public GanttChartRight(GanttChart ganttChart) {
        this.ganttChart = ganttChart;
        ganttChart.sortedRowNamesProperty.addListener(new ChangeListener<SortedSet<String>>() {
            @Override
            public void changed(ObservableValue<? extends SortedSet<String>> observable, SortedSet<String> oldValue, SortedSet<String> newValue) {
                if(newValue != null) {
                    show();
                }
            }
        });
        ganttChart.yUnitProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateSize();
                }
            }
        });
        ganttChart.yMarginProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue != null && ganttChart.sortedRowNamesProperty.get() != null) {
                    updateSize();
                }
            }
        });
        if (ganttChart.sortedRowNamesProperty.get() != null)
            show();
    }
    private void createRowCaptions(){
        rowCaptions.clear();
        ganttChart.sortedRowNamesProperty.get().forEach((e)->{
            Label label  = new Label(e);
            label.getStyleClass().add("row_caption");
            label.setAlignment(Pos.CENTER);
            label.setRotate(90);
            rowCaptions.add(label);
        });
    }
    private void updateSize() {
        final double yUnit = ganttChart.yUnitProperty.get() + ganttChart.yMarginProperty.get();
        rowCaptions.forEach((e)->{
            e.setPrefHeight(yUnit);
        });
    }
    public void show() {
        createRowCaptions();
        updateSize();
        getChildren().setAll(rowCaptions);
        if(ganttChart.getChildren().indexOf(GanttChartRight.this)<0)
            ganttChart.setLeft(GanttChartRight.this);
    }
}
