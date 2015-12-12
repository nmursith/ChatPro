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

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class ChatController{

    @FXML public Button sendButton;
    @FXML public Button CloseButton;
    @FXML public TextArea messageTextField;
    @FXML public Label Username;
    @FXML public TextArea chatBubble;
    @FXML public Button AddChatBtn;
    @FXML public ListView<UserItem> chatUsersList;
    @FXML public TextField sampleNameInput;
    @FXML public ScrollPane messageDisplay;
    @FXML private ContextMenu variablesMenu;

    private HashMap<String, BindOperator> hashMapOperator;
    private ChatController controller =null;
    private Scene scene;
    private GridPane chatHolder;
    private String previousID; // previous opertor message



    final ObservableList<UserItem> listItems = FXCollections.observableArrayList();
    private Vector<String> messageProducerID;


    private  OperatorController operatorController ;
    private  HistoryController historyController;
    Boolean isSet = false;
    private String defaultOperator;


    public ChatController() throws JMSException {

        System.out.println("ChatController Started");

        hashMapOperator = new HashMap<>();
        controller =this;
        previousID = "";
        Configuration config = ConfigurationController.readConfig();
        //operatorController = new OperatorController("operator0", "chat.*",this);
        operatorController = new OperatorController(config.getOperator(), config.getTopic(),this);

        //chatBubble = new TextArea();
        chatHolder = new GridPane();
        defaultOperator = ConfigurationController.readConfig().getOperator();// "operator1";
        messageProducerID = new Vector<>();



   //     listItems.add("operator0");
    //    System.out.println(hashMapOperator);

        try {
            Thread.sleep(100);

        } catch (InterruptedException e) {

        }
//        chatUsersList.setItems(listItems);
        Platform.runLater(() -> {
            try {
                createChatSpace();
                applyDimensions();
                addMenuItems();
                hashMapOperator.put(defaultOperator, new BindOperator(operatorController, getGridPane()));
                historyController = hashMapOperator.get(config.getSubscription()).getHistoryController();



            } catch (JMSException e) {
                e.printStackTrace();
            }
        });


    }


    public void getMyMessage() {

        sendButton.setOnMouseClicked(event -> {
            sendMessage();

        });
    }
    public void sendMessage(){
        String myMessage = messageTextField.getText().trim();
        ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());


        try {
            if(!myMessage.trim().equals("") && !myMessage.trim().equalsIgnoreCase("exit")){
                operatorController.sendMessage(myMessageMod, operatorController);
                int counter = (int) operatorController.getMessageCounter();
                //             System.out.println("value chat:  "+counter+"     ****"+operatorController);
                Bubble bubble = new Bubble(myMessage, controller);
                //       GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                historyController.writehistory(counter, "operator",myMessage);
                chatHolder.addRow(counter, bubble.getFromBubble());
                //          chatBubble.appendText("Admin : "+myMessageMod);
                messageDisplay.setContent(chatHolder);

            }

            else if(myMessage.trim().equalsIgnoreCase("exit")){
                if(!operatorController.getSubscriptionName().equals(defaultOperator)) {


                    operatorController.sendMessage(myMessageMod, operatorController);
                    int counter = (int) operatorController.getMessageCounter();
                    //             System.out.println("value chat:  "+counter+"     ****"+operatorController);
                    Bubble bubble = new Bubble(myMessage, controller);
                    //           GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);

                    historyController.writehistory(counter, "operator",myMessage);
                    chatHolder.addRow(counter, bubble.getFromBubble());
                    //          chatBubble.appendText("Admin : "+myMessageMod);
                    messageDisplay.setContent(chatHolder);

                    controller.closeChat();//closeConnection();
                    try{
                        operatorController.closeConnection();
                    }
                    catch (NullPointerException e){

                    }


                }
                else {
                    try{
                        operatorController.closeConnection();
                    }
                    catch (NullPointerException e){

                    }
                    System.exit(0);
                }
            }



        } catch (JMSException e) {
            e.printStackTrace();
        }
        messageTextField.setText("");
    }

    // Set usernames from the users list to Header label
    public void setUsername() {

        try {
            String message = messageTextField.getText();
            if(!previousID.equals(""))
                hashMapOperator.get(previousID).setTypedMessage(message.trim());
            messageTextField.setText("");


            UserItem useritem = chatUsersList.getSelectionModel().getSelectedItem();

            String name = useritem.getUser().getUserName();
            String userID = useritem.getUser().getSubscriptionName();
            previousID = userID;
            System.out.println("userName:     "+name);

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



//                    if(name.equals(defaultOperator)){
//                        CloseButton.setDisable(true);
//                    }
//                    else {
//                        CloseButton.setDisable(false);
//                    }

                }
                catch (NullPointerException e){
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


        }

        catch (RuntimeException r ){

        }
    }

    public ChatMessage getObjectMessage(String messageText, String producerID){
        ChatMessage chatMessage =  new ChatMessage();
        chatMessage.setProducerID(producerID);
        chatMessage.setTime(ChatMessage.dateFormat.format(ChatMessage.calendar.getTime()));
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
    public void closeChat() throws JMSException {
        int index = chatUsersList.getSelectionModel().getSelectedIndex();
        System.out.println("Closing index: "+ index);
      //  operatorController.closeConnection();

        if(!listItems.isEmpty() ) {

            String myMessage = "exit";
            ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());

            operatorController.sendMessage(myMessageMod, operatorController);
            int counter = (int) operatorController.getMessageCounter();
            Bubble bubble = new Bubble(myMessage, controller);
            historyController.writehistory(counter, "operator",myMessage);
            chatHolder.addRow(counter, bubble.getFromBubble());
            messageDisplay.setContent(chatHolder);


            UserItem useritem = controller.getListItems().get(index);
            String userID = useritem.getUser().getSubscriptionName();
//            hashMapOperator.remove(name);
 //           hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
            //listItems.remove(index);
            hashMapOperator.get(userID).getChatHolder().setDisable(true);
            sendButton.setDisable(true);
            messageTextField.setDisable(true);
            useritem.setDisable(true);

            if(index>0) {
                 useritem = controller.getListItems().get(index-1);

                userID = useritem.getUser().getSubscriptionName();

                //chatBubble = hashMapOperator.get(name).getTextArea();
                chatHolder = hashMapOperator.get(userID).getChatHolder();
                operatorController = hashMapOperator.get(userID).getOperatorController();
                messageDisplay.setContent(chatHolder);

            }

        }


    }


    public void closeChat(UserItem useritem) throws JMSException {
        int index = chatUsersList.getSelectionModel().getSelectedIndex();
        System.out.println("Closing index: "+ index);
        //  operatorController.closeConnection();

        if(!listItems.isEmpty() ) {

            String myMessage = "exit";
            ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());

            operatorController.sendMessage(myMessageMod, operatorController);
            int counter = (int) operatorController.getMessageCounter();
            Bubble bubble = new Bubble(myMessage, controller);
            historyController.writehistory(counter, "operator",myMessage);
            chatHolder.addRow(counter, bubble.getFromBubble());
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
    public void doSendMessage(Event event) {

        if (((KeyEvent)event).getCode().equals(KeyCode.ENTER)){
            sendMessage();
            event.consume();
            System.out.println("sending");

        }
    }

    public  void addMenuItems(){
        ArrayList<Variable> variables = VariablesController.readVariables();
        for (Variable variable :variables) {
            MenuItem menuitem = new MenuItem(variable.getName());
            menuitem.setId(variable.getID());
            variablesMenu.getItems().add(menuitem);
        }
    }
    public void createChatSpace() throws JMSException {

//        chatBubble.setPrefSize(309,362);
//        chatBubble.setLayoutX(0);
//        chatBubble.setLayoutY(0);
//        chatBubble.setEditable(false);


        chatHolder.setMaxSize(309, 362);
        messageDisplay.setFitToWidth(true);
        chatHolder.setVgap(7);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(100);
        chatHolder.getColumnConstraints().add(c1);

        messageDisplay.setContent(chatHolder);


   //     isSet = true;

        System.out.println("Click2");


    }
    private  void applyDimensions(){
//        chatBubble.setPrefSize(309,362);
//        chatBubble.setLayoutX(0);
//        chatBubble.setLayoutY(0);
//        chatBubble.setEditable(false);

        chatHolder.setMaxSize(309, 362);
        System.out.println("MessageDisplay"+messageDisplay);
        messageDisplay.setFitToWidth(true);
        chatHolder.setVgap(7);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(100);
        chatHolder.getColumnConstraints().add(c1);
        messageDisplay.setContent(chatHolder);
    }

    public void closeAllConnections() throws JMSException {
        if(!listItems.isEmpty()) {

            for(int index=0; index< listItems.size(); index++) {
                UserItem useritem = controller.getListItems().get(index);

                String name = useritem.getUser().getSubscriptionName();
                hashMapOperator.remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().closeConnection();
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


    public GridPane getGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setMaxSize(309, 362);
        gridPane.setVgap(7);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(100);
        gridPane.getColumnConstraints().add(c1);
        return gridPane;
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

    public TextArea getChatBubble() {
        return chatBubble;
    }

    public void setChatBubble(TextArea chatBubble) {
        this.chatBubble = chatBubble;
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

    public TextField getSampleNameInput() {
        return sampleNameInput;
    }

    public void setSampleNameInput(TextField sampleNameInput) {
        this.sampleNameInput = sampleNameInput;
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

    public void sendMyMessage(ActionEvent actionEvent) {
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

    public void setScene(Scene scene) {
       // System.out.println("setting scene");
      //  System.out.println(hashMapOperator);
        this.scene = scene;
    }



}











