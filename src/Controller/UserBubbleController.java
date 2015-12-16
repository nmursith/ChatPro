package Controller;

import Model.ChatMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by mmursith on 12/16/2015.
 */
public class UserBubbleController {
    @FXML private Label username;
    @FXML private Label usermessage;
    @FXML private Label time;

    public UserBubbleController(String name, ChatMessage message){
        Platform.runLater(() -> {
            usermessage.setText(message.getTextMessage());
            username.setText(name);
            time.setStyle(message.getTime());
        });
    }
}
