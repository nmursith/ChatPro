package Model;

import Controller.UserBubbleController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by mmursith on 12/15/2015.
 */
public class UserBubble {
    private Parent root;
    private Vector<Object> list = new Vector<>();
    public static void main(String [] args){

    }
    public UserBubble(String name, String  chatMessage, String time) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userbubble.fxml"));
        root = fxmlLoader.load();
        UserBubbleController userBubbleController= fxmlLoader.<UserBubbleController>getController();

  //      System.out.println(userBubbleController);

                userBubbleController.setUsermessage(chatMessage);
                userBubbleController.setTime(time);
                userBubbleController.setUsernamename(name);



    }

    public Parent getRoot() {
        GridPane.setHalignment(root, HPos.LEFT);
        return root;
    }
}
