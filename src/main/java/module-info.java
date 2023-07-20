/** Module information  **/
module wowfeatureextractor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.prefs;
    requires jakarta.activation;
    requires jakarta.xml.bind;

    opens org.jenhan.wowfeatureextractor to javafx.fxml,jakarta.xml.bind;
    exports org.jenhan.wowfeatureextractor;
    exports org.jenhan.wowfeatureextractor.Util;
    opens org.jenhan.wowfeatureextractor.Util to jakarta.xml.bind, javafx.fxml;
}