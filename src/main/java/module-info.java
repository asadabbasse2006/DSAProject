module com.example.dsaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.dsaproject to javafx.graphics, javafx.fxml;
    opens com.example.dsaproject.Controllers to javafx.fxml;   // <-- Important
    exports com.example.dsaproject;
}
