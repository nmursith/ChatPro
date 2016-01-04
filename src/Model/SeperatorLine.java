package Model;

import Controller.SeparatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by mmursith on 1/4/2016.
 */
public class SeperatorLine {
    SeparatorController separatorController;
    Parent root;

    public SeperatorLine(BindOperator bindOperator) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SeperatorLine.class.getResource("seperator.fxml"));
        root = fxmlLoader.load();
        separatorController = fxmlLoader.<SeparatorController>getController();
        separatorController.setBindOperator(bindOperator);
    }

    public Parent getSeperator()  {
        System.out.println("sending sperto");
        return root;
    }

}
