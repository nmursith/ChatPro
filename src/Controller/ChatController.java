package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class ChatController{



    @FXML public Label closeLabel;
    @FXML public Label minimizeLabel;
    @FXML public Pane titleBar;

    @FXML public Button sendButton;
    @FXML public Button CloseButton;
    @FXML public TextArea messageTextField;
    @FXML public Label Username;

    @FXML public Button AddChatBtn;
    @FXML public ListView<UserItem> chatUsersList;

    @FXML public ScrollPane messageDisplay;
    @FXML private ContextMenu variablesMenu;

    private double xOffset;
    private double yOffset;

    private final HashMap<String, BindOperator> hashMapOperator;
    private volatile  ChatController controller =null;
    private Scene scene;
    private GridPane chatHolder;
    private volatile String previousID; // previous opertor message


    final ObservableList<UserItem> listItems = FXCollections.observableArrayList();
    private Vector<String> messageProducerID;
    private final ArrayList<Variable> contextMenuVariables;
    private final Configuration config;
    private  OperatorController operatorController ;
    private  HistoryController historyController;
    private NetworkDownHandler networkHandler;
    private final String defaultOperator;
    private volatile boolean isOnline;
    private Stage stage;

    public ChatController() throws JMSException {



        hashMapOperator = new HashMap<>();
        controller =this;
        previousID = null;
        config = ConfigurationController.readConfig();
        //operatorController = new OperatorController("operator0", "chat.*",this);
        this.isOnline = false;


        String ID = Constant.getRandomString();

        try{
            Operator operator = new Operator(ID, ID);
            boolean isConnected = operator.isConnected();


                     System.out.println("startup:  " + isConnected);
            if (isConnected) {
                isOnline = true;
                networkHandler = null;
                System.out.println("connected");
            }
            else {
                isOnline = false;
                System.out.println("initial Offline");


//                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    networkHandler = new NetworkDownHandler();
                    networkHandler.start();
                });
//                latch.await();

            }
            if(isOnline)
                operatorController = new OperatorController(config.getOperator(), config.getTopic(),this);

        }
        catch (Exception e){
            isOnline = false;
            System.out.println("initial Offline exception");
            networkHandler = new NetworkDownHandler();
            networkHandler.start();

        }

        //chatBubble = new TextArea();
        chatHolder = new GridPane();

        defaultOperator = ConfigurationController.readConfig().getOperator();// "operator1";
        messageProducerID = new Vector<>();
        contextMenuVariables = (ArrayList<Variable>) VariablesController.readVariables();


   //     listItems.add("operator0");
    //    System.out.println(hashMapOperator);


//        chatUsersList.setItems(listItems);
        Platform.runLater(() -> {

            messageTextField.setDisable(true);
            sendButton.setDisable(true);
            this.Username.getStyleClass().add("username");
            messageDisplay.setContent(chatHolder);
            messageDisplay.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            addMenuItems();
            if(isOnline) {
                hashMapOperator.put(defaultOperator, new BindOperator(operatorController, getGridPane()));
                historyController = hashMapOperator.get(config.getSubscription()).getHistoryController();
            }


        });


    }


    public void getMyMessage() {

        sendButton.setOnMouseClicked(event -> {
            try {
                sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
    public void sendMessage() throws IOException {
        String myMessage = messageTextField.getText();

        ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());

        try {
            if(!myMessage.trim().equals("") && !myMessage.trim().equalsIgnoreCase("exit")){

                int counter = (int) operatorController.getMessageCounter();
                //             System.out.println("value chat:  "+counter+"     ****"+operatorController);

                OperatorBubble bubble = new OperatorBubble(defaultOperator, myMessageMod.getTextMessage(), myMessageMod.getTime() );
                //       GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);



                historyController.writehistory(counter, defaultOperator,myMessageMod);
                chatHolder.addRow(counter, bubble.getRoot());

                messageDisplay.setContent(chatHolder);

                myMessage = getReplacedVariables(myMessage);

                myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());
                operatorController.sendMessage(myMessageMod, operatorController);

                //System.out.println("Max:  "+ messageDisplay.getVmax());
                Thread.sleep(50);
                Platform.runLater(() -> messageDisplay.setVvalue(messageDisplay.getVmax()));


            }

            else if(myMessage.trim().equalsIgnoreCase("exit")){
                if(!operatorController.getSubscriptionName().equals(defaultOperator)) {

//                    operatorController.sendMessage(myMessageMod, operatorController);
//                    int counter = (int) operatorController.getMessageCounter();
                    //             System.out.println("value chat:  "+counter+"     ****"+operatorController);

//                    OperatorBubble bubble = new OperatorBubble(defaultOperator, myMessageMod.getTextMessage(),myMessageMod.getTime() );
//                    //           GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
//
//                    historyController.writehistory(counter, "operator",myMessageMod);
//                    chatHolder.addRow(counter, bubble.getRoot());
//                    //          chatBubble.appendText("Admin : "+myMessageMod);
//                    messageDisplay.setContent(chatHolder);
//                    Thread.sleep(50);
//                    Platform.runLater(() -> messageDisplay.setVvalue(messageDisplay.getVmax()));

                    controller.closeChat();//closeConnection();
                }
                else {

                    System.exit(0);
                }
            }



        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
        messageTextField.setText("");
    }

    public String getReplacedVariables(String message){
        String replacedmessage = message;

        for (Variable variable:contextMenuVariables) {
            if(replacedmessage.contains("{"+variable.getName()+"}")) {
                String ID = variable.getID();
                String name = "{"+variable.getName()+"}";
                replacedmessage = replacedmessage.replace(name, ID);


            }
        }


        return  replacedmessage;
    }
    // Set usernames from the users list to Header label
    public void setUsername() {

        try {
            String message = messageTextField.getText();
            if(previousID !=null) {
             //   System.out.println("ID Selected:  "+ previousID + "     "+ hashMapOperator.get(previousID));
             //   chatUsersList.getItems().get(messageProducerID.indexOf(previousID)).setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
                if(message!=null)
                    hashMapOperator.get(previousID).setTypedMessage(message.trim());
            }
            messageTextField.setText("");


            UserItem useritem = chatUsersList.getSelectionModel().getSelectedItem();
         //   useritem.setStyle("-fx-background-color:#e7f0f5; -fx-border-color:#e7f0f5;");
            useritem.getThumbUserName().setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; ");
            String name = useritem.getUser().getUserName();
            String userID = useritem.getUser().getSubscriptionName();
            previousID = userID;
    //        System.out.println("previousID:     "+previousID);

            Username.setText(name);
            Platform.runLater(() -> {
           //     chatBubble = hashMapOperator.get(name).getTextArea();
       //         System.out.println(chatHolder +"                    ");

                try{
                    messageTextField.setText(hashMapOperator.get(userID).getTypedMessage());
                    chatHolder = hashMapOperator.get(userID).getChatHolder();
                    operatorController = hashMapOperator.get(userID).getOperatorController();
                    historyController = hashMapOperator.get(userID).getHistoryController();
                    messageDisplay.setContent(chatHolder);
                    messageDisplay.setVvalue(messageDisplay.getVmax());

                }
                catch (NullPointerException e){
                    //e.printStackTrace();
                }



            });
            if(hashMapOperator.get(userID).getChatHolder().isDisabled()) {
                sendButton.setDisable(true);
                messageTextField.setDisable(true);
            }
            else {
                sendButton.setDisable(false);
                messageTextField.setDisable(false);
            }


        }

        catch (RuntimeException r ){
        //        r.printStackTrace();
        }
    }

    public void setUsername(UserItem useritem) {

        try {
            String message = messageTextField.getText();
            if(previousID !=null) {
               // chatUsersList.getItems().get(messageProducerID.indexOf(previousID)).setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
                chatUsersList.getItems().get(messageProducerID.indexOf(previousID)).getThumbUserName().setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; ");
                //   System.out.println("ID Selected:  "+ previousID + "     "+ hashMapOperator.get(previousID));
                if(message!=null)
                    hashMapOperator.get(previousID).setTypedMessage(message.trim());
            }
            messageTextField.setText("");
            //useritem.setStyle("-fx-background-color:#e7f0f5; -fx-border-color:#e7f0f5;");



            String name = useritem.getUser().getUserName();
            String userID = useritem.getUser().getSubscriptionName();
            previousID = userID;
            //        System.out.println("previousID:     "+previousID);

            Username.setText(name);
            Platform.runLater(() -> {
                //     chatBubble = hashMapOperator.get(name).getTextArea();
                //         System.out.println(chatHolder +"                    ");

                try{
                    messageTextField.setText(hashMapOperator.get(userID).getTypedMessage());
                    chatHolder = hashMapOperator.get(userID).getChatHolder();
                    operatorController = hashMapOperator.get(userID).getOperatorController();
                    historyController = hashMapOperator.get(userID).getHistoryController();
                    messageDisplay.setContent(chatHolder);
                    messageDisplay.setVvalue(messageDisplay.getVmax());
                    Thread.sleep(10);
                    useritem.getThumbUserName().setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; ");

                }
                catch (NullPointerException | InterruptedException e){
                    e.printStackTrace();
                }


            });

            if(hashMapOperator.get(userID).getChatHolder().isDisabled()) {
                sendButton.setDisable(true);
                messageTextField.setDisable(true);
            }
            else {
                sendButton.setDisable(false);
                messageTextField.setDisable(false);
            }

//            try {
//                Thread.sleep(100);
//                Platform.runLater(() -> useritem.getThumbUserName().setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; "));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        }

        catch (RuntimeException r ){
            //r.printStackTrace();
        }
    }



    public ChatMessage getObjectMessage(String messageText, String producerID){
        ChatMessage chatMessage =  new ChatMessage();
        chatMessage.setProducerID(producerID);
        chatMessage.setTextMessage(messageText);
        return chatMessage;
    }

    // Get Usernames from TextField and add them to the ArrayList
//    public void setUserList() throws JMSException {
//        System.out.println("Click");
//        String tempName = sampleNameInput.getText();
//        listItems.add(tempName);
//        chatUsersList.setItems(listItems);
//        sampleNameInput.clear();
//        createChatSpace();
//
//    }

    // Remove users from the chat and ArrayList
    public void closeChat() throws JMSException, IOException {

        int index = chatUsersList.getSelectionModel().getSelectedIndex();
        System.out.println("Closing index: "+ index);
      //  operatorController.closeConnection();

        if(!listItems.isEmpty() ) {

            String myMessage = "exit";
            ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());


            int counter = (int) operatorController.getMessageCounter();

            OperatorBubble bubble = new OperatorBubble(defaultOperator, myMessageMod.getTextMessage(),myMessageMod.getTime() );

            historyController.writehistory(counter, defaultOperator,myMessageMod);
            chatHolder.addRow(counter, bubble.getRoot());
            messageDisplay.setContent(chatHolder);
            operatorController.sendMessage(myMessageMod, operatorController);

            UserItem useritem = controller.getListItems().get(index);
            String userID = useritem.getUser().getSubscriptionName();
//            hashMapOperator.remove(name);
 //           hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
            //listItems.remove(index);
            hashMapOperator.get(userID).getChatHolder().setDisable(true);
            sendButton.setDisable(true);
            messageTextField.setDisable(true);
            useritem.setDisable(true);

//            if(index>0) {
//                 useritem = controller.getListItems().get(index-1);
//
//                userID = useritem.getUser().getSubscriptionName();
//
//                //chatBubble = hashMapOperator.get(name).getTextArea();
//                chatHolder = hashMapOperator.get(userID).getChatHolder();
//                operatorController = hashMapOperator.get(userID).getOperatorController();
//                messageDisplay.setContent(chatHolder);
//
//            }

        }


    }


    public void closeChat(UserItem useritem) throws JMSException, IOException {
        int index = chatUsersList.getSelectionModel().getSelectedIndex();
        System.out.println("Closing index: "+ index);
        //  operatorController.closeConnection();

        if(!listItems.isEmpty() ) {

            String myMessage = "exit";
            ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());

            operatorController.sendMessage(myMessageMod, operatorController);
          //  System.out.println("exit");
            int counter = (int) operatorController.getMessageCounter();

            OperatorBubble bubble = new OperatorBubble(defaultOperator, myMessageMod.getTextMessage(), myMessageMod.getTime() );

            historyController.writehistory(counter, defaultOperator,myMessageMod);
            chatHolder.addRow(counter, bubble.getRoot());
            messageDisplay.setContent(chatHolder);


    //        UserItem useritem = controller.getListItems().get(index);
            String userID = useritem.getUser().getSubscriptionName();
//            hashMapOperator.remove(name);
            //           hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
            //listItems.remove(index);
            hashMapOperator.get(userID).getChatHolder().setDisable(true);
            sendButton.setDisable(true);
            messageTextField.setDisable(true);
            useritem.setDisable(true);


        }


    }
    public void doSendMessage(Event event) throws IOException {

        if (((KeyEvent)event).getCode().equals(KeyCode.ENTER)){
            sendMessage();
            event.consume();
            //System.out.println("sending");

        }
    }

    public  void addMenuItems(){

        for (Variable variable :contextMenuVariables) {
            MenuItem menuitem = new MenuItem(variable.getName());
            menuitem.setId(variable.getID());
            variablesMenu.getItems().add(menuitem);
        }
    }
//    public void createChatSpace() throws JMSException {
//
////        chatBubble.setPrefSize(309,362);
////        chatBubble.setLayoutX(0);
////        chatBubble.setLayoutY(0);
////        chatBubble.setEditable(false);
//
//
//     chatHolder = getGridPane();
//
//        messageDisplay.setContent(chatHolder);
//
//
//   //     isSet = true;
//
//
//
//    }
//    private  void applyDimensions(){
////        chatBubble.setPrefSize(309,362);
////        chatBubble.setLayoutX(0);
////        chatBubble.setLayoutY(0);
////        chatBubble.setEditable(false);
//            chatHolder = getGridPane();
////        chatHolder.setMaxSize(431, 413);
//// //       System.out.println("MessageDisplay"+messageDisplay);
////        //messageDisplay.setFitToWidth(true);
////
////
////        chatHolder.setVgap(7);
////        ColumnConstraints c1 = new ColumnConstraints();
////        c1.setPercentWidth(100);
////        chatHolder.getColumnConstraints().add(c1);
//        messageDisplay.setContent(chatHolder);
//    }

    public void closeAllConnections() throws JMSException {
        if(!listItems.isEmpty()) {
            String myMessage = "exit";
       for(int index=0; index< listItems.size(); index++) {
                UserItem useritem = controller.getListItems().get(index);

                String name = useritem.getUser().getSubscriptionName();

                ChatMessage myMessageMod = getObjectMessage(myMessage, hashMapOperator.get(defaultOperator).getOperatorController().getSubscriptionName());
                hashMapOperator.remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().sendMessage(myMessageMod, operatorController);
                hashMapOperator.get(defaultOperator).getOperatorController().closeConnection();
             //   hashMapOperator.get(defaultOperator).getOperatorController().getExecutor().shutdown();
              //  while(! hashMapOperator.get(defaultOperator).getOperatorController().getExecutor().isTerminated()){}
                listItems.remove(index);
                System.out.println("Removed");
            }

        }

    }
    public void setSelectedItem(ActionEvent actionEvent) {
        String item = " {"+((MenuItem)(actionEvent.getTarget())).getText()+"}";
        messageTextField.appendText(item);

    }
    public synchronized ChatController getInstance(){
        if(controller==null){
            controller = this;
        }
        return  controller;
    }


    public void closeApp() throws JMSException {
        controller.closeAllConnections();
        closeLabel.setOnMousePressed(event -> System.exit(0));
        closeLabel.setOnMouseClicked(event -> System.exit(0));

        //closeLabel.setOnMouseReleased(event -> System.exit(0));
    }
//
//    public void minimizeApp() throws JMSException {
//       // stage.setFocused(false);
//        //System.out.println(stage.isFocused());
//
//        minimizeLabel.setOnMouseClicked(event -> {
//
//
//
//        });
//
//        //minimizeLabel.setOnMouseClicked(event -> stage.setIconified(true));
//        //minimizeLabel.setOnMouseReleased(event -> stage.setIconified(true));
//        //minimizeLabel.setOnMousePressed(event -> System.out.println("minimized"));
//    }

    static boolean firstClick = false;
    public void moveApp(){
        titleBar.setOnMousePressed(event -> {
            if(!firstClick){
                firstClick = true;
            }else{
                //Do nothing
            }
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        titleBar.setOnMouseDragged(event -> {
            if(!firstClick){
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
                firstClick=true;
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }else{
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }

        });
    }


    public void minimizeApp(Event event) {
        stage.setIconified(true);

    }

    public void hoverCloseLabel(){
        closeLabel.setOnMouseEntered(event1 -> closeLabel.setOpacity(0.2));
    }

    public void exitOnClose(){
        closeLabel.setOnMouseExited(event1 -> closeLabel.setOpacity(1));
    }

    public void hoverMinimizeLabel(){
        minimizeLabel.setOnMouseEntered(event1 -> minimizeLabel.setOpacity(0.2));
    }

    public void exitOnMinimize(){
        minimizeLabel.setOnMouseExited(event1 -> minimizeLabel.setOpacity(1));
    }

    public void hoverSendButton(){
        sendButton.setOnMouseEntered(event1 -> sendButton.setOpacity(0.6));
    }

    public void exitOnSend(){
        sendButton.setOnMouseExited(event1 -> sendButton.setOpacity(1));
    }




    public GridPane getGridPane() {
        GridPane gridPane = new GridPane();
        //gridPane.setMaxSize(431, 413);
        int width = 400;
        gridPane.setPrefWidth(width);
        gridPane.setMinWidth(width);
        gridPane.setMaxWidth(width);
        gridPane.setPrefHeight(413);
        gridPane.setVgap(7);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(100);
        gridPane.getColumnConstraints().add(c1);
        return gridPane;
    }

    public GridPane getChatHolder() {
        return chatHolder;
    }

    public void setChatHolder(GridPane chatHolder) {
        this.chatHolder = chatHolder;
    }

    public HistoryController getHistoryController() {
        return historyController;
    }

    public void setHistoryController(HistoryController historyController) {
        this.historyController = historyController;
    }

    public OperatorController getOperatosrController() {
        return operatorController;
    }

    public void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public void setSendButton(Button sendButton) {
        this.sendButton = sendButton;
    }

    public Button getCloseButton() {
        return CloseButton;
    }

    public void setCloseButton(Button closeButton) {
        CloseButton = closeButton;
    }

    public TextArea getMessageTextField() {
        return messageTextField;
    }

    public void setMessageTextField(TextArea messageTextField) {
        this.messageTextField = messageTextField;
    }

    public Label getUsername() {
        return Username;
    }

    public void setUsername(Label username) {
        Username = username;
    }



    public Button getAddChatBtn() {
        return AddChatBtn;
    }

    public void setAddChatBtn(Button addChatBtn) {
        AddChatBtn = addChatBtn;
    }

    public ListView<UserItem> getChatUsersList() {
        return chatUsersList;
    }

    public void setChatUsersList(ListView<UserItem> chatUsersList) {
        this.chatUsersList = chatUsersList;
    }



    public ScrollPane getMessageDisplay() {
        return messageDisplay;
    }

    public void setMessageDisplay(ScrollPane messageDisplay) {
        this.messageDisplay = messageDisplay;
    }

    public ObservableList<UserItem> getListItems() {
        return listItems;
    }

    public HashMap<String, BindOperator> getHashMapOperator() {
        return hashMapOperator;
    }

    public Vector<String> getMessageProducerID() {
        return messageProducerID;
    }

    public void setMessageProducerID(Vector<String> messageProducerID) {
        this.messageProducerID = messageProducerID;
    }



    public String getPreviousID() {
        return previousID;
    }

    public void setPreviousID(String previousID) {
        this.previousID = previousID;
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setScene(Scene scene, Stage stage) {
       // System.out.println("setting scene");
      //  System.out.println(hashMapOperator);
        this.scene = scene;
        this.stage = stage;
    }



    private class NetworkDownHandler extends Thread{
        Thread thread = this;
        public void run() {
            thread = Thread.currentThread();
            System.out.println(isOnline);
            String ID = Constant.getRandomString();
                while (!isOnline) {
                    try {
                        System.out.println("Trying to resolve");
                        Operator operator = new Operator(ID, ID);
                        boolean isConnected = operator.isConnected();

                        //         System.out.println("inside:  " + isOnline);
                        if (isConnected) {
                            isOnline = true;
                            System.out.println("Re-connected");
                        }
                        else
                            isOnline = false;


                    } catch (IllegalStateException e) {
                        isOnline = false;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println("Offline: Session");
                        System.out.println("Re-connected");
                    } catch (JMSException e) {
                        isOnline = false;
                        try {

                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println("Offline: JMS");
                        System.out.println("Re-connected");
                    }
                }

            if(isOnline){
                System.out.println("Re-connected and put the operator");
                try {
                    OperatorController operatorController = new OperatorController(config.getOperator(), config.getTopic(),controller);
                    hashMapOperator.put(defaultOperator, new BindOperator(operatorController, getGridPane()));
                    historyController = hashMapOperator.get(config.getSubscription()).getHistoryController();
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }

            stopThread();
        }

        public  void stopThread(){
            Thread t = thread;
            thread = null;
            t.interrupt();
        }
    }




}











