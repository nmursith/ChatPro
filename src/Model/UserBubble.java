package Model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by mmursith on 12/15/2015.
 */
public class UserBubble {
    private Parent root;
    public static void main(String [] args){

    }
    public UserBubble() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userbubble.fxml"));

        root = fxmlLoader.load();

    }

    public Parent getRoot() {
        return root;
    }
}
