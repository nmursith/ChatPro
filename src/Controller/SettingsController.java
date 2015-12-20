package Controller;

import Model.Configuration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SettingsController  {
    @FXML private TextField destination;
    @FXML private TextField topic;
    @FXML private TextField subscription;
    @FXML private TextField operator;
    @FXML private TextField URL;

    @FXML private Button cancelConfiguration;
    @FXML private Button okConfiguration;
    @FXML private Button applyConfiguration;

    @FXML private Button cancelVariable;
    @FXML private Button okVariable;
    @FXML private Button applyVariable;
    @FXML private Button addVariable;
    @FXML private Button deleteVariable;
    @FXML private TableView tableVariables;
    @FXML private Button settings_closeButton;

    private SettingsController settingController;
    private ChatController controller;
    private Stage settingsStage;


    public SettingsController(){

    }

    public SettingsController(ChatController controller) throws IOException {
        this.controller = controller;
        this.settingsStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        Parent root = fxmlLoader.load();
        SettingsController settingsC= fxmlLoader.<SettingsController>getController();
        settingController = settingsC;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fillConfiguration();
                fillVariable();
            }
        });
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
        System.out.println("old Configuration "+ settingController.destination +"    "+ settingController.topic);
        settingController.destination.setText(oldConfiguration.getDestination());
        settingController.topic.setText(oldConfiguration.getTopic());
        settingController.subscription.setText(oldConfiguration.getSubscription());
        settingController.operator.setText(oldConfiguration.getOperator());
        settingController.URL.setText(oldConfiguration.getURL());
    }





}
