package panes;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.ULocale;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.Main;

import java.util.Date;

/**
 * Created by Afzalan on 9/11/2016.
 */
public class LeftToDo extends BorderPane {
    final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    final static DateFormat faClock = DateFormat.getPatternInstance("HH:mm", faLocale);
    final static DateFormat faDate = DateFormat.getDateInstance(DateFormat.SHORT, faLocale);
    private  Thread ts;
    private  Label hLable = new Label();
    private  Label dLable = new Label();
    public LeftToDo() {
        super();
        setBorder(Main.regularBorder);
        hLable = new Label();
        Font font24 = new Font(24);
        hLable.setTextAlignment(TextAlignment.CENTER);
        hLable.setFont(font24);
        dLable = new Label();
        VBox vBoxTop = new VBox();
        vBoxTop.setAlignment(Pos.CENTER);
        ts = Main.getNewThread(new Runnable() {
            @Override
            public void run() {
                boolean intrupted = false;
                while(!intrupted) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            hLable.setText(faClock.format(new Date()));
                            dLable.setText(faDate.format(new Date()));
                        }
                    });
                    try {
                        Thread.sleep(1000*60);
                    } catch (InterruptedException e) {
                        intrupted = true;
                    }
                }
            }
        });

        vBoxTop.getChildren().addAll(hLable, dLable);
        setTop(vBoxTop);
        setPrefWidth(150.0);
        ts.start();
    }
}
