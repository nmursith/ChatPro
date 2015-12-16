package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by mmursith on 12/15/2015.
 */
public class UserBubble extends Parent {
    private UserBubble root;
    public static void main(String [] args){

    }
    public UserBubble() throws IOException {
        root = this;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bubble.fxml"));
        root = fxmlLoader.load();
    }

    public Parent getRoot() {
        return root;
    }
}
