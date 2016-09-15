package pane;


import Net.Query;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import main.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by AliReza on 9/11/2016.
 */
public class TopNavigation extends HBox {
    public SimpleIntegerProperty currentLocation = new SimpleIntegerProperty();
    private class location{
        Query query;
        Parent view;
        public location(Query _query, Parent _parent) {
            query = _query;
            view = _parent;
        }
    }
    private Label labelCurrentLocation = new Label("خانه");
    private Button btnNext = new Button();
    private Button btnPrevious = new Button();
    private Button btnHome = new Button();
    private Button btnRefresh = new Button();
    private ArrayList<location> history;
    private Hashtable<String, Parent> defaultViews;
    public TopNavigation(String[] fileName, String[] keys) throws IOException {
        super();
        history = new ArrayList<location>();
        defaultViews = new Hashtable<String, Parent>();
        defaultViews.put("Home", FXMLLoader.load(getClass().getResource("../home.fxml")));
        for (int i=0; i < fileName.length; i++) {
            defaultViews.put(keys[i], FXMLLoader.load(getClass().getResource("../view/" + fileName[i] + ".fxml")));
        }
        history.add(new location(new Query(Query.FindAll, null), getHome()));
        btnNext.getStyleClass().add("icon");
        btnNext.setText('\uF060' + "");
        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentLocation.get() < history.size()-1)
                    currentLocation.set(currentLocation.get()+1);
            }
        });
        btnPrevious.getStyleClass().add("icon");
        btnPrevious.setText('\uF061' + "");
        btnPrevious.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentLocation.get()>0)
                    currentLocation.set(currentLocation.get()-1);
            }
        });
        btnHome.getStyleClass().add("icon");
        btnHome.setText('\uf015' + "");
        btnHome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentLocation.set(0);
            }
        });
        currentLocation.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue() == 0)
                    btnPrevious.setDisable(true);
                else
                    btnPrevious.setDisable(false);
                if(newValue.intValue() >= history.size()-1)
                    btnNext.setDisable(true);
                else
                    btnNext.setDisable(false);
            }
        });
        btnNext.setDisable(true);
        btnPrevious.setDisable(true);
        btnRefresh.getStyleClass().add("icon");
        btnRefresh.setText('\uf021' + "");
        labelCurrentLocation.getStyleClass().add("head");
        labelCurrentLocation.setPrefWidth(600.0);
        labelCurrentLocation.setBorder(Main.regularBorder);
        setSpacing(2.0);
        getChildren().addAll(btnHome, labelCurrentLocation, btnRefresh, btnPrevious, btnNext);
    }
    public int getSize(){
        return  defaultViews.size();
    }
    public void setNewLocation(Query query, String key) {
        history.add(new location(query, defaultViews.get(key)));
        currentLocation.set(history.size() - 1);
    }
    public String[] getKeys(){
        int i = 0;
        String[] keys = new String[defaultViews.size()];
        for (Enumeration<String> e = defaultViews.keys(); e.hasMoreElements();)
            keys[i++]=e.nextElement();
        return keys;
    }
    public Parent getHome() {
        return defaultViews.get("Home");
    }
    public Parent getCurrentView() {
        return history.get(currentLocation.getValue()).view;
    }
}
