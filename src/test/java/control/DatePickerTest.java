package control;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import skin.DatePickerSkinExt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Afzalan on 8/27/2016.
 */
public class DatePickerTest extends GuiTest {
    private Thread ts = null;
    private DatePicker dp;
    private Parent root;
    private DatePickerSkinExt datePickerSkinExt;
    static SocketIOServer server;
    static final int PORT = 9291;
    static SocketIOClient socketIOClient = null;
    static String eventData = "[{"
            + "caption : 'جاوا مقدماتی'"
            + ",location : 'بندر امام'"
            + ",trainer : 'محمود اکبری'"
            + ",from : '950531'"
            + ",to : '950605'"
            + "} , {caption : 'اکسس پیشرفته'"
            + ",location : 'بندر امام'"
            + ",trainer : 'محمود اکبری'"
            + ",from : '950615'"
            + ",to : '950616'"
            + "}]";
    static String eventData2 = "[{"
            + "caption : 'محاسبات با C++'"
            + ",location : 'بندر امام'"
            + ",trainer : 'محمود اکبری'"
            + ",from : '950528'"
            + ",to : '950529'"
            + "} , {caption : 'مبانی HSE'"
            + ",trainer : 'محمود اکبری'"
            + ",location : 'بندر امام'"
            + ",from : '950708'"
            + ",to : '950709'"
            + "}]";
    public static void server() throws InterruptedException, UnsupportedEncodingException {
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
        config.setPort(PORT);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient _socketIOClient) {
                if (socketIOClient == null) {
                    socketIOClient = _socketIOClient;
                    System.out.println("client connected " + _socketIOClient);
                } else {
                    System.out.println("why client connected? " + _socketIOClient);
                    //TODO
                }
            }
        });
        /*
        server.addEventListener("newEvent", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws JSONException {
                JSONObject jdata = null;
                jdata = new JSONObject(data);
                System.out.println("new event received " + jdata.get("caption"));
            }
        });
        server.addEventListener("message", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                System.out.println("message received " + data);
            }
        });
        */
        server.addEventListener("requestEvents", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws JSONException {
                System.out.println("new request for events from " + client);
                client.sendEvent("calendarEvent", eventData);
            }
        });
        System.out.println("server start...");
        server.start();
    }
    @Before
    public void startServerThread(){
        ts = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }){
            @Override
            public void interrupt() {
                System.out.println("server stop...");
                server.stop();
            }
        };
        ts.start();
    }
    @After
    public void stopServerThread() {
        ts.interrupt();
    }
    @Override
    protected Parent getRootNode() {
        root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../datepicker.fxml"));
            return root;
        } catch (IOException ex) {
            // TODO ...
        }
        return root;
    }
    @Test
    public void getEventFromServerAndShow() throws Exception {
        clickOn(root);
        TimeUnit.SECONDS.sleep(2);
        type(KeyCode.F4);
        type(KeyCode.PAGE_UP);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.F10);
        server.getBroadcastOperations().sendEvent("calendarEvent", eventData2);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.F10);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.LEFT);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.RIGHT);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.UP);
        TimeUnit.SECONDS.sleep(1);
        type(KeyCode.DOWN);
        TimeUnit.SECONDS.sleep(10);
    }
}