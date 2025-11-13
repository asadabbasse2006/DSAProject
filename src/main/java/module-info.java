module com.example.dsaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.dsaproject to javafx.fxml;
    exports com.example.dsaproject;
}