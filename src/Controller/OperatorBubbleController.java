package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mmursith on 12/16/2015.
 */
public class OperatorBubbleController implements Initializable {
    public ImageView operator_pic_image_view;
    @FXML
    Label operatorname;
    @FXML
    Label operatormessage;
    @FXML
    Label time;

    public  OperatorBubbleController(){
    }


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

    public ImageView getOperator_pic_image_view() {
        return operator_pic_image_view;
    }

    public void setOperator_pic_image_view(Image operator_pic_image_view) {
        this.operator_pic_image_view.setImage(operator_pic_image_view);
    }
}
