package Model;

import Controller.UserBubbleController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Created by mmursith on 12/15/2015.
 */
public class UserBubble {
    private static Parent root;
    private static FXMLLoader fxmlLoader;
    private static UserBubbleController userBubbleController;

    public UserBubble(String name, String  chatMessage, String time) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userbubble.fxml"));
        root = fxmlLoader.load();
        userBubbleController= fxmlLoader.<UserBubbleController>getController();

  //      System.out.println(userBubbleController);

                userBubbleController.setUsermessage(chatMessage);
                userBubbleController.setTime(time);
                userBubbleController.setUsernamename(name);



    }

    public Parent getRoot() {
        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);

        GridPane.setHalignment(root, HPos.LEFT);
        return root;
    }



    public UserBubble() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userbubble.fxml"));
        root = fxmlLoader.load();

        userBubbleController= fxmlLoader.<UserBubbleController>getController();
        GridPane.setHalignment(root, HPos.LEFT);
        //      System.out.println(userBubbleController);





    }

    public static Parent getRoot(String name, String  chatMessage, String time) {
        userBubbleController.setUsermessage(chatMessage);
        userBubbleController.setTime(time);
        userBubbleController.setUsernamename(name);
        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);


        return root;
    }
}
