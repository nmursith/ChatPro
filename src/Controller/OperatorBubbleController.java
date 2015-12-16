package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmursith on 12/16/2015.
 */
public class OperatorBubbleController implements Initializable {
    @FXML
    Label operatorname;
    @FXML
    Label operatormessage;
    @FXML
    Label time;

    public  OperatorBubbleController(){
    }

//    public OperatorBubbleController(String name, String message, String time){
////        Platform.runLater(() -> {
////            this.operatormessage.setText(message);
////            this.operatorname.setText(name);
////            this.time.setText(time);
////        });
//    }

    public void setOperatorname(String operatorname) {
        this.operatorname.setText(operatorname);
    }

    public void setOperatormessage(String operatormessage) {
        this.operatormessage.setText(operatormessage);

    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
