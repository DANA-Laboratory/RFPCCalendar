package control;

import Net.Query;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import main.Main;
import main.MainController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AliReza on 9/7/2016.
 */
public class ServerIO {
    static SocketIOServer server;
    static final int PORT = 9291;
    static SocketIOClient socketIOClient = null;
    private static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
    private static int indexOf(JSONArray arr, String str){
        for (int i=0; i<arr.length(); i++)
            try {
                if(arr.getString(i).compareTo(str)==0)
                    return i;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return -1;
    }
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

        System.out.println("server start...");
        server.start();
    }
    public static Thread startComboIDServer() {
        JSONArray itemList = new JSONArray();
        SecureRandom random = new SecureRandom();
        int i = 0;
        while(i++ < 30000)
            itemList.put(new BigInteger(20, random).toString(16));
        Thread ts = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server();
                } catch (InterruptedException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                server.addEventListener("runQuery", String.class, new DataListener<String>() {
                    @Override
                    public void onData(SocketIOClient client, String data, AckRequest ackSender) {
                        //System.out.println("requestItemType for item = " + data);
                        JSONObject jData = null;
                        String parameter;
                        String query;
                        try {
                            jData = new JSONObject(data);
                            parameter = jData.getString("parameter");
                            query = jData.getString("query");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        if(query.equals(Query.FindTypes.toString())) {
                            String nav[] = MainController.getNavigation();
                            JSONArray types = new JSONArray();
                            types.put(getRandom(nav));
                            String str;
                            while (indexOf(types, str = getRandom(nav)) < 0)
                                types.put(str);
                            client.sendEvent("types", types.toString());
                        } else
                            if (query.equals(Query.FindAll.toString())) {
                                ArrayList<String> selectedByFuzzy = new ArrayList<String>();
                                int i = 0;
                                int d=0;
                                while(i<itemList.length() && selectedByFuzzy.size() < 50) {
                                    try {
                                        if (itemList.getString(i).contains(parameter))
                                            selectedByFuzzy.add(itemList.getString(i++));
                                        else
                                            i++;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                client.sendEvent("items", selectedByFuzzy.toString());
                            }
                    }
                });
            }
        })
        {
            @Override
            public void interrupt() {
                System.out.println("server stop...");
                ServerIO.server.stop();
            }
        };
        ts.start();
        return ts;
    }
}
