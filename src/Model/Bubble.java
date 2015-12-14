package Model;

import Controller.ChatController;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by mmursith on 12/8/2015.
 */

public class Bubble {
    private Label fromBubble;
    private Label toBubble;


    public Bubble(String message, ChatController controller){
        //Scene scene = new Scene();
        try{


            fromBubble = new Label(message);
            fromBubble.getStyleClass().add("fromLabel");
            fromBubble.setWrapText(true);

//        GridPane.setHalignment(chatMessage, i % 2 == 0 ? HPos.LEFT : HPos.RIGHT);
            GridPane.setHalignment(fromBubble, HPos.RIGHT);

            toBubble = new Label(message);
            toBubble.getStyleClass().add("toLabel");
            toBubble.setWrapText(true);
            GridPane.setHalignment(toBubble, HPos.LEFT);


        }
        catch (NullPointerException e){
            System.out.println("problem with bubble");
        }

    }

    public Label getFromBubble() {
        return fromBubble;
    }

    public Label getToBubble() {
        return toBubble;
    }
}
