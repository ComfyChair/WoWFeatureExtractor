/** Module information  **/
module org.jenhan.wowfeatureextractiontool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.logging;
    requires java.prefs;
    requires jakarta.activation;
    requires jakarta.xml.bind;

    opens org.jenhan.wowfeatureextractor to javafx.fxml, javafx.graphics, jakarta.xml.bind;
    exports org.jenhan.wowfeatureextractor;
    exports org.jenhan.wowfeatureextractor.Util;
    opens org.jenhan.wowfeatureextractor.Util to jakarta.xml.bind, javafx.fxml;
}