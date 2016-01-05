package Controller;

import Model.*;
import com.csvreader.CsvReader;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.IllegalStateException;
import javax.jms.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mmursith on 11/24/2015.
 */
public class OperatorController implements MessageListener {

    private volatile Operator operator;
    private java.util.Queue<ChatMessage> chatMessagess;
    private Vector<String> messageProduceID;

    private ChatController controller;
    private MessageConsumer messageConsumer;
    private int messageCounter;
    private int IDtracker;


    private NetworkDownHandler networkHandler;
    private OperatorController operatorController;      //static or volatile or something
    private static String defaultOperator;
    private volatile boolean isOnline;
    private OfflineNetworkDownHandler offlineNetworkDownHandler;
    private volatile boolean isFirstime =true;
    private final Queue<ChatMessage> cachedMessages =  new LinkedList<>();
    private volatile Queue<Notification> pendingNotification;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);  // 100 blink at a time




    public OperatorController(String subscriptionName, String topicName, ChatController controller) throws JMSException {
        this.operator = new Operator(subscriptionName, topicName);

        this.messageProduceID = new Vector<>();
        this.chatMessagess= new LinkedList<>();
        this.networkHandler = new NetworkDownHandler();
        this.controller = controller.getInstance();
        this.operatorController = this;
        this.offlineNetworkDownHandler = new OfflineNetworkDownHandler();
        this.pendingNotification = new LinkedList<>();
        this.defaultOperator = ConfigurationController.readConfig().getOperator();//"operator1";
        System.out.println("Default operat:  "+ defaultOperator);

     //   this.notificationController = new NotificationController();
        try {
            //if
            if(subscriptionName.equalsIgnoreCase(defaultOperator)) {
                messageConsumer = operator.getSession().createDurableSubscriber(getTopic(), getSubscriptionName());
                messageConsumer.setMessageListener(this);

                Timer timer = new Timer();
                TimerTask myTask = new TimerTask() {

                    @Override
                    public void run() {
//                        System.out.println("Timer Working online :  "+ isOnline);

                            if(networkHandler.isAlive())
                                networkHandler.stopThread();

                            networkHandler = new NetworkDownHandler();
                            networkHandler.start();

                    }
                };

                    Platform.runLater(() -> timer.schedule(myTask, 500, 3000));

            }

        }
        catch (NullPointerException e){
            System.out.println("Already Answering");
        }
        messageCounter = -1;

        if(!messageProduceID.contains(defaultOperator)) {
            messageProduceID.add(defaultOperator);

            //BindOperator bindOperator = new BindOperator(this, new TextArea());
            //this.controller.getHashMapOperator().put(defaultOperator, bindOperator);
        }
    }


    public void sendMessage(ChatMessage chatMessage,OperatorController operator) throws JMSException {

        // check if a message was received
        if (chatMessage != null ) {
                try{

                    System.out.println("opreato session :  "+controller.getHashMapOperator().get(defaultOperator).getOperatorController().getSesssion());
                    TextMessage response =   operator.getSesssion().createTextMessage(); //controller.getHashMapOperator().get(controller.getDefaultOperator()).getOperatorController().getSesssion().createTextMessage();
                    //System.out.println("Response: "+ response);

                    String myMessage = chatMessage.getTextMessage();
                    //System.out.println("message: "+ myMessage);
                    response.setText(myMessage.trim().equalsIgnoreCase("exit") ? "DIRROUTETOBOT":myMessage);
                    //System.out.println("offline:   "+myMessage);
                    String random = Constant.correalationID;
                    String JMSmessageID = Constant.JMSmessageID;
                    response.setJMSCorrelationID(random);
                    response.setJMSMessageID(JMSmessageID);
                    //System.out.println("Getting producer");
                    operator.getMessageProducer().send(response);
                    //System.out.println("fine: "+ operator.getMessageProducer());


                }

                catch (IllegalStateException e){
                //e.printStackTrace();
//                  if(!isOnline){
                        //isOnline = false;
                      System.out.println("network handling");
                      if(offlineNetworkDownHandler.isAlive()) {

                          offlineNetworkDownHandler.stopThread();
                      }

                      cachedMessages.add(chatMessage);
                      //      System.out.println("Message Added:  "+ chatMessage.getTextMessage() +"   "+cachedMessages.size());
                      offlineNetworkDownHandler = new OfflineNetworkDownHandler();
                      offlineNetworkDownHandler.start();
//                  }

                }
            catch (NullPointerException e){
                e.printStackTrace();
                System.out.println("Error caused  " +e.getClass() );

            }


        }

    }

     @Override
    public void onMessage(Message message) {
    String producerID = null;
   //     System.out.println("Recieving......:      ");
        try {


            if (message instanceof TextMessage) {
     //           System.out.println("Object: "+message.toString());
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();

         //       System.out.println("From Clisent: "+ txtMsg.getText());

                ChatMessage chatMessage =  new ChatMessage();

                chatMessage.setProducerID("");
                chatMessage.setMessage(message);
                chatMessage.setTextMessage(messageText);
                this.chatMessagess.add(chatMessage);


            }
            else {

                //System.out.println(text.getText());
                ActiveMQBytesMessage activeMQBytesMessage = (ActiveMQBytesMessage) message;

                String destination = ((ActiveMQBytesMessage) message).getDestination().getPhysicalName();
                destination =destination.substring(destination.indexOf('.')+1);

//                System.out.println("destination: "+ destination);
                producerID = destination;
                //producerID = activeMQBytesMessage.getProducerId().getConnectionId();

//                if(producerID!=null){
//                    producerID = producerID.replace("-","");
//                    producerID = producerID.replace(":","");
//                }

                ByteSequence byteSequence = activeMQBytesMessage.getContent();
                byte [] bytes = byteSequence.getData();
                String messageText = new String(bytes, StandardCharsets.UTF_8);
//                System.out.println(messageText);

                ChatMessage chatMessage =  new ChatMessage();
                chatMessage.setProducerID(producerID);

                chatMessage.setMessage(message);
                chatMessage.setTextMessage(messageText);
//                System.out.println("From Byte Client: "+ chatMessage.getTextMessage());
                this.chatMessagess.add(chatMessage);
//                System.out.println(chatMessagess.isEmpty());
            }


            if(!messageProduceID.contains(producerID) && producerID!=null ){
                System.out.println("Adding:  "+  producerID);
                messageProduceID.add(producerID);


                GridPane chatHolder = controller.getGridPane();
                OperatorController operatorController = new OperatorController(producerID, "chat."+producerID, controller);
//                bindOperator.getChatHolder().addRow(0, SeperatorLine.getSeperator());//oldhistory);

//                if(!producerID.equals(defaultOperator))
//                    operatorController.getMessageConsumer().setMessageListener(null);

                BindOperator bindOperator = new BindOperator(operatorController, chatHolder);


                controller.getHashMapOperator().put(producerID, bindOperator);

                //Thread.sleep(20);

                if(controller!=null){
                    int count  = loadHistory(controller.getHashMapOperator().get(producerID));
                    operatorController.setMessageCounter(count);        //starting
                    operatorController.setIDtracker(1);
                }


               }
            else {
               // System.out.println(controller.getHashMapOperator().get(producerID).getChatHolder().isDisabled());
                try{
                    if(controller.getHashMapOperator().get(producerID).getChatHolder().isDisabled()) {
                        controller.getHashMapOperator().get(producerID).getChatHolder().setDisable(false);
                        controller.getSendButton().setDisable(false);
                        controller.getMessageTextField().setDisable(false);
                        controller.getSendButton().setDisable(false);
                        controller.getListItems().get(controller.getMessageProducerID().indexOf(producerID)).setDisable(false);
                    }

                }
                catch (NullPointerException e){
                        //System.out.println("First startup fails");
                }
            }


            /********/
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            //Background work
                            final CountDownLatch latch = new CountDownLatch(1);
                            Platform.runLater(() -> {
                                if(controller!=null && !chatMessagess.isEmpty()) {
                                    System.out.println("Routing    "+ chatMessagess.size());
                                    try {
                                        routeChat();
                                    } catch (JMSException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            latch.await();
                            //Keep with the background work
                            return null;
                        }
                    };
                }
            };
            service.start();

/*            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    if(controller!=null)
                        routeChat();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
            latch.await();*/

/*            Task<Void> task = new Task<Void>() {
                @Override protected Void call() throws Exception {
                    final CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        try {
                            if(controller!=null)
                                routeChat();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    });
                    latch.await();
                    return null;
                }
            };

            Platform.runLater(task);*/


        /***********/



        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void routeChat() throws JMSException {

        String reply;
        Queue<ChatMessage> durablechatMessage;
        Vector<String> producerID = getMessageProduceID();
        ChatMessage chatMessage;
        String pID;

  //      System.out.println("Routing...");


        if(controller.getListItems().size()< producerID.size()){
 //           System.out.println("Condition ");
            for(int index=0; index < producerID.size(); index++) {
//                boolean isoOutOfIndex = false;
                String tempName = producerID.get(index);
           //     System.out.println("updating  "+tempName);
//                try {
//             //       System.out.println("checking:  " + controller.getListItems().get(index).getUser().getSubscriptionName());
//                    isoOutOfIndex =false;
//                }
//                catch (Exception e) {
//                        isoOutOfIndex = true;
//
//                    //  e.printStackTrace();
//                }

                    if (!controller.getMessageProducerID().contains(tempName) && !tempName.equals(null) && !tempName.equals(defaultOperator)) {
                        controller.getMessageProducerID().add(tempName);
                        String username=Constant.usernames[messageProduceID.size()-1];
                        User user = new User();
                        user.setuserId(tempName);
                        user.setUserName(username);
                        user.setSubscriptionName(tempName);
                        user.setTopicName("chat." + tempName);

                        controller.getListItems().add(new UserItem(user,controller));
                  //      controller.getChatUsersList().setItems(controller.getListItems());
                //        System.out.println("updated:   "+ defaultOperator +"        "+ tempName);

                    }



            }
        }

        durablechatMessage = getChatMessagess();


            if(!durablechatMessage.isEmpty()) {
                while (!durablechatMessage.isEmpty()) {
                    //                  System.out.println("internal");
                    try {


                        chatMessage =durablechatMessage.remove();
                        reply = chatMessage.getTextMessage();
                        String correID = chatMessage.getMessage().getJMSCorrelationID();
                        pID = chatMessage.getProducerID();
//                        if (correID==null)
//                            correID = "";
//                       System.out.println(correID);
                        if( correID==null || !correID.equals(Constant.correalationID)  ) {  //!correID.equals(Constant.correalationID)

              //              Bubble bubble = new Bubble(reply, controller);
                            int index = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                            String username = controller.getListItems().get(index).getUser().getUserName();

                         //   GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT );

                            //System.out.println("User:  " + reply);
//                            BindOperator bindOperator =  controller.getHashMapOperator().get("operator0");
//                            //  bindOperator.getTextArea().appendText("User:  " + reply+"\n\n");
//      //                      System.out.println(bindOperator);
//                            int counter = (int)bindOperator.getOperatorController().getMessageCounter();
//                         //   System.out.println("value operator0:  "+counter+"     ****"+this);
//
//                            bindOperator.getChatHolder().addRow(counter, bubble.getFromBubble());
//                            bindOperator.getHistoryController().writehistroty(counter, "user",reply);       //writing to csv

    /*******/               //UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
                            BindOperator bindOperator =  controller.getHashMapOperator().get(chatMessage.getProducerID());
                            int counter = (int)bindOperator.getOperatorController().getMessageCounter();
                            int ID = bindOperator.getOperatorController().getIDtracker();
//                            bindOperator.getTextArea().appendText("User:  " + reply+"\n\n");



/****************/
                            String JMSreplyTo = chatMessage.getMessage().getJMSMessageID();
                            if(JMSreplyTo!=null) {
                                if (JMSreplyTo.equalsIgnoreCase(Constant.JMSmessageID)) {
                                    OperatorBubble bubble = new OperatorBubble(defaultOperator, chatMessage.getTextMessage(), chatMessage.getTime());
                                    //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                                    bindOperator.getChatHolder().addRow(ID, bubble.getRoot());


                                } else {
                                    UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
                                    //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                                    bindOperator.getChatHolder().addRow(ID, bubble.getRoot());
                                }
                            }
                            else {
                                UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
                                bindOperator.getChatHolder().addRow(ID, bubble.getRoot());
                            }
/******************/


                            int cID = controller.getMessageProducerID().indexOf(pID);//current ID;
                            //System.out.println("cid:   "+ cID);

                            //executor.execute();

                                int sID = controller.getChatUsersList().getSelectionModel().getSelectedIndex(); // selected ID

                            //    System.out.println("selected   "+cID);
                                if(sID!=cID){
                                 //   System.out.println("should work");
                                    Platform.runLater(() -> controller.getChatUsersList().getItems().get(cID).startBlink());

                                }



                            Platform.runLater(() -> {
                             //   System.out.println("firsttime:   " + isFirstime + "    " + controller.getListItems().isEmpty());
                                if (!controller.getListItems().isEmpty() && isFirstime) {
                                    controller.getMessageTextField().setDisable(false);
                                    System.out.println("first");
                                    isFirstime = false;

                                    UserItem item = controller.getListItems().get(0);
                                    controller.getChatUsersList().getSelectionModel().select(0);
                                    controller.setUsername(item);

                                }

                                //ScrollPane messageHolder = new ScrollPane();
                                //messageHolder.setContent((bindOperator.getChatHolder()));
                                controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax());
                                //messageDisplay.setVvalue(messageDisplay.getVmax());
                                //    controller.messageDisplay.setVvalue(messageHolder.getVmax());
                            });
                            bindOperator.getHistoryController().writehistory(counter, username,chatMessage);       //swriting to csv
                            index = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                            username = controller.getListItems().get(index).getUser().getUserName();

//                            final String uName = username;
//                            final String rep = reply;


                                if(!controller.getStage().isFocused() || controller.getStage().isIconified()) {

                           //         pendingNotification.add(new Notification(reply, username, index));
                                    //System.out.println("pending noti  "+ pendingNotification.size());



                           //         notificationHandler.start();

                                    NotificationController.getNotification(reply, username,controller,index);

                                }



       //                     System.out.println("Handling notifications   "+ pendingNotification.size());


/*

                            Task<Void> task = new Task<Void>() {
                                  @Override protected Void call() throws Exception {
                                  Platform.runLater(() -> {
                                            Notification notification;
                                            while (!pendingNotification.isEmpty()) {
                                                notification = pendingNotification.remove();

                                                if(!controller.getStage().isFocused() || controller.getStage().isIconified()) {
                                                    NotificationController.getNotification(notification.getReply(), notification.getUserName(),controller,notification.getIndex());
                                                }
                                                try {
                                                    Thread.sleep(20);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                             return null;
                                }
                            };

                            Platform.runLater(task);
*/





                            // System.out.println(chatMessage.getMessage());
                            //     System.out.print("Operator:   ");
                            //  send = in.nextLine();


                        }

                    }
                    catch (NullPointerException e){
                        System.out.println("Null");
                      //  e.printStackTrace();
                        //   break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

    }

    private int loadHistory(BindOperator bindOperator) throws IOException {

        int count = 0;
        SeperatorLine seperatorLine = new SeperatorLine(bindOperator);
        bindOperator.getChatHolder().getChildren().clear();
        bindOperator.getChatHolder().addRow(0,seperatorLine.getSeperator());
        CsvReader messages = bindOperator.getHistoryController().readHistory();
        if(messages != null){


            try {
                messages.readHeaders();

                GridPane oldhistory = controller.getGridPane();

                while (messages.readRecord()) {
                    String ID = messages.get("id");
                    String from = messages.get("from");
                    String message = messages.get("message");
                    String time = messages.get("time");
                    int id = Integer.parseInt(ID);
////working here


                    if(from.equalsIgnoreCase(Constant.operatorID)) {
                        OperatorBubble bubble = new OperatorBubble(defaultOperator, message, time);
                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                        oldhistory.addRow(id, bubble.getRoot());
                       // bindOperator.getChatHolder().addRow(id, bubble.getRoot());


                    }
                    else {
                        UserBubble bubble = new UserBubble(from, message, time);
                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                        oldhistory.addRow(id, bubble.getRoot());
                    //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
                    }
//                    for (Node node : bindOperator.getChatHolder().getChildren()) {
//                        System.out.print("index:    "+GridPane.getRowIndex(node)+"     ");
//                        GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                        System.out.println(GridPane.getRowIndex(node));
//
//                    }
           //         bindOperator.getChatHolder().addRow(0, oldhistory);
       //             Node node = bindOperator.getChatHolder().getChildren().get(0);


                    bindOperator.setOldchatHolder(oldhistory);


/*                    int numRows = 1;
                    int rowIndex = 0;
                    for (Node node :  bindOperator.getChatHolder().getChildren()) {
                        int currentRow = GridPane.getRowIndex(node);
                        if (currentRow >= rowIndex) {
                            GridPane.setRowIndex(node, currentRow+1);
                            if (currentRow+1 > numRows) {
                                numRows = currentRow + 1;
                            }
                        }
                    }*/

                    //bindOperator.getChatHolder().addRow(0, oldhistory);
                    count++;

                }

                messages.close();

            }

            catch(IOException e){
                count =0;
                //e.printStackTrace();
            }

        }
        else{

        }

        return count;
    }

    public int getMessageCounter() {
        operatorController.messageCounter = operatorController.messageCounter+1;
        return operatorController.messageCounter;
    }



    private GridPane getGridPane() {
//        GridPane gridPane = new GridPane();
//        //gridPane.setMaxSize(431, 413);
//        gridPane.setPrefWidth(431);
//        gridPane.setMinWidth(431);
//        gridPane.setMaxWidth(431);
//        gridPane.setPrefHeight(413);
//        gridPane.setVgap(7);
//        ColumnConstraints c1 = new ColumnConstraints();
//        c1.setPercentWidth(100);
//        gridPane.getColumnConstraints().add(c1);
        return controller.getGridPane();
    }

    protected Queue<ChatMessage> getChatMessagess() {
        return operatorController.chatMessagess;
    }

    private Topic getTopic() {
        return operatorController.operator.getTopic();
    }

    public Vector<String> getMessageProduceID() {
        return operatorController.messageProduceID;
    }

    public void setMessageProduceID(Vector<String> messageProduceID) {
        this.messageProduceID = messageProduceID;
    }

    public void closeConnection() throws JMSException {
        operatorController.operator.closeConnection();
    }

    public void setFirstime(boolean firstime) {
        isFirstime = firstime;
    }

    public void setChatMessagess(Queue<ChatMessage> chatMessagess) {
        this.chatMessagess = chatMessagess;
    }

    public boolean isFirstime() {
        return isFirstime;
    }

    public Queue<ChatMessage> getCachedMessages() {
        return cachedMessages;
    }

    private void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }

    public String getSubscriptionName() {
        return operator.getSubscriptionName();
    }

    protected Session getSesssion() {
        return operator.getSession();
    }

    public static String getDefaultOperator() {
        return defaultOperator;
    }

    private MessageProducer getMessageProducer() {
        return operator.getMessageProducer();
    }

    private MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }
    protected void setMessageCounter(int messageCounter) {
        this.messageCounter = messageCounter;
    }

    public int getIDtracker() {
        operatorController.IDtracker = operatorController.IDtracker+1;
        return operatorController.IDtracker;

    }

    public void setIDtracker(int IDtracker) {
        this.IDtracker = IDtracker;
    }

    private class OfflineNetworkDownHandler extends Thread{


        Thread thread = this;
//        String ID = Constant.getRandomString();

        public void run() {
            super.run();
        //thread = Thread.currentThread();
        System.out.println("isOnline:    "+isOnline);

        synchronized (cachedMessages) {
//            while (!isOnline) {
//                try {
//                    Operator operator = new Operator(ID, ID);
//                    boolean isConnected = operator.isConnected();
//
//                    System.out.println("inside:  " + isOnline);
//                    if (isConnected) {
//                        isOnline = true;
//                        controller.statusImageView.setImage(image_online); //==========================
//                        System.out.println("Re-connected");
//                    }
//                    else {
//                        controller.statusImageView.setImage(image_offline); //========================
//                        isOnline = false;
//                    }
//
//                }  catch (Exception e) {
//                    isOnline = false;
//
//                    System.out.println("Not connected");
//                }
//            }


            if (isOnline) {
                try {
                    for (int index = 0; index < controller.getListItems().size(); index++) {

                        UserItem useritem = controller.getListItems().get(index);
                        String producerID =useritem.getUser().getSubscriptionName();
                        System.out.println("producerID:     "+producerID);
                        OperatorController operatorController = new OperatorController(producerID, "chat." + producerID, controller);
                        int count =   controller.getHashMapOperator().get(producerID).getOperatorController().getMessageCounter() -1;

                        operatorController.setMessageCounter(count);
//                        ChatMessage chat = new ChatMessage();
//                        chat.setTextMessage("sdfsdfssdgs");
                      //  operatorController.sendMessage(chat, operatorController);
                        controller.getHashMapOperator().get(producerID).setOperatorController(operatorController);
                    }

                    OperatorController operatorController = new OperatorController(OperatorController.defaultOperator, "chat.*", controller);
                    int count =   controller.getHashMapOperator().get(defaultOperator).getOperatorController().getMessageCounter();
                    operatorController.setMessageCounter(count);
                    controller.getHashMapOperator().get(OperatorController.defaultOperator).setOperatorController(operatorController);
                    setOperatorController(controller.getHashMapOperator().get(OperatorController.defaultOperator).getOperatorController());

                    //int selected = controller.getChatUsersList().getSelectionModel().getSelectedIndex();
                    UserItem useritem = controller.getChatUsersList().getSelectionModel().getSelectedItem();
                   // OperatorController currentOperator = controller.getHashMapOperator().get(getMessageProduceID().get(selected)).getOperatorController();
                    controller.setUsername(useritem);


                    System.out.println("contained:   " + cachedMessages.size());

                    while (!cachedMessages.isEmpty()) {

                        ChatMessage chatMessage = cachedMessages.remove();

                        BindOperator bindOperator = controller.getHashMapOperator().get(chatMessage.getProducerID());
                        controller.sendMessage(chatMessage, bindOperator.getOperatorController());
                    }
                //    stop();
                    stopThread();

                } catch (JMSException e) {
                    System.out.println("JMS problem");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }



        }

        public  void stopThread(){

            thread = null;

        }

    }


    private class NetworkDownHandler extends Thread{
        Image image_offline = new Image(getClass().getResourceAsStream("offline.png")); //===========================
        Image image_online = new Image(getClass().getResourceAsStream("online.png"));   //===========================

        Thread thread = this;
        public void run() {
            thread = Thread.currentThread();

            String ID = Constant.operatorID;//Constant.getRandomString();

                try {

                    Operator operator = new Operator(ID, ID);
                    boolean isConnected = operator.isConnected();

                    //         System.out.println("inside:  " + isOnline);
                    if (isConnected) {
                        controller.statusImageView.setImage(image_online); //==========================
                        isOnline = true;
                        operator.closeConnection();

                    }
                    else {
                        controller.statusImageView.setImage(image_offline);//===========================
                        isOnline = false;
                    }

                } catch (IllegalStateException e) {
                    isOnline = false;
                    try {
                        sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                } catch (JMSException e) {
                    isOnline = false;
                    try {

                        sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }


//            stopThread();
        }

        public  void stopThread(){
            Thread t = thread;
            thread = null;
            t.interrupt();
        }
    }

}
