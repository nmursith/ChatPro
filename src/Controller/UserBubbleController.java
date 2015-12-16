package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmursith on 12/16/2015.
 */
public class UserBubbleController implements Initializable {
    @FXML
    Label username;
    @FXML
    Label usermessage;
    @FXML
    Label time ;

    public UserBubbleController(){

    }
//    public UserBubbleController(String name, String message, String time){
//
////            this.usermessage = new Label();
////            this.username = new Label();
////            this.time = new Label();
//
//    }

    public void setUsernamename(String operatorname) {
//        System.out.println("seetting");

        this.username.setText(operatorname);
    }

    public void setUsermessage(String operatormessage) {
        this.usermessage.setText(operatormessage);

    }
    public void setTime(String time){
        this.time.setText(time);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(username);


    }
}
