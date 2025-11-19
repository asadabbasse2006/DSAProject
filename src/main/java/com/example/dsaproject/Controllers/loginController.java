package com.example.dsaproject.Controllers;

// Packages import
import com.example.dsaproject.Model.Admin;
import com.example.dsaproject.Model.User;
import com.example.dsaproject.AlertMessage;
import com.example.dsaproject.DAO.databaseManager;

//JavaFX libraries
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

//SQL Libraries
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
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
        String email = emailField.getText();
        String password = passwordField.getText();
        if(email.isEmpty() || password.isEmpty()){
            alertMessage.errorMessage("Invalid Credentials....");
        }
        String sql = "SELECT * FROM admin WHERE admin_username = ? AND password = ?";
        connect = databaseManager.getConnection();
        try(PreparedStatement ps = connect.prepareStatement(sql)){
            ps.setString(1,email);
            ps.setString(2,password);
            ResultSet result = ps.executeQuery();
            if (result.next()){
                Admin.email = email;
                Admin.password = password;
                alertMessage.successMessage("Successfully Logged In");
            }else{
                alertMessage.errorMessage("Incorrect username or password");
            }
        }catch (Exception e){
            alertMessage.errorMessage("Error: " + e.getMessage());
        }
    }

    public void handleSignup(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/dsaproject/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
