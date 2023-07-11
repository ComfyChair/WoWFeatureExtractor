/** Module information  **/
module org.jenhan.wowfeatureextractiontool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.logging;
    requires java.prefs;
    requires jakarta.activation;
    requires jakarta.xml.bind;

    opens org.jenhan.wowfeatureextractiontool to javafx.fxml, jakarta.xml.bind;
    exports org.jenhan.wowfeatureextractiontool;
    exports org.jenhan.wowfeatureextractiontool.Utilities;
    opens org.jenhan.wowfeatureextractiontool.Utilities to jakarta.xml.bind, javafx.fxml;
}