package Controller;

import Model.BindOperator;
import Model.Configuration;
import Model.UserItem;
import Model.Variable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.jms.JMSException;
import java.util.ArrayList;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SettingsController  implements ChangeListener{

    public Button cancelConfiguration;
    public Button okConfiguration;
    public Button applyConfiguration;
    public Button cancelVariable;
    public Button okVariable;
    public Button applyVariable;
    public Button addVariable;
    public Button deleteVariable;
    public Button settings_closeButton;
    @FXML public ScrollPane tableViewContainer;
    @FXML private Button applyConfigurationButton;
    @FXML private  Button applyVariableButton;
    @FXML private TextField destination;
    @FXML private TextField topic;
    @FXML private TextField subscription;
    @FXML private TextField operator;
    @FXML private TextField URL;
    ObservableList<Variable> data;
     private TableView<Variable> tableVariables;


    private SettingsController settingController;
    private ChatController chatController;
    private Stage settingsStage;
    private TableColumn variableName;
    private TableColumn variableID;
    public SettingsController(){

    }
    public void setListeners(){
        tableVariables = new TableView<>();
        topic.textProperty().addListener(this);
        topic.textProperty().addListener(this);
        destination.textProperty().addListener(this);
        operator.textProperty().addListener(this);
        subscription.textProperty().addListener(this);
        operator.textProperty().addListener(this);

    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
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
        ArrayList<Variable> contextMenuVariables = (ArrayList<Variable>) VariablesController.readVariables();
         data =FXCollections.observableArrayList(contextMenuVariables);
//
//        for (Variable variable :contextMenuVariables) {
//           data.add(variable);
//        }

        tableVariables.setEditable(true);

        variableName = new TableColumn("Variable name");
        variableName.setPrefWidth(270);
        variableName.setEditable(true);
        variableName.cellFactoryProperty().addListener((observable, oldValue, newValue) -> applyVariable.setDisable(false));

        variableName.setCellValueFactory(new PropertyValueFactory<Variable,String>("Name"));

         variableID = new TableColumn("Variable ID");
        variableID.setEditable(true);
        variableID.setPrefWidth(270);
        variableID.cellFactoryProperty().addListener((observable, oldValue, newValue) -> applyVariable.setDisable(false));
        variableID.setCellValueFactory(new PropertyValueFactory<Variable,String>("ID"));


        tableVariables.setItems(data);
        tableVariables.getColumns().addAll(variableName, variableID);

        tableViewContainer.setContent(tableVariables);
        tableViewContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        System.out.println(tableVariables.getColumns().get(0));

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
        if(!okConfiguration.isDisabled())
            apply();
        settingsStage.hide();
        settingsStage.close();
    }

    public void applyConfiguration(ActionEvent actionEvent) {
        apply();


    }
    public void apply()  {
        System.out.println("Working");
        Configuration previousConfiguraion = ConfigurationController.readConfig();

        Configuration currentConfiguration = new Configuration();
        currentConfiguration.setDestination(destination.getText());
        currentConfiguration.setTopic(topic.getText());
        currentConfiguration.setSubscription(subscription.getText());
        currentConfiguration.setURL(URL.getText());
        currentConfiguration.setOperator(operator.getText());
        ConfigurationController.writeConfig(currentConfiguration);

        if(!previousConfiguraion.equals(currentConfiguration) )
        {
            applyConfigurationButton.setDisable(true);
            try {
                chatController.getHashMapOperator().get(chatController.getDefaultOperator()).getOperatorController().closeConnection();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            BindOperator bindOperator = chatController.getHashMapOperator().get(chatController.getDefaultOperator());
            OperatorController previous = bindOperator.getOperatorController();


          //  System.out.println("previius:   " + chatController.getHashMapOperator().get(chatController.getDefaultOperator()).getOperatorController());

            chatController.getHashMapOperator().remove(chatController.getDefaultOperator());
            chatController.setConfig(currentConfiguration);
            OperatorController operatorController = null;
            try {
                operatorController = new OperatorController(currentConfiguration.getOperator(), currentConfiguration.getTopic(),chatController);
                operatorController.setMessageCounter(previous.getMessageCounter());

                operatorController.setMessageProduceID(previous.getMessageProduceID());
                operatorController.getMessageProduceID().remove(0);

                operatorController.getMessageProduceID().add(0,currentConfiguration.getOperator());
                operatorController.setChatMessagess(previous.getChatMessagess());



            } catch (JMSException e) {
                e.printStackTrace();
            }
            chatController.getHashMapOperator().put(currentConfiguration.getOperator(), new BindOperator(operatorController, chatController.getGridPane()) );


            chatController.setDefaultOperator(currentConfiguration.getOperator());
//            if(chatController.isOnline()){
//                try {
//                    OperatorController operatorController = new OperatorController(currentConfiguration.getOperator(), currentConfiguration.getTopic(),chatController);
//                    chatController.getHashMapOperator().put(chatController.getDefaultOperator(), new BindOperator(operatorController, chatController.getGridPane()));
//                    //   historyController = hashMapOperator.get(config.getSubscription()).getHistoryController();
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//           else {


       //         chatController.setOnline(false);
       //         chatController.getNetworkHandler().start();

            //}
//                System.out.println("new" + chatController.getHashMapOperator().get(chatController.getDefaultOperator()).getOperatorController());

        }

     //   chatController.setUsername();
    }
    public void setOkVariable(ActionEvent actionEvent) {
        if(!okVariable.isDisabled())
            changeVariable();
        settingsStage.hide();
        settingsStage.close();


    }

    public void changeVariable(){

        ArrayList<Variable> previousList = VariablesController.readVariables();
        ObservableList<Variable> currentList = tableVariables.getItems();

        ArrayList<Variable> List = new ArrayList<>();
        for (Variable variable: currentList) {
            if(!variable.getID().trim().equals("") && !variable.getName().trim().equals("")){
                List.add(variable);
            }

        }

        if(!List.equals(previousList)){
            VariablesController.writeVariables(List);
        }
        applyVariable.setDisable(true);
        chatController.addMenuItems();

    }
    public void applyVariable(ActionEvent actionEvent) {

        changeVariable();


    }

    public void addVariable(ActionEvent actionEvent) {
        data.add(new Variable("new", "new"));
    }

    public void removeVariable(ActionEvent actionEvent) {
        Variable variable = tableVariables.getSelectionModel().getSelectedItem();
        data.remove(variable);
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


    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        applyConfigurationButton.setDisable(false);
    }
}
