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

package control;

import java.util.Date;

import skin.DateCellSkin;
import javafx.scene.control.Cell;
import javafx.scene.control.Skin;

/**
 * DateCell is used by {@link DatePicker} to render the individual
 * grid cells in the calendar month. By providing a
 * {@link DatePicker#dayCellFactoryProperty() dayCellFactory}, an
 * application can provide an update method to change each cell's
 * properties such as text, background color, etc.
 *
 * @since JavaFX 8.0
 */
public class DateCell extends Cell<Date> {
    public DateCell() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /** {@inheritDoc} */
    @Override public void updateItem(Date item, boolean empty) {
        super.updateItem(item, empty);
    }

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new DateCellSkin(this);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    protected static final String DEFAULT_STYLE_CLASS = "date-cell";
}
