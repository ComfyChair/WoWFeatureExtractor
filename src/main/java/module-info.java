module org.jenhan.wowfeatureextractiontool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.logging;
    requires java.xml;
    requires java.prefs;
    requires org.glassfish.jaxb.core;
    requires org.glassfish.jaxb.runtime;
    requires java.xml.bind;

    opens org.jenhan.wowfeatureextractiontool to javafx.fxml;
    exports org.jenhan.wowfeatureextractiontool;
    exports org.jenhan.wowfeatureextractiontool.Utilities;
    opens org.jenhan.wowfeatureextractiontool.Utilities to javafx.fxml;
}