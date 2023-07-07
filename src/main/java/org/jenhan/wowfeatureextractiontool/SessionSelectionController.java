package org.jenhan.wowfeatureextractiontool;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

    public void populateTable() {
        sessionInfoTable.setItems(MainControl.getSessionInfos());
    }
}
