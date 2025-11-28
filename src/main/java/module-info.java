module com.example.dsaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;

    opens com.example.dsaproject to javafx.fxml;
    opens com.example.dsaproject.Controllers to javafx.fxml;
    opens com.example.dsaproject.Dialog to javafx.fxml;
    opens com.example.dsaproject.Model to javafx.fxml;
    opens com.example.dsaproject.Util to javafx.fxml;
    opens com.example.dsaproject.factory to javafx.fxml;

    exports com.example.dsaproject;
    exports com.example.dsaproject.Controllers;
    exports com.example.dsaproject.Model;
}
