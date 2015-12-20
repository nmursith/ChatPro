package Model;

import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by mmursith on 12/20/2015.
 */
public class Settings {

        public void start(Stage primaryStage) throws Exception{
            //  AquaFx.style();
            System.out.println("stage  "+ primaryStage);

            //Parent root = FXMLLoader.load(getClass().getResource("Operator.fxml"));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
            Parent root = fxmlLoader.load();

            root.setCache(true);
            root.setCacheHint(CacheHint.DEFAULT);

            Scene scene = new Scene(root);//, 550, 605);
//            SettingsController chatController = fxmlLoader.<SettingsController>getController();
//            chatController.setStage(primaryStage);
            primaryStage.setScene(scene);
            System.out.println("show");
            //FlatterFX.style();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
            primaryStage.setResizable(false);

        }


}
