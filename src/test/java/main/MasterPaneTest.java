package main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static control.ServerIO.startComboIDServer;
import static org.junit.Assert.*;

/**
 * Created by Afzalan on 9/11/2016.
 */
public class MasterPaneTest extends ApplicationTest {
    private Application MasterPane = new MasterPane();
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