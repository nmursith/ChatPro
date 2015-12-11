package Controller;

import Model.BindOperator;
import Model.Bubble;
import Model.ChatMessage;
import Model.Configuration;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import javax.jms.JMSException;
import java.util.HashMap;

import static java.lang.String.valueOf;


public class ChatController{

    public Button sendButton;
    public Button CloseButton;
    public TextArea messageTextField;
    public Label Username;
    public TextArea chatBubble;
    public Button AddChatBtn;
    public ListView<String> chatUsersList;
    public TextField sampleNameInput;
    public ScrollPane messageDisplay;
    private HashMap<String, BindOperator> hashMapOperator;
    private ChatController controller =null;
    private Scene scene;
    private GridPane chatHolder;


    final ObservableList<String> listItems = FXCollections.observableArrayList();



    private  OperatorController operatorController ;
    private  HistoryController historyController;
    Boolean isSet = false;
    private String defaultOperator;


    public ChatController() throws JMSException {

        System.out.println("ChatController Started");

        hashMapOperator = new HashMap<>();
        controller =this;
        Configuration config = ConfigurationController.readConfig();
        //operatorController = new OperatorController("operator0", "chat.*",this);
        operatorController = new OperatorController(config.getOperator(), config.getTopic(),this);

        //chatBubble = new TextArea();
        chatHolder = new GridPane();
        defaultOperator = "operator1";


        //hashMapOperator.put("operator0", new BindOperator(operatorController, chatBubble));


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

                hashMapOperator.put(defaultOperator, new BindOperator(operatorController, getGridPane()));
                historyController = hashMapOperator.get(config.getSubscription()).getHistoryController();


            } catch (JMSException e) {
                e.printStackTrace();
            }
        });


    }


    public void getMyMessage() {

        sendButton.setOnMouseClicked(event -> {
            String myMessage = messageTextField.getText();
            ChatMessage myMessageMod = getObjectMessage(myMessage, operatorController.getSubscriptionName());


            try {
                if(!myMessage.trim().equals("") && !myMessage.trim().equalsIgnoreCase("exit")){
                    operatorController.sendMessage(myMessageMod, operatorController);
                    int counter = (int) operatorController.getMessageCounter();
                    //             System.out.println("value chat:  "+counter+"     ****"+operatorController);
                    Bubble bubble = new Bubble(myMessage, controller);
                    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
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
                        GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);

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
            messageTextField.clear();

        });
    }

    // Set usernames from the users list to Header label
    public void setUsername() {

        try {
            String name = valueOf(chatUsersList.getSelectionModel().getSelectedItem());


            Platform.runLater(() -> {
           //     chatBubble = hashMapOperator.get(name).getTextArea();
       //         System.out.println(chatHolder +"                    ");

                try{

                    chatHolder = hashMapOperator.get(name).getChatHolder();
                    operatorController = hashMapOperator.get(name).getOperatorController();
                    historyController = hashMapOperator.get(name).getHistoryController();
                    messageDisplay.setContent(chatHolder);
                    messageDisplay.setVvalue(messageDisplay.getVmax());
                }
                catch (NullPointerException e){

                }



                if(name.equals(defaultOperator)){
                    CloseButton.setDisable(true);
                }
                else {
                    CloseButton.setDisable(false);
                }

            });
            if(hashMapOperator.get(name).getChatHolder().isDisabled()) {
                sendButton.setDisable(true);
                messageTextField.setDisable(true);
            }
            else {
                sendButton.setDisable(false);
                messageTextField.setDisable(false);
            }

            Username.setText("User "+ name.substring(name.length()-2));
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
      //  operatorController.closeConnection();

        if(!listItems.isEmpty() ) {


            String name = chatUsersList.getItems().get(index);
//            hashMapOperator.remove(name);
 //           hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
            //listItems.remove(index);
            hashMapOperator.get(name).getChatHolder().setDisable(true);
            sendButton.setDisable(true);
            messageTextField.setDisable(true);

            //if(index==0)
              //  messageDisplay.setContent(getGridPane());
            if(index>0) {
                name = chatUsersList.getItems().get(index - 1);
                //chatBubble = hashMapOperator.get(name).getTextArea();
                chatHolder = hashMapOperator.get(name).getChatHolder();
                operatorController = hashMapOperator.get(name).getOperatorController();
                messageDisplay.setContent(chatHolder);

            }

        }


        //System.exit(0);

    }

    // Test Message Reply



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

                String name = chatUsersList.getItems().get(index);
                hashMapOperator.remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().getMessageProduceID().remove(name);
                hashMapOperator.get(defaultOperator).getOperatorController().closeConnection();
                listItems.remove(index);
                System.out.println("Removed");
            }

        }

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

    public ListView<String> getChatUsersList() {
        return chatUsersList;
    }

    public void setChatUsersList(ListView<String> chatUsersList) {
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

    public ObservableList<String> getListItems() {
        return listItems;
    }

    public HashMap<String, BindOperator> getHashMapOperator() {
        return hashMapOperator;
    }

    public void sendMyMessage(ActionEvent actionEvent) {
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











