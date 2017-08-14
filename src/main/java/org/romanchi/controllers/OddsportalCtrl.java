package org.romanchi.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.romanchi.DAO.MatchDAO;
import org.romanchi.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ResourceBundle;

public class OddsportalCtrl extends Ctrl implements Initializable {
    @FXML
    WebView browser;
    @FXML
    Button startBtn;
    @FXML
    Label statusLabel;
    @FXML
    ListView menu;
    @FXML
    public Label location;
    @FXML
    public TextField locationInputField;
    private boolean hasNextPage = true;
    private boolean loaded = false;
    public void initialize(final URL loc, ResourceBundle resources) {
        browser.getEngine().load("http://www.oddsportal.com/");
        browser.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        location.setText(browser.getEngine().getLocation());
                        if (newState == Worker.State.SUCCEEDED) {
                            if (browser.getEngine().getLocation().equals("http://www.oddsportal.com/")) {
                                Document page = browser.getEngine().getDocument();
                                Element userHeader = page.getElementById("user-header");
                                if(userHeader.getTextContent().contains("romanchi")){
                                    System.out.println("Hello, romanchi");
                                    statusLabel.setText("Hello, romanchi");
                                }else{
                                    System.out.println("Please login to parse all data");
                                    statusLabel.setText("Please login to parse all data");
                                }
                            }else{
                                try{
                                    Document doc = browser.getEngine().getDocument();
                                    Element tableContainer = (Element) doc.getElementsByTagName("tbody").item(0);
                                    System.out.println(tableContainer);
                                    NodeList rows = tableContainer.getElementsByTagName("tr");
                                    for (int i = 0; i < rows.getLength(); i++) {
                                        Element row = (org.w3c.dom.Element) rows.item(i);
                                        if(row.getAttribute("class").contains("deactivate")){
                                            int winner = -1;
                                            NodeList columns = row.getElementsByTagName("td");
                                            Element timeNode = (Element) columns.item(0);
                                            String time = timeNode.getTextContent();
                                            Element matchNameNode = (Element) columns.item(1);
                                            String matchName = matchNameNode.getTextContent();
                                            Element matchLinkNode = (Element) matchNameNode.getElementsByTagName("a").item(0);
                                            String matchLink = matchLinkNode.getTextContent();
                                            Element scoreNode = (Element) columns.item(2);
                                            String score = scoreNode.getTextContent();
                                            Element firstNode = (Element) columns.item(3);
                                            String first = firstNode.getTextContent();
                                            Element drawNode = (Element) columns.item(4) ;
                                            String draw = drawNode.getTextContent();
                                            Element secondNode = (Element) columns.item(5);
                                            String second = secondNode.getTextContent();
                                            Element amountOfAvailableBookmakersOddsNode = (Element) columns.item(6);
                                            String amountOfAvailableBookmakersOdds = amountOfAvailableBookmakersOddsNode.getTextContent();
                                            /*Checking winner*/
                                            if(firstNode.getAttribute("class").contains("result-ok")){
                                                winner = 1;
                                            }else if(drawNode.getAttribute("class").contains("result-ok")){
                                                winner = 0;
                                            }else if(secondNode.getAttribute("class").contains("result-ok")){
                                                winner = 2;
                                            }
                                            /*MatchDAO match = new MatchDAO(winner, time, matchName, matchLink, score, first, draw, second, amountOfAvailableBookmakersOdds, li);*/
                                            /*System.out.println(match);*/
                                        }
                                    }
                                }catch (NullPointerException ex){
                                    hasNextPage = false;
                                }
                            }
                        }
                    }
                });
    }

    public void startParsing(ActionEvent actionEvent) {
        for(int page=1; hasNextPage; page++){
            browser.getEngine().load(locationInputField.getText() + "#/page/" + page +"/");
        }
    }
}
