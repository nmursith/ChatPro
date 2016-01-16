package Controller;

import Model.*;
import com.sun.deploy.panel.TextFieldProperty;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import java.util.ArrayList;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SettingsController  implements ChangeListener, EventHandler<KeyEvent> {

    @FXML public Button cancelConfiguration;
    @FXML public Button okConfiguration;

    @FXML public Button cancelVariable;
    @FXML public Button okVariable;

    @FXML public Button addVariable;
    @FXML public Button deleteVariable;
    @FXML public Button settings_closeButton;
    @FXML public ScrollPane tableViewContainer;
    @FXML  public Button removeVariableButton;
    @FXML private Button applyConfigurationButton;
    @FXML private  Button applyVariableButton;
    @FXML private TextField destination;
    @FXML private TextField topic;
    @FXML private TextField subscription;
    @FXML private TextField operator;
    @FXML private TextField URL;
    ObservableList<Variable> data;
    private TableView<Variable> tableVariables;
    private Stage parentStage;
    private boolean isError;
    private SettingsController settingController;
    private ChatController chatController;
    private Stage settingsStage;
    private TableColumn variableName;
    private TableColumn variableID;
    public SettingsController(){

    }
    public void setListeners(){
        isError = false;
        tableVariables = new TableView<>();

        topic.setOnKeyReleased(this);
        operator.setOnKeyReleased((this));
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




    public void showSettingsWindow(Stage parentStage) throws Exception {
      //  controller.getStage().toBack();
        System.out.println(settingsStage);
       // setting.start(settingsStage);

//  System.out.println(settingsStage);
        settingsStage.show();
        double x = parentStage.getX() + parentStage.getWidth() / 2 - settingsStage.getWidth() / 2;
        double y = parentStage.getY() + parentStage.getHeight() / 2 - settingsStage.getHeight() / 2;
        settingsStage.setX(x);
        settingsStage.setY(y);
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



        variableName = new TableColumn("Variable name");
        variableName.setPrefWidth(270);
        variableName.setEditable(true);

        variableName.setCellValueFactory(new PropertyValueFactory<Variable,String>("Name"));
        variableName.setCellFactory(TextFieldTableCell.forTableColumn());

        variableName.cellFactoryProperty().addListener((observable, oldValue, newValue) -> applyVariableButton.setDisable(false));
        variableName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Variable, String>> () {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Variable, String> t) {
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setName(t.getNewValue());
                        applyVariableButton.setDisable(false);
                    }
                }
        );

        variableID = new TableColumn("Variable ID");
        variableID.setEditable(true);
        variableID.setPrefWidth(270);

        variableID.setCellValueFactory(new PropertyValueFactory<Variable,String>("ID"));
        variableID.setCellFactory(TextFieldTableCell.forTableColumn());
        variableID.cellFactoryProperty().addListener((observable, oldValue, newValue) -> applyVariableButton.setDisable(false));

        variableID.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Variable, String>> () {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Variable, String> t) {
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setID(t.getNewValue());
                        applyVariableButton.setDisable(false);
                    }
                }
        );
        tableVariables.setItems(data);
        tableVariables.getColumns().addAll(variableName, variableID);

        tableViewContainer.setContent(tableVariables);
        tableViewContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tableVariables.setEditable(true);
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


        if(!applyConfigurationButton.isDisable() )
            apply();

        if(!isError) {
            settingsStage.hide();
            settingsStage.close();
        }
    }

    public void applyConfiguration(ActionEvent actionEvent) {
        apply();


    }
    public void apply()  {
        if(isError) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Topic Name Error.\nEg: chat.*");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(settingsStage);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.show();
        }
        {
            NetworkDownHandler networkDownHandler = new NetworkDownHandler();
            networkDownHandler.start();
        }
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

        System.out.println(!List.equals(previousList));
        if(!List.equals(previousList)){
            VariablesController.writeVariables(List);
        }

        applyVariableButton.setDisable(true);
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
        //if(removeVariableButton.isDisabled())
            applyVariableButton.setDisable(false);
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

    public Stage getParentStage() {
        return parentStage;
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
          applyConfigurationButton.setDisable(false);
    }

    @Override
    public void handle(KeyEvent event) {

        TextField textField = (TextField)event.getTarget();
        String text = textField.getText();
        String ID = textField.getId();
        String operator= "."+subscription.getText();
        String topicText = topic.getText();
        topicText = topicText.substring(0,topicText.length()-1);

        if(ID.equalsIgnoreCase("operator")) {
                subscription.setText(text);
                destination.setText(topicText+text);
            }
        else if(ID.equalsIgnoreCase("topic")){
            String prefix = text.substring(0,text.length()-2);
            String suffix = text.substring(text.length()-2,text.length());
         //   System.out.println(suffix);
            destination.setText(prefix + operator);


            if(text.substring(text.length()-2,text.length()).equals(".*")) {
                isError = false;
                topic.setStyle("-fx-text-fill: black;");
                destination.setStyle("-fx-text-fill: black;");


            }
            else {
                isError = true;
                topic.setStyle("-fx-text-fill: red;");
                destination.setStyle("-fx-text-fill: red;");

            }

        }



    }


    class NetworkDownHandler extends Thread{
        Image image_offline = new Image(getClass().getResourceAsStream("offline.png")); //===========================
        Image image_online = new Image(getClass().getResourceAsStream("online.png"));   //===========================

        Thread thread = this;

        public void run() {

            thread = Thread.currentThread();
            System.out.println(chatController.isOnline());
            String ID = Constant.operatorID;//Constant.getRandomString();
            while (!chatController.isOnline()) {
                try {
                    System.out.println("Trying to resolve");
                    Operator operator = new Operator(ID, ID);
                    boolean isConnected = operator.isConnected();

                    //         System.out.println("inside:  " + isOnline);
                    if (isConnected) {
                        chatController.statusImageView.setImage(image_online); //==========================
                        chatController.setOnline(true);
                        System.out.println("Re-connected");
                        operator.closeConnection();
                    }
                    else {
                        chatController.statusImageView.setImage(image_offline);//===========================
                        chatController.setOnline(false);
                    }
                    operator = null;

                } catch (IllegalStateException e) {
                    chatController.setOnline(false);
                    try {
                        sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Offline: Session");
                    System.out.println("Re-connected");
                } catch (JMSException e) {
                    chatController.setOnline(false);
                    try {

                        sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Offline: JMS");
                    System.out.println("Re-connected");
                }
            }

            if(chatController.isOnline() && !isError){

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
                    String prefix = currentConfiguration.getTopic().replace("*","");
                    Constant.topicPrefix = prefix;
                    Constant.configuration = currentConfiguration;
                    BindOperator bindOperator = chatController.getHashMapOperator().get(chatController.getDefaultOperator());
                    OperatorController previous = bindOperator.getOperatorController();
                    bindOperator.setOperatorController(null);

                    System.out.println("Message in que:         "+ previous.getChatMessagess().size());


                    //  System.out.println("previius:   " + chatController.getHashMapOperator().get(chatController.getDefaultOperator()).getOperatorController());

                    chatController.getHashMapOperator().remove(chatController.getDefaultOperator());
                    chatController.setConfig(currentConfiguration);


                    OperatorController operatorController = null;
                    try {
                        operatorController = new OperatorController(currentConfiguration.getOperator(), currentConfiguration.getTopic(),chatController);
                        operatorController.createSession();
                        operatorController.startDefaultOperatorAction();
                        try {
                            previous.closeConnection();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
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
                stopThread();
            }





        }

        public  void stopThread(){
            Thread t = thread;
            thread = null;
            t.interrupt();
        }
    }


}
