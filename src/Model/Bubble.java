package Model;

import Controller.ChatController;
import javafx.scene.control.Label;

/**
 * Created by mmursith on 12/8/2015.
 */

public class Bubble {
    private Label fromBubble;
    private Label toBubble;


    public Bubble(String message, ChatController controller){
        controller.getScene().getStylesheets().add(getClass().getResource("bubble.css").toExternalForm());

        fromBubble = new Label(message);
        fromBubble.getStyleClass().add("fromLabel");
//        GridPane.setHalignment(chatMessage, i % 2 == 0 ? HPos.LEFT : HPos.RIGHT);

        toBubble = new Label(message);
        toBubble.getStyleClass().add("toLabel");
        //chatController.getScene().getStylesheets().add(getClass().getResource("bubble.css").toExternalForm());

    }

    public Label getFromBubble() {
        return fromBubble;
    }

    public Label getToBubble() {
        return toBubble;
    }
}
