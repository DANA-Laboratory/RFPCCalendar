package control;

import Net.Query;
import javafx.scene.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.util.concurrent.TimeUnit;


public class ComboIOTest extends GuiTest {
    private Thread ts = null;
    private ComboIO root = null;
    @Override
    protected Parent getRootNode() {
        root = new ComboIO(Query.FindAll);
        return root;
    }
    @Before
    public void startServerThread(){
        ts = ServerIO.startComboIDServer();
    }
    @After
    public void stopServerThread() {
        Net.Socket.get().close();
        ts.interrupt();
    }
    @Test
    public void testDataReceived() throws Exception {
        TimeUnit.SECONDS.sleep(150);
    }
}