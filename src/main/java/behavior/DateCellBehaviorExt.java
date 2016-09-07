package behavior;

import com.sun.javafx.scene.traversal.Direction;
import control.DateCell;
import control.EventCell;
import javafx.scene.Node;
import skin.DatePickerContent;
import skin.DatePickerContentExt;
import skin.DatePickerEventsPane;

/**
 * Created by Afzalan on 9/6/2016.
 */
public class DateCellBehaviorExt extends DateCellBehavior {
    public final static int WEEKLY = 1;
    public static final int DAILY = 0;

    public DateCellBehaviorExt(DateCell dateCell) {
        super(dateCell);
    }
    @Override public void traverse(final Node node, final Direction dir) {
        if (node instanceof EventCell) {
            DatePickerContentExt dpc = (DatePickerContentExt) findDatePickerContent(node);
            if (dpc != null) {
                EventCell cell = (EventCell)node;
                switch (dir) {
                    case UP: dpc.goToEventCell(cell, -1, WEEKLY,  true); break;
                    case DOWN: dpc.goToEventCell(cell, +1, WEEKLY,  true); break;
                    case LEFT: dpc.goToEventCell(cell, -1, DAILY,  true); break;
                    case RIGHT: dpc.goToEventCell(cell, +1, DAILY,  true); break;
                }
            }
            return;
        }
        super.traverse(node, dir);
    }
    @Override
    protected DatePickerContent findDatePickerContent(Node node) {
        if(node instanceof EventCell) {
            Node parent = node;
            while ((parent = parent.getParent()) != null && !(parent instanceof DatePickerEventsPane));
            if(parent instanceof DatePickerEventsPane){
                DatePickerEventsPane datePickerEventsPane = (DatePickerEventsPane) parent;
                for (int i=0; i<datePickerEventsPane.getChildren().size(); i++) {
                    if (datePickerEventsPane.getChildren().get(i) instanceof DatePickerContent)
                        return (DatePickerContent)datePickerEventsPane.getChildren().get(i);
                }
            }
        }
        return super.findDatePickerContent(node);
    }
}
