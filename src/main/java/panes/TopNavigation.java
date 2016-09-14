package panes;


import Net.Query;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import main.Main;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by AliReza on 9/11/2016.
 */
public class TopNavigation extends HBox {
    private static class location{
        Query query;
        Pane view;
    }
    private static location home = new location();
    private static location currentLocation;
    private static ArrayDeque<location> history;
    private Hashtable<String, Parent> views;
    public TopNavigation(String[] fileName, String[] keys) throws IOException {
        super();
        views = new Hashtable<String, Parent>();
        for (int i=0; i < fileName.length; i++) {
            views.put(keys[i], FXMLLoader.load(getClass().getResource("../view/" + fileName[i] + ".fxml")));
        }
        views.put("Home", FXMLLoader.load(getClass().getResource("../home.fxml")));
        btnNext.getStyleClass().add("icon");
        btnNext.setText('\uf061' + "");
        btnPrevious.getStyleClass().add("icon");
        btnPrevious.setText('\uf060' + "");
        btnHome.getStyleClass().add("icon");
        btnHome.setText('\uf015' + "");
        btnHome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getHome();
            }
        });
        btnRefresh.getStyleClass().add("icon");
        btnRefresh.setText('\uf021' + "");
        labelCurrentLocation.getStyleClass().add("head");
        labelCurrentLocation.setPrefWidth(600.0);
        labelCurrentLocation.setBorder(Main.regularBorder);
        setSpacing(2.0);
        getChildren().addAll(btnHome, labelCurrentLocation, btnRefresh, btnNext, btnPrevious);
    }
    public int getSize(){
        return  views.size();
    }
    public Parent getView(String key){
        return views.get(key);
    }
    public String[] getKeys(){
        int i = 0;
        String[] keys = new String[views.size()];
        for (Enumeration<String> e = views.keys(); e.hasMoreElements();)
            keys[i++]=e.nextElement();
        return keys;
    }
    public Parent getHome() {
        return getView("Home");
    }
    private static Label labelCurrentLocation = new Label("خانه");
    private static Button btnNext = new Button();
    private static Button btnPrevious = new Button();
    private static Button btnHome = new Button();
    private static Button btnRefresh = new Button();
}
