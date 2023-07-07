package org.jenhan.wowfeatureextractiontool;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class SessionSelectionController {
    // session selection
    @FXML
    TableView<SessionInfo> sessionInfoTable;
    @FXML
    TableColumn<SessionInfo, SessionInfo.DateFormatted> date;
    @FXML
    TableColumn<SessionInfo, SessionInfo.DateFormatted> time;
    @FXML
    TableColumn<SessionInfo, String> charName;
    @FXML
    TableColumn<SessionInfo, String> serverName;
    @FXML
    ObservableList<SessionInfo> sessionList;

    @FXML
    void populateTable() {
        sessionList = MainControl.getSessionInfos();
        sessionInfoTable.setItems(sessionList);
    }

    List<SessionInfo> getSelected(){
        return sessionInfoTable.getSelectionModel().getSelectedItems();
    }
}
