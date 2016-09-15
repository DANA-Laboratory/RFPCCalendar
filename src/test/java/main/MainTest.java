package main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static control.ServerIO.startComboIDServer;

/**
 * Created by Afzalan on 9/11/2016.
 */
public class MainTest extends ApplicationTest {
    private Application MasterPane = new Main();
    @Test
    public void testCreateAncorPane() throws Exception {
        Thread.sleep(20000);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startComboIDServer();
        MasterPane.start(stage);
    }
}