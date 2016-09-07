/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.javafx.scene.control.skin.CellSkinBase;
import control.DateCell;
import javafx.scene.text.Text;

import behavior.DateCellBehaviorExt;

public class DateCellSkin extends CellSkinBase<DateCell, DateCellBehaviorExt> {
    static final double DEFAULT_CELL_SIZE = 24.0;
    public DateCellSkin(DateCell control) {
        super(control, new DateCellBehaviorExt(control));

        control.setMaxWidth(Double.MAX_VALUE); // make the cell grow to fill a GridPane's cell
    }

    @Override protected void updateChildren() {
        super.updateChildren();

        Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
        if (secondaryText != null) {
            // LabeledSkinBase rebuilds the children list each time, so it's
            // safe to add more here.
            secondaryText.setManaged(false);
            getChildren().add(secondaryText);
        }
    }

    @Override protected void layoutChildren(final double x, final double y,
                                            final double w, final double h) {
        super.layoutChildren(x,y,w,h);

        Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
        if (secondaryText != null) {
            // Place the secondary Text node at BOTTOM_RIGHT.
            double textX = x + w - rightLabelPadding()  - secondaryText.getLayoutBounds().getWidth();
            double textY = y + h - bottomLabelPadding() - secondaryText.getLayoutBounds().getHeight();
            secondaryText.relocate(snapPosition(textX), snapPosition(textY));
        }
    }

    private double cellSize() {
        double cellSize = getCellSize();
        Text secondaryText = (Text)getSkinnable().getProperties().get("DateCell.secondaryText");
        if (secondaryText != null && cellSize == DEFAULT_CELL_SIZE) {
            // Workaround for RT-31643. The cellSize property was not yet set from CSS.
            cellSize = 36;
        }
        return cellSize;
    }

    @Override protected double computePrefWidth(double height,
                                                double topInset, double rightInset,
                                                double bottomInset, double leftInset) {
        double pref = super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        return snapSize(Math.max(pref, cellSize()));
    }

    @Override protected double computePrefHeight(double width,
                                                 double topInset, double rightInset,
                                                 double bottomInset, double leftInset) {
        double pref = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        return snapSize(Math.max(pref, cellSize()));
    }
}
