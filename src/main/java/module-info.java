module com.example.dsaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jdk.compiler;
    requires java.desktop;

    opens com.example.dsaproject to javafx.fxml, javafx.graphics;
    opens com.example.dsaproject.Controllers to javafx.fxml, javafx.graphics;

    exports com.example.dsaproject;
    exports com.example.dsaproject.Controllers;  // optional but recommended
}
