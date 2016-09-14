package control;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.ULocale;
import control.ComboIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.MasterPane;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Afzalan on 9/11/2016.
 */
public class Clock extends BorderPane {
    final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    final static DateFormat faClock = DateFormat.getPatternInstance("HH:mm", faLocale);
    final static DateFormat faDate = DateFormat.getDateInstance(DateFormat.SHORT, faLocale);
    private  Thread ts;
    private  Label hLable = new Label();
    private  Label dLable = new Label();
    public Clock() {
        super();
        setBorder(MasterPane.regularBorder);
        hLable = new Label();
        Font font24 = new Font(24);
        hLable.setTextAlignment(TextAlignment.CENTER);
        hLable.setFont(font24);
        dLable = new Label();
        VBox vBoxTop = new VBox();
        vBoxTop.setAlignment(Pos.CENTER);
        ts = new Thread(new Runnable() {
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
    public void interrupt() {
        ts.interrupt();
    }
}
