package org.romanchi;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jsoup.nodes.Document;
import org.romanchi.controllers.ControllerManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scene oddsPortalScene = ControllerManager.changeSceneTo("Oddsportal","oddsportal");
        primaryStage.setScene(oddsPortalScene);
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
/*
<dependency>
<groupId>com.jcabi</groupId>
<artifactId>jcabi-xml</artifactId>
<version>0.14</version>
</dependency>*/
