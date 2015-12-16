package Controller;

import Model.ChatMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by mmursith on 12/16/2015.
 */
public class OperatorBubbleController {
    @FXML private Label operatorname;
    @FXML private Label operatormessage;
    @FXML private Label time;

    public OperatorBubbleController(String name, ChatMessage message){
        Platform.runLater(() -> {
            operatormessage.setText(message.getTextMessage());
            operatorname.setText(name);
            time.setStyle(message.getTime());
        });
    }

}
