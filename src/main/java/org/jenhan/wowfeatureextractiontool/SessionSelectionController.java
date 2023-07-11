package org.jenhan.wowfeatureextractiontool;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.jenhan.wowfeatureextractiontool.Utilities.DateFormatted;

import java.util.List;

/** GUI controller for session selection dialog **/
public class SessionSelectionController {
    // session selection
    @FXML
    TableView<Session> sessionInfoTable;
    @FXML
    TableColumn<Session, DateFormatted> date;
    @FXML
    TableColumn<Session, DateFormatted> time;
    @FXML
    TableColumn<Session, String> charName;
    @FXML
    TableColumn<Session, String> serverName;
    @FXML
    ObservableList<Session> sessionList;

    @FXML
    void populateTable() {
        sessionList = MainControl.getSessionInfos();
        sessionInfoTable.setItems(sessionList);
        sessionInfoTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    List<Session> getSelected() {
        return sessionInfoTable.getSelectionModel().getSelectedItems();
    }
}
