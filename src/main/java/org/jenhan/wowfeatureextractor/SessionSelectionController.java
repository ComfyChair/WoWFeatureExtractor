package org.jenhan.wowfeatureextractor;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.jenhan.wowfeatureextractor.Util.DateFormatted;

import java.util.List;

/**
 * GUI controlller for session selection dialog
 **/
public class SessionSelectionController {
    /** table view for gui representation of session information **/
    @FXML
    TableView<Session> sessionInfoTable;
    /** formatted recording date **/
    @FXML
    TableColumn<Session, DateFormatted> date;
    /** formatted recording time **/
    @FXML
    TableColumn<Session, DateFormatted> time;
    /** character that was played during the session **/
    @FXML
    TableColumn<Session, String> charName;
    /** server the session was recorded on **/
    @FXML
    TableColumn<Session, String> serverName;
    /** list of sessions **/
    @FXML
    ObservableList<Session> sessionList;

    /** populates the session selection table **/
    @FXML
    void populateTable() {
        sessionList = MainControl.sessionList();
        sessionInfoTable.setItems(sessionList);
        sessionInfoTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /** @return list of selected sessions **/
    List<Session> getSelected() {
        return sessionInfoTable.getSelectionModel().getSelectedItems();
    }
}
