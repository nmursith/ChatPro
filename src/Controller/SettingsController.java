package Controller;

import Model.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SettingsController  implements Initializable {
    public TextField destination;
    public TextField topic;
    public TextField subscription;
    public TextField operator;
    public TextField URL;

    public Button cancelConfiguration;
    public Button okConfiguration;
    public Button applyConfiguration;

    public Button cancelVariable;
    public Button okVariable;
    public Button applyVariable;
    public Button addVariable;
    public Button deleteVariable;
    public TableView tableVariables;
    public Button settings_closeButton;

    private ChatController controller;
    private Stage settingsStage;


    public SettingsController(){

    }
    public SettingsController(ChatController controller) throws IOException {
        this.controller = controller;
        this.settingsStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        Parent root = fxmlLoader.load();

        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);

        Scene scene = new Scene(root);//, 550, 605);
//            SettingsController chatController = fxmlLoader.<SettingsController>getController();
//            chatController.setStage(primaryStage);
        settingsStage.setScene(scene);
        System.out.println("show");
        //FlatterFX.style();
        settingsStage.initStyle(StageStyle.UNDECORATED);

        settingsStage.setResizable(false);

    }


    public void showSettingsWindow() throws Exception {
      //  controller.getStage().toBack();
        System.out.println(settingsStage);
       // setting.start(settingsStage);



        settingsStage.show();
        fillConfiguration();
        fillVariable();
    }

    private void fillVariable() {
    }

    private void fillConfiguration() {
        Configuration oldConfiguration = ConfigurationController.readConfig();
//        destination;
//        topic;
//        subscription;
//        operator;
//        URL;
        destination.setText(oldConfiguration.getDestination());
        topic.setText(oldConfiguration.getTopic());
        subscription.setText(oldConfiguration.getSubscription());
        operator.setText(oldConfiguration.getOperator());
        URL.setText(oldConfiguration.getURL());
    }



    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {

    }


}
