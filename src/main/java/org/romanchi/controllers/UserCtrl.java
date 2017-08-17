package org.romanchi.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.romanchi.DAO.BookmakerDAO;
import org.romanchi.DAO.Database;
import org.romanchi.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Роман on 02.08.2017.
 */
public class UserCtrl extends Ctrl implements Initializable {
    @FXML
    public Button goBtn;
    @FXML
    public TextField bankSizeInput;
    @FXML
    public TextField betSizeInput;
    @FXML
    public ComboBox bookmakerComboBox;
    @FXML
    public Label resultLabel;
    @FXML
    public LineChart<Integer, Double> bankChart;

    public void go(ActionEvent actionEvent) {
        try{
            Double betSize = Double.parseDouble(betSizeInput.getText());
            String bookmaker = (String) bookmakerComboBox.getSelectionModel().getSelectedItem();
            System.out.println(bookmaker + " " + betSize);
            User user = new User(bookmaker,Double.parseDouble(bankSizeInput.getText()), betSize);
            user.start();
        }catch (NumberFormatException ex){

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<BookmakerDAO> bookmakerDAOS = FXCollections.observableList(Database.getAllBookmakers());
        bookmakerDAOS.forEach(item -> bookmakerComboBox.getItems().add(item.getBookmakerName()));
    }
}
