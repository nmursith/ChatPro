package Controller;

import Model.Configuration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SettingsController  implements EventHandler{

    public Button cancelConfiguration;
    public Button okConfiguration;
    public Button applyConfiguration;
    public Button cancelVariable;
    public Button okVariable;
    public Button applyVariable;
    public Button addVariable;
    public Button deleteVariable;
    public Button settings_closeButton;
    @FXML private Button applyConfigurationButton;
    @FXML private  Button applyVariableButton;
    @FXML private TextField destination;
    @FXML private TextField topic;
    @FXML private TextField subscription;
    @FXML private TextField operator;
    @FXML private TextField URL;

    @FXML private TableView tableVariables;


    private SettingsController settingController;
    private ChatController controller;
    private Stage settingsStage;


    public SettingsController(){

    }

//    public SettingsController(ChatController controller) throws IOException {
//        this.controller = controller;
//        this.settingsStage = new Stage();
//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
//        Parent root = fxmlLoader.load();
//        SettingsController settingsC= fxmlLoader.<SettingsController>getController();
//        settingController = settingsC;
//
//        root.setCache(true);
//        root.setCacheHint(CacheHint.DEFAULT);
//
//        Scene scene = new Scene(root);//, 550, 605);
////            SettingsController chatController = fxmlLoader.<SettingsController>getController();
////            chatController.setStage(primaryStage);
//        settingsStage.setScene(scene);
//        System.out.println("show");
//        //FlatterFX.style();
//        settingsStage.initStyle(StageStyle.UNDECORATED);
//        settingsStage.setResizable(false);
//
//        settingController.topic.setOnInputMethodTextChanged(this);
//        settingController.destination.setOnInputMethodTextChanged(this);
//        settingController.operator.setOnInputMethodTextChanged(this);
//        settingController.subscription.setOnInputMethodTextChanged(this);
//        settingController.operator.setOnInputMethodTextChanged(this);
//
//        settingController.applyConfigurationButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                settingController.applyConfiguration();
//            }
//        });
//
//
//    }


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
//        System.out.println("old Configuration "+ settingController.destination +"    "+ settingController.topic);
        destination.setText(oldConfiguration.getDestination());
        topic.setText(oldConfiguration.getTopic());
        subscription.setText(oldConfiguration.getSubscription());
        operator.setText(oldConfiguration.getOperator());
        URL.setText(oldConfiguration.getURL());
    }




    public void setOkConfiguration(ActionEvent actionEvent) {
        //applyConfiguration();
        settingsStage.hide();
        settingsStage.close();
    }

    public void applyConfiguration(ActionEvent actionEvent) {
        apply();


    }
    public void apply(){
        System.out.println("Working");
        Configuration configuration = new Configuration();
        configuration.setDestination(destination.getText());
        configuration.setTopic(topic.getText());
        configuration.setSubscription(subscription.getText());
        configuration.setURL(URL.getText());
        configuration.setOperator(operator.getText());
        ConfigurationController.writeConfig(configuration);
        applyConfigurationButton.setDisable(true);
    }
    public void setOkVariable(ActionEvent actionEvent) {
        apply();
        settingsStage.close();

    }

    public void applyVariable(ActionEvent actionEvent) {
    }

    public void addVariable(ActionEvent actionEvent) {
    }

    public void removeVariable(ActionEvent actionEvent) {
    }

    @Override
    public void handle(Event event) {
        settingController.applyConfigurationButton.setDisable(false);
    }

    public Stage getSettingsStage() {
        return settingsStage;
    }

    public void setSettingsStage(Stage settingsStage) {
        this.settingsStage = settingsStage;
    }

    public void cancel(ActionEvent actionEvent) {
        settingsStage.close();
    }
}
