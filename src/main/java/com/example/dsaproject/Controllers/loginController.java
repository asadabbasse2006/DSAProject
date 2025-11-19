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
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button adminLogin;
    @FXML private Button studentLogin;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private final AlertMessage alertMessage = new AlertMessage();

    public void handleStudentLogin(){
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isEmpty() || password.isEmpty()){
            alertMessage.errorMessage("Invalid Credentials....");
        }
        String sql = "SELECT * FROM users WHERE email = ?, password = ?";
        connect = databaseManager.getConnection();
        try(PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setString(1,email);
            ps.setString(2,password);
            ResultSet result = ps.executeQuery();
            if (result.next()){
                User.email = email;
                User.password = password;
                alertMessage.successMessage("Successfully Logged In");
            }else{
                alertMessage.errorMessage("Incorrect username or password");
            }
        }catch (Exception e){
            alertMessage.errorMessage("Error: " + e.getMessage());
        }
    }

    public void handleAdminLogin(){

    }

    public void handleSignup(){

    }
}
