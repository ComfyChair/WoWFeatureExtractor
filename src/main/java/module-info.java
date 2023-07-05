module org.jenhan.wowfeatureextractiontool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.xml;

    opens org.jenhan.wowfeatureextractiontool to javafx.fxml;
    exports org.jenhan.wowfeatureextractiontool;
}