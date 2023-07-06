package org.jenhan.wowfeatureextractiontool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class SessionSelectionController implements Initializable {
    // session selection
    @FXML
    TableView<SessionInfo> sessionInfoTable;
    @FXML
    TableColumn<SessionInfo, Integer> sessionID;
    @FXML
    TableColumn<SessionInfo, SessionInfo.DateTimeFormatted> dateTime;
    @FXML
    TableColumn<SessionInfo, String> charName;
    @FXML
    TableColumn<SessionInfo, String> serverName;
    @FXML
    Button sessionSelectOK;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing session selection controller");
        //populate table cells
        System.out.println("Populating properties table");
        sessionID.setCellValueFactory(new PropertyValueFactory<SessionInfo, Integer>("sessionID"));
        dateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        charName.setCellValueFactory(new PropertyValueFactory<>("charName"));
        serverName.setCellValueFactory(new PropertyValueFactory<>("serverName"));
    }

    public void populateTable() {
        sessionInfoTable.setItems(MainControl.getSessionInfos());
    }
}
