package com.example.dsaproject.Controllers;

// Packages import
import com.example.dsaproject.Model.Admin;
import com.example.dsaproject.Model.Driver;
import com.example.dsaproject.Model.User;
import com.example.dsaproject.AlertMessage;
import com.example.dsaproject.Service.databaseManager;

//JavaFX libraries
import javafx.fxml.FXML;
import javafx.scene.control.*;

//SQL Libraries
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField hiddenPasswordField;
    @FXML private TextField showPasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Button loginButton;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private final AlertMessage alertMessage = new AlertMessage();

    @FXML public void initialize() {
        // Initialize combo box values
        roleComboBox.getItems().addAll("Admin", "User", "Driver");
        roleComboBox.setValue("User");
    }

    @FXML
    public void loginAccount() {
        String role = roleComboBox.getValue();
        String username = usernameField.getText();
        String password = hiddenPasswordField.isVisible()
                ? hiddenPasswordField.getText()
                : showPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            alertMessage.errorMessage("Please fill in all fields and select a role!");
            return;
        }

        String sql = "";
        switch (role.toLowerCase()) {
            case "admin":
                sql = "SELECT * FROM admin WHERE admin_username = ? AND password = ?";
                break;
            case "user":
                sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                break;
            case "driver":
                sql = "SELECT * FROM drivers WHERE username = ? AND password = ?";
                break;
            default:
                alertMessage.errorMessage("Invalid role selected!");
                return;
        }

        connect = databaseManager.getConnection();
        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                switch (role.toLowerCase()) {
                    case "admin":
                        Admin.username = username;
                        Admin.id = resultSet.getInt("id");
                        break;
                    case "user":
                        User.email = username;
                        User.id = resultSet.getInt("user_id");
                        break;
                    case "driver":
                        Driver.username = username;
                        Driver.id = resultSet.getInt("drivers_id");
                        break;
                }

                alertMessage.successMessage(role + " login successful!");

            } else {
                alertMessage.errorMessage("Incorrect username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alertMessage.errorMessage("Database error: " + e.getMessage());
        }
    }
    @FXML
    public void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            showPasswordField.setText(hiddenPasswordField.getText());
            showPasswordField.setVisible(true);
            hiddenPasswordField.setVisible(false);
        } else {
            hiddenPasswordField.setText(showPasswordField.getText());
            showPasswordField.setVisible(false);
            hiddenPasswordField.setVisible(true);
        }
    }
}
