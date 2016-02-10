package Controller;

import Model.*;
import com.csvreader.CsvReader;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.json.simple.parser.ParseException;

import javax.jms.IllegalStateException;
import javax.jms.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mmursith on 11/24/2015.
 */
public class OperatorController implements MessageListener {

    private volatile Operator operator;
    private BlockingQueue<ChatMessage> chatMessagess;
    private Vector<String> messageProduceID;

    private ChatController controller;
    private MessageConsumer messageConsumer;
    private int messageCounter;
    private volatile int IDtracker;

    private NetworkDownHandler networkHandler;
    private OperatorController operatorController;      //static or volatile or something
    private static String defaultOperator;

    private MessageDistributionHandler messageDistributionHandler;
    private OfflineNetworkDownHandler offlineNetworkDownHandler;
    private volatile boolean isFirstime =true;
    private final Queue<ChatMessage> cachedMessages =  new LinkedList<>();
    private volatile Queue<Notification> pendingNotification;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);  // 100 blink at a time
    private Timer timer;
    private volatile boolean isOnline;
    private boolean isSessionCreated;
    private String subscriptionName;
    private boolean isClosedAlready;
    private static ChatMessage chatMessage;
    private  JSONFormatController jsonFormatController;
    private Image image_offline = new Image(getClass().getResourceAsStream("offline.png")); //===========================
    private Image image_online = new Image(getClass().getResourceAsStream("online.png"));   //===========================
    private boolean isReceived;


    public OperatorController(String subscriptionName, String topicName, ChatController controller) throws JMSException {
        this.operator = new Operator(subscriptionName, topicName);
        this.subscriptionName = subscriptionName;


        this.chatMessagess= new LinkedBlockingQueue<>();
        this.networkHandler = new NetworkDownHandler();
        this.controller = controller.getInstance();
        this.operatorController = this;
        this.offlineNetworkDownHandler = new OfflineNetworkDownHandler();
        this.messageDistributionHandler = new MessageDistributionHandler();
        this.messageProduceID = this.controller.messageProducerID;//new Vector<>();
        this.pendingNotification = new LinkedList<>();
        this.defaultOperator = Constant.configuration.getOperator();//"operator1";

        this.jsonFormatController = new JSONFormatController();
        this.isSessionCreated = false;
        this.isClosedAlready = false;
        this.isReceived = false;
//      this.notificationController = new NotificationController();
        this.messageCounter = -1;
        this.IDtracker = -1;

    }

    protected void startDefaultOperatorAction(){
        try {
            //if
/**** single producerID**/
/*            if(!messageProduceID.contains(defaultOperator)) {
                messageProduceID.add(defaultOperator);
              //    controller.getMessageProducerID().add(defaultOperator);
                //BindOperator bindOperator = new BindOperator(this, new TextArea());
                //this.controller.getHashMapOperator().put(defaultOperator, bindOperator);
            }*/

            //controller.setOperatorProducerID(messageProduceID);
            if(subscriptionName.equalsIgnoreCase(defaultOperator) &&operator.getSession()!=null) {
                messageConsumer = operator.getSession().createDurableSubscriber(getTopic(), getSubscriptionName());//Constant.operatorID);
                messageConsumer.setMessageListener(this);
                System.out.println("Default operat:  "+ defaultOperator);

                try{

                    if(networkHandler.isAlive())
                        networkHandler.stopThread();

                    networkHandler = new NetworkDownHandler();
                    networkHandler.start();
                }
                catch (Exception e){
                    e.printStackTrace();
                    //System.out.println("Sleepdetected");
                }

                try{

                    if(messageDistributionHandler.isAlive())
                        messageDistributionHandler.stopThread();

                    messageDistributionHandler = new MessageDistributionHandler();
                    messageDistributionHandler.start();
                }
                catch (Exception e){
                    e.printStackTrace();
                    //System.out.println("Sleepdetected");
                }




                timer = new Timer();
                TimerTask offlineModeTask = new TimerTask() {

                    @Override
                    public void run() {

                       //                        System.out.println("Timer Working online :  "+ isOnline);

                        if(operatorController.getSesssion()==null){
                             System.out.println("Null session:  ");

                                operatorController.setListener();
                                System.out.println("Created session:  "+ operatorController.getSesssion());


                        }

                        try {
                            //if(operatorController.getSesssion()!=null)
                                operatorController.getSesssion().createTextMessage();
                        }
                        catch ( JMSException e){


                                System.out.println("Sleep Mode handling");
                            controller.statusImageView.setImage(image_offline);
                                if(offlineNetworkDownHandler.isAlive()) {

                                    offlineNetworkDownHandler.stopThread();
                                }

                            operatorController.setListener();
                            isOnline = operatorController.operator.isConnected();
                            //      System.out.println("Message Added:  "+ chatMessage.getTextMessage() +"   "+cachedMessages.size());
                                offlineNetworkDownHandler = new OfflineNetworkDownHandler();
                                offlineNetworkDownHandler.start();

                            //e.printStackTrace();
                        }

                        catch (NullPointerException e){
                            e.printStackTrace();
                            System.out.println("operator null");
                           // timer.cancel();

                        }
                    }
                };

//                TimerTask routeMessageTask  = new TimerTask() {
//                    @Override
//                    public void run() {
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    routeChat();
//                                } catch (JMSException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//
//
//                    }
//                };

                Platform.runLater(() -> timer.schedule(offlineModeTask, 500, 3000));
                //Platform.runLater(() -> timer2.schedule(routeMessageTask, 100, 1000));



            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Already Answering");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void setListener(){
        try {
            operatorController.createSession();
            messageConsumer = operator.getSession().createDurableSubscriber(getTopic(), Constant.operatorID);//getSubscriptionName());
            messageConsumer.setMessageListener(this);
        } catch (JMSException e) {
         //   e.printStackTrace();
        }

    }
    public void sendMessage(ChatMessage chatMessage,OperatorController operator) throws JMSException {

        // check if a message was received
        if (chatMessage != null ) {
            if(!operator.isSessionCreated()){
                operator.createSession();
                System.out.println("operator session:  " + operator.getSesssion());
            }


            if(operator.getSesssion()==null){
                operator.setSubscriptionName(operator.getSubscriptionName()+Constant.operatorID);
                operator.createSession();
                System.out.println("null and operator session:  " + operator.getSesssion());

            }

            try{

                operator.getSesssion().createTextMessage();
            }
            catch(IllegalStateException e){
                operator.createSession();
                isOnline = operator.operator.isConnected();
            }
            catch (NullPointerException e ){
                e.printStackTrace();
            }


                try{

                   // System.out.println("opreato session :  "+controller.getHashMapOperator().get(defaultOperator).getOperatorController().getSesssion());
                    System.out.println("Message: "+ chatMessage.getTextMessage());
                    TextMessage response =   operator.getSesssion().createTextMessage(); //controller.getHashMapOperator().get(controller.getDefaultOperator()).getOperatorController().getSesssion().createTextMessage();
                    String myMessage = chatMessage.getTextMessage();
                    //System.out.println("message: "+ myMessage);
                    myMessage = myMessage.trim().equalsIgnoreCase("exit") ? "DIRROUTETOBOT":myMessage;
                    myMessage = jsonFormatController.createJSONmessage(OperatorController.defaultOperator,myMessage);
                    response.setText(myMessage);
                    //System.out.println("offline:   "+myMessage);
                    String random = Constant.correalationID;
         //           String JMSmessageID = Constant.JMSmessageID;
                    response.setJMSCorrelationID(random);

                    //response.acknowledge();

               //     response.setJMSMessageID(JMSmessageID);
                    //System.out.println("Getting producer");
                    operator.getMessageProducer().send(response);
                    //System.out.println("fine: "+ operator.getMessageProducer());

                }

                catch (IllegalStateException e){
                //e.printStackTrace();
//                  if(!isOnline){
                        //isOnline = false;
                      System.out.println("network handling while sending");
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
             //   e.printStackTrace();
                System.out.println("Error caused  " +e.getClass() );

            }


        }

    }

     @Override
    public void onMessage(Message message) {
        String producerID = null;
        String correlationID =null;


        try {


            if (message instanceof TextMessage) {
                //System.out.println("Object: "+message.toString());
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();

                String [] jsoNmessage = jsonFormatController.readJSONmessage(messageText);
                messageText = jsoNmessage[0];
                String owner = jsoNmessage[1];

                if(messageText.contains(Constant.DO_NOT_TRAIN_TAG)){
                   messageText= messageText.replace(Constant.DO_NOT_TRAIN_TAG,"");

                }
                System.out.println("Recieving......:      "+messageText);
                String destination = message.getJMSDestination().toString();
                destination = destination.substring(destination.indexOf('.') + 1);
                //                System.out.println("destination: "+ destination);
                producerID = destination;
                correlationID = message.getJMSCorrelationID();
                chatMessage =  new ChatMessage();
                chatMessage.setProducerID(producerID);
                chatMessage.setMessage(message);


                chatMessage.setTextMessage(messageText);
                chatMessage.setOwner(owner);
                this.chatMessagess.add(chatMessage);


            }
            else if (message instanceof ActiveMQBytesMessage){

                //System.out.println(text.getText());
                     ActiveMQBytesMessage activeMQBytesMessage = (ActiveMQBytesMessage) message;

                    String destination = ((ActiveMQBytesMessage) message).getDestination().getPhysicalName();

                    destination = destination.substring(destination.indexOf('.') + 1);

                    //                System.out.println("destination: "+ destination);
                    producerID = destination;
                    //producerID = activeMQBytesMessage.getProducerId().getConnectionId();

                    //                if(producerID!=null){
                    //                    producerID = producerID.replace("-","");
                    //                    producerID = producerID.replace(":","");
                    //                }

                    ByteSequence byteSequence = activeMQBytesMessage.getContent();
                    byte[] bytes = byteSequence.getData();
                    String messageText = new String(bytes, StandardCharsets.UTF_8);
                    String [] jsoNmessage = jsonFormatController.readJSONmessage(messageText);

                    messageText = jsoNmessage[0];
                    String owner = jsoNmessage[1];
                    correlationID = message.getJMSCorrelationID();
         //       System.out.println(messageText);

                if(messageText.contains(Constant.DO_NOT_TRAIN_TAG)){
                    messageText= messageText.replace(Constant.DO_NOT_TRAIN_TAG,"");
                }

                System.out.println("Recieving... byte...:      "+messageText);

                    chatMessage = new ChatMessage();
                    chatMessage.setProducerID(producerID);

                    chatMessage.setMessage(message);



                    chatMessage.setTextMessage(messageText);
                    chatMessage.setOwner(owner);

                //chatMessage.setTextMessage(messageText);
                    //                System.out.println("From Byte Client: "+ chatMessage.getTextMessage());

                    if(!messageText.contains(Constant.BOT_TAG) && !messageText.contains(Constant.HISTORY_TAG))
                    this.chatMessagess.add(chatMessage);

//                System.out.println(chatMessagess.isEmpty());
            }

            System.out.println(correlationID +"         PRODUCERID :  "+  producerID);

            if(!controller.getMessageProducerID().contains(producerID) && producerID!=null && !producerID.equalsIgnoreCase("*") && !producerID.equals(defaultOperator) && correlationID==null){
                System.out.println("Adding:  "+  producerID);
                controller.getMessageProducerID().add(producerID);


                GridPane chatHolder = controller.getGridPane();
                OperatorController operatorController = new OperatorController(producerID, Constant.topicPrefix+producerID, controller);
//                bindOperator.getChatHolder().addRow(0, SeperatorLine.getSeperator());//oldhistory);

//                if(!producerID.equals(defaultOperator))
//                    operatorController.getMessageConsumer().setMessageListener(null);

                BindOperator bindOperator = new BindOperator(operatorController, chatHolder);
                if(chatMessage.getOwner()!=null || chatMessage.getOwner()!="") {
                    bindOperator.setClientName(chatMessage.getOwner());
                }
                else {
                    bindOperator.setClientName(null);
                }

                System.out.println("Setting client Name from Mesasge: "+ chatMessage.getOwner());


                if(!controller.getHashMapOperator().containsKey(producerID))
                    controller.getHashMapOperator().put(producerID, bindOperator);

                //Thread.sleep(20);




                    if(!chatMessagess.contains(chatMessage)) {
                        String  username = bindOperator.getHistoryController().writeHistory(chatMessage.getTextMessage(), bindOperator, false);

                        if(bindOperator!=null && bindOperator.getClientName()==null)
                            bindOperator.setClientName(chatMessage.getOwner());

                        System.out.println("client Name set:  "+ username);
                        chatMessage =null;
                        //count = count +count2;
                        //operatorController.setMessageCounter(count);
                  }

                    int count  = loadHistory(controller.getHashMapOperator().get(producerID));
                    operatorController.setMessageCounter(count);        //starting

                    //operatorController.setIDtracker(0);


                    String tempName = producerID;


//                    System.out.println("updating:  "+tempName);
            //        controller.getMessageProducerID().add(tempName);

                    bindOperator = controller.getHashMapOperator().get(tempName);
                    String username =null;

                    if(bindOperator!=null)
                        username=bindOperator.getClientName();

                    if(username == null || username=="")
                        username=Constant.Annonymus;//+tempName.substring(tempName.length()-2);//Constant.usernames[messageProduceID.size()-1];

                    User user = new User();
                    user.setuserId(tempName);
                    user.setUserName(username);
                    user.setSubscriptionName(tempName);
                    user.setTopicName(Constant.topicPrefix+ tempName);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.getListItems().add(new UserItem(user,controller));

                            System.out.println("USERITEM IS ADDED       ");// +controller.getListItems().get(controller.getListItems().size()-1) );

                        }
                    });

                    //      controller.getChatUsersList().setItems(controller.getListItems());
                    //        System.out.println("updated:   "+ defaultOperator +"        "+ tempName);


                     setENDisableUI(producerID);


               }
            else {

                setENDisableUI(producerID);
               // System.out.println(controller.getHashMapOperator().get(producerID).getChatHolder().isDisabled());

            }





            if(chatMessage!=null && !chatMessagess.contains(chatMessage)) {
                //Image botImage= new Image(getClass().getResourceAsStream("robotic.png"));

                BindOperator bindOperator = controller.getHashMapOperator().get(chatMessage.getProducerID());

                bindOperator.setClientName(chatMessage.getOwner());

                try {
                    final String username = bindOperator.getHistoryController().writeHistory(chatMessage.getTextMessage(), bindOperator, true);

                    if (username != null) {
                        if(bindOperator.getClientName()!=null)
                        bindOperator.setClientName(username);
                        int index = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                        ListView<UserItem> userItemListView =  controller.getChatUsersList();

                        if (index >= 0 && !userItemListView.getItems().isEmpty() && index <userItemListView.getItems().size()) {
                            UserItem userItem = userItemListView.getItems().get(index);
                            userItem.startBlink();
                            userItem.getUser().setUserName(username);
                            System.out.println(userItemListView.getSelectionModel().isSelected(index));


                            if (userItemListView.getSelectionModel().isSelected(index)) {
                                System.out.println("client Name set:  " + username);
                                Platform.runLater(() -> {
                                    userItem.getThumbUserName().setText(username);
                                    controller.Username.setText(username);

                                });

                            }


                        }

                        int trakcer = bindOperator.getOperatorController().getIDtracker();//getTracker();
                        SeperatorLine seperatorLine = new SeperatorLine(bindOperator, trakcer);            // uncomment
                        Platform.runLater(() -> {
                            //bindOperator.getChatHolder().addRow(trakcer, seperatorLine.getSeperator()); // uncomment

                            bindOperator.getChatHolder().add(seperatorLine.getSeperator(), 0, trakcer); // uncomment
                        });
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                catch(Exception e){
                        e.printStackTrace();
                }

            }




        } catch (Exception e){
            e.printStackTrace();
        }
         isReceived = true;
    }

    protected void setENDisableUI(String producerID){
        boolean isdisable = false;
        try{
            if(controller.getHashMapOperator().get(producerID).getChatHolder().isDisabled()) {
                controller.getHashMapOperator().get(producerID).getChatHolder().setDisable(isdisable);
                controller.getSendButton().setDisable(isdisable);
                controller.getMessageTextField().setDisable(isdisable);
                controller.getDoTrain().setDisable(isdisable);
                controller.getSendButton().setDisable(isdisable);

                controller.getListItems().get(controller.getMessageProducerID().indexOf(producerID)).changePicture(isdisable);
            }

        }
        catch (NullPointerException e){
     //       e.printStackTrace();
        }
        catch (Exception e){
            //       e.printStackTrace();
        }

    }


    private  void routeMessagetoThread(){
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        //final CountDownLatch latch = new CountDownLatch(1);
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
               //         latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();


    }


    private void routeChat() throws JMSException {

        String reply;
        Queue<ChatMessage> durablechatMessage;

        ChatMessage chatMessage;
        String pID;

  //      System.out.println("Routing...");


/*
        if(controller.getListItems().size()< producerID.size()){
 //           System.out.println("Condition ");
            for(int index=1; index < producerID.size(); index++) {
//                boolean isoOutOfIndex = false;
                String tempName = producerID.get(index);

//                try {
//             //       System.out.println("checking:  " + controller.getListItems().get(index).getUser().getSubscriptionName());
//                    isoOutOfIndex =false;
//                }
//                catch (Exception e) {
//                        isoOutOfIndex = true;
//
//                    //  e.printStackTrace();
//                }





            }
        }
*/

        durablechatMessage = getChatMessagess();


            if(!durablechatMessage.isEmpty()) {
                while (!durablechatMessage.isEmpty()) {
                    //                  System.out.println("internal");
                    try {

                        chatMessage =durablechatMessage.remove();
                        reply = chatMessage.getTextMessage();



                        String correID = chatMessage.getMessage().getJMSCorrelationID();

                        pID = chatMessage.getProducerID();
                    //    System.out.println("chatmessage ID: "+chatMessage.getProducerID());

                        if(reply.equalsIgnoreCase(Constant.exitMessage))
                            reply = Constant.exitBubbleMessage;
                        else if(reply.equalsIgnoreCase(Constant.exitUserMessage))
                            reply = Constant.exitUserBubbleMessage;

//                        if (correID==null)
//                            correID = "";
//                       System.out.println(correID);
                        if( correID==null ||  !correID.equals(Constant.correalationID) ){  //|| !correID.equals(Constant.correalationID)

                            BindOperator bindOperator =  controller.getHashMapOperator().get(chatMessage.getProducerID());

                            if(bindOperator==null)
                                continue;

                            int ID = bindOperator.getOperatorController().getIDtracker();

                            //System.out.println("ID:   "+ID);
              //              Bubble bubble = new Bubble(reply, controller);
                            //int index = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                 //           System.out.println(controller.getMessageProducerID().size()+  "   index:  "+index);

                            String username = chatMessage.getOwner();

                            if(bindOperator!=null)
                               username = username.equals("")?bindOperator.getClientName():username;
                            /*if(index>=0 && index<controller.getListItems().size())            //don't get from list
                                username= controller.getListItems().get(index).getUser().getUserName();*/



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

//                            bindOperator.getTextArea().appendText("User:  " + reply+"\n\n");



/****************/
                            //String JMSreplyTo = chatMessage.getMessage().getJMSMessageID();
                            if(correID!=null) {
                              //  System.out.println("MessageID working"+ JMSreplyTo);
                                if (!correID.equalsIgnoreCase(Constant.correalationID)) {
                                        if(!chatMessage.getTextMessage().equals(Constant.exitMessage) ){
                                            OperatorBubble bubble = new OperatorBubble(chatMessage.getOwner(), chatMessage.getTextMessage(), chatMessage.getTime());
                                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                                            bindOperator.getChatHolder().addRow(ID, bubble.getRoot());

                                            //   Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
                                        }

                                        else {

                                            if( chatMessage.getTextMessage().equals(Constant.exitMessage)){
                                                chatMessage.setTextMessage(Constant.exitBubbleMessage);

                                            }
                                            OperatorBubble bubble = new OperatorBubble(chatMessage.getOwner(), chatMessage.getTextMessage(), chatMessage.getTime());
                                            bindOperator.getChatHolder().addRow(ID, bubble.getRoot());



                                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                                            bindOperator.getOperatorController().setClosedAlready(true);


                                            controller.getHashMapOperator().get(chatMessage.getProducerID()).getChatHolder().setDisable(true);
                                            controller.sendButton.setDisable(true);
                                            controller.messageTextField.setDisable(true);
                                            controller.getDoTrain().setDisable(true);
/****Disabling user Item***/
                                            try{
                                                int index1 = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                                                if(index1>=0 && index1<controller.getChatUsersList().getItems().size())
                                                    controller.getChatUsersList().getItems().get(index1).changePicture(true);
                                            }
                                            catch (Exception e){
                                                e.printStackTrace(); /*************  do no exist --fix   IndexOutOfBoundsException*************/
                                            }




                                           // Platform.runLater(() -> controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax()));
                                        }
                                    bindOperator.getHistoryController().writehistory(Constant.ID_O, username,chatMessage);

                                }

//                                else {
//                                    UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
//                                    //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
//                                    bindOperator.getChatHolder().addRow(ID, bubble.getRoot());
//                                }
                            }
                            else {

                                if( chatMessage.getTextMessage().equals(Constant.exitUserMessage)){
                                    chatMessage.setTextMessage(Constant.exitUserBubbleMessage);

                                    bindOperator.getHistoryController().writehistory(Constant.ID_U, username,chatMessage);

                                    bindOperator.getOperatorController().setClosedAlready(true);


                                    controller.getHashMapOperator().get(chatMessage.getProducerID()).getChatHolder().setDisable(true);
                                    controller.sendButton.setDisable(true);
                                    controller.messageTextField.setDisable(true);
                                    controller.getDoTrain().setDisable(true);
/**** disabling useritem***/
                                    try{
                                        int index1 = controller.getMessageProducerID().indexOf(chatMessage.getProducerID());
                                        if(index1>=0 && index1<controller.getChatUsersList().getItems().size())
                                            controller.getChatUsersList().getItems().get(index1).changePicture(true);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace(); /*************  do no exist --fix   IndexOutOfBoundsException*************/
                                    }

                                }
                                if(username.equals(""))
                                    username = Constant.Annonymus;
                                UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
                                bindOperator.getChatHolder().addRow(ID, bubble.getRoot());

                            }

/******************/


                            int cID = controller.getMessageProducerID().indexOf(pID);//current ID;
                            //System.out.println("cid:   "+ cID);

                            //executor.execute();

                                int sID = controller.getChatUsersList().getSelectionModel().getSelectedIndex(); // selected ID

                            //    System.out.println("selected   "+cID);
                                if(sID!=cID && cID<controller.getChatUsersList().getItems().size() && cID>=0){
                                 //   System.out.println("should work");
                                    try{
                                        Platform.runLater(() -> controller.getChatUsersList().getItems().get(cID).startBlink());
                                    }
                                    catch (ArrayIndexOutOfBoundsException e){
                                        System.out.println("Not in the Chat user list");
                                    }
                                    catch (Exception e){
                                        System.out.println("Not in the Chat user list");
                                    }

                                }



                            Platform.runLater(() -> {


                             //   System.out.println("firsttime:   " + isFirstime + "    " + controller.getListItems().isEmpty());
                                if (controller.getListItems().size()==1) {
//                                    controller.getMessageTextField().setDisable(false);
//                                    controller.getDoTrain().setDisable(false);
                                    System.out.println("first");
                                    //isFirstime = false;

                                    UserItem item = controller.getListItems().get(0);
                                    controller.setUsername(item);
                                    controller.getChatUsersList().getSelectionModel().select(0);


                                }


                                if(controller.getMessageTextField().isDisabled()){
                                    controller.messageDisplay.setDisable(false);
                                    controller.sendButton.setDisable(false);
                                    controller.messageTextField.setDisable(false);
                                    controller.getDoTrain().setDisable(false);
                                }
                                //ScrollPane messageHolder = new ScrollPane();
                                //messageHolder.setContent((bindOperator.getChatHolder()));

/****need tofix here**/
                                controller.messageDisplay.setVvalue(controller.messageDisplay.getVmax());
                                //messageDisplay.setVvalue(messageDisplay.getVmax());
                                //    controller.messageDisplay.setVvalue(messageHolder.getVmax());
                            });


                            final String finalReply = reply;
                            final String producerIDDD = chatMessage.getProducerID();
                            Platform.runLater(() -> {
                                    if(!controller.getStage().isFocused() || controller.getStage().isIconified() || !controller.getStage().isShowing()) {
                                        final int index = controller.getMessageProducerID().indexOf(producerIDDD);
                                        String username1 =null;
                                        if(index>=0 && index <controller.getListItems().size())
                                             username1 = controller.getListItems().get(index).getUser().getUserName();
                                        else
                                            username1 =Constant.Annonymus;

                                        NotificationController.getNotification(finalReply, username1,controller,index);
                                        Platform.runLater(() -> {
                                            controller.chatUsersList.getFocusModel().focus(index);
                                            controller.chatUsersList.scrollTo(index);
                                        });

                                    }
                                });



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
                            //      System.out.print("Operator:   ");
                            //  send = in.nextLine();


                        }
                        else if(correID!=null && !correID.equals(Constant.correalationID) ){
//                            OperatorBubble bubble = new OperatorBubble(defaultOperator, chatMessage.getTextMessage(), chatMessage.getTime());
//                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
//                            bindOperator.getChatHolder().addRow(ID, bubble.getRoot());

                            System.out.println("from other operator:  "+ correID);

                        }

                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                        System.out.println("Null");
                      //  e.printStackTrace();
                        //   break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

    }

    private int loadHistory(BindOperator bindOperator) throws IOException {

        int count = 0;
        CsvReader messages = bindOperator.getHistoryController().readHistory();
        String userName = null;
        if(messages != null){
            int track=bindOperator.getOperatorController().getIDtracker();
            try {
                messages.readHeaders();

                //GridPane oldhistory = bindOperator.getOldchatHolder();//controller.getGridPane();
                ArrayList<HistoryMessage> historyMessages = bindOperator.getHistoryMessages();
                while (messages.readRecord()) {
                    String ID = messages.get("id");
                    String from = messages.get("from");
                    String message = messages.get("message");
                    String time = messages.get("time");

                    if(!from.equals(Constant.Annonymus) &&!from.equals(Constant.BOT_TAG) && !from.equals(Constant.operatorhistoryID)&& userName==null && bindOperator.getClientName()==null) {
                        System.out.println("setting client from History: "+ from);
                        userName = from;
                        bindOperator.setClientName(userName);
                    }
                    historyMessages.add(new HistoryMessage(ID,from, message,time));

//                    int id = Integer.parseInt(ID);
//////working here
//
//                    if(from.equals(Constant.operatorhistoryID)) {
//                        OperatorBubble bubble = new OperatorBubble(defaultOperator, message, time);
//                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
//                        oldhistory.addRow(id, bubble.getRoot());
//                       // bindOperator.getChatHolder().addRow(id, bubble.getRoot());
//
//
//                    }
//                    else {
//                        UserBubble bubble = new UserBubble(from, message, time);
//                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
//                        oldhistory.addRow(id, bubble.getRoot());
//                    //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
//                    }
//                    for (Node node : bindOperator.getChatHolder().getChildren()) {
//                        System.out.print("index:    "+GridPane.getRowIndex(node)+"     ");
//                        GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                        System.out.println(GridPane.getRowIndex(node));
//
//                    }
           //         bindOperator.getChatHolder().addRow(0, oldhistory);
       //             Node node = bindOperator.getChatHolder().getChildren().get(0);

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
              //  System.out.println(historyMessages);
               // bindOperator.setHistoryMessages(historyMessages);
              //  bindOperator.setOldchatHolder(oldhistory);
                messages.close();

                bindOperator.setHistoryMessages(historyMessages);

                SeperatorLine seperatorLine = new SeperatorLine(bindOperator,0);            // uncomment
                //   bindOperator.getChatHolder().getChildren().clear();  // uncomment
     //           Platform.runLater(() -> {
                    bindOperator.getChatHolder().add(seperatorLine.getSeperator(),0,0); // uncomment
      //          });


            }

            catch(IOException e){
                //setIDtracker(0);
                count =0;
                e.printStackTrace();
            }
            catch(Exception e){
                //setIDtracker(0);
                e.printStackTrace();
            }

        }
        else {
      //      System.out.println("////////////////////////   " +getIDtracker());
            //IDtracker =0;
        }



        return count;
    }

    public int getMessageCounter() {
        operatorController.messageCounter = operatorController.messageCounter+1;
        return operatorController.messageCounter;
    }

    public Operator getOperator() {
        return operator;
    }

    public GridPane getGridPane() {
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

    public void setSubscriptionName(String subscriptionName) {
        operator.setSubscriptionName(subscriptionName);
    }

    protected void createSession(){
        operator.create();
        setSessionCreated(true);
    }

    public ChatController getController() {
        return controller;
    }

    protected BlockingQueue<ChatMessage> getChatMessagess() {
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

    public void closeConnection()  {

        try{
            setSessionCreated(false);
            operatorController.operator.closeConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setFirstime(boolean firstime) {
        isFirstime = firstime;
    }

    public void setChatMessagess(BlockingQueue<ChatMessage> chatMessagess) {
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

    public synchronized int getIDtracker() {
        IDtracker = IDtracker+1;
        System.out.println("ID:  "+IDtracker);
        return IDtracker;

    }

    public MessageDistributionHandler getMessageDistributionHandler() {
        return messageDistributionHandler;
    }

    public boolean isSessionCreated() {
        return isSessionCreated;
    }

    public void setSessionCreated(boolean sessionCreated) {
        isSessionCreated = sessionCreated;
    }

    public void setIDtracker(int IDtracker) {
        this.IDtracker = IDtracker;
    }

    public boolean isClosedAlready() {
        return isClosedAlready;
    }

    public void setClosedAlready(boolean closedAlready) {
        isClosedAlready = closedAlready;
    }

    public Timer getTimer() {
        return timer;
    }

    public NetworkDownHandler getNetworkHandler() {
        return networkHandler;
    }

    public OfflineNetworkDownHandler getOfflineNetworkDownHandler() {
        return offlineNetworkDownHandler;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    class OfflineNetworkDownHandler extends Thread{


        Thread thread = this;
//        String ID = Constant.getRandomString();

        public void run() {
            //super.run();
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


            if (isOnline ) {
                try {
//                    for (int index = 0; index < controller.getListItems().size(); index++) {
//
//                        UserItem useritem = controller.getListItems().get(index);
//                        String producerID =useritem.getUser().getSubscriptionName();
//                        System.out.println("producerID:     "+producerID);
//                        controller.getHashMapOperator().get(producerID).getOperatorController().createSession();
///*                        OperatorController operatorController = new OperatorController(producerID, Constant.topicPrefix + producerID, controller);
//                        int count =   controller.getHashMapOperator().get(producerID).getOperatorController().getMessageCounter() -1;
//                        int idtracker= controller.getHashMapOperator().get(producerID).getOperatorController().getIDtracker() -1;
//                        operatorController.setMessageCounter(count);
//                        operatorController.setIDtracker(idtracker);
////                        ChatMessage chat = new ChatMessage();
////                        chat.setTextMessage("sdfsdfssdgs");
//                      //  operatorController.sendMessage(chat, operatorController);
////                        controller.getHashMapOperator().get(producerID).getOperatorController().closeConnection();
//                        controller.getHashMapOperator().get(producerID).setOperatorController(operatorController);*/
//
//
//                    }




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
                    Platform.runLater(() -> controller.statusImageView.setImage(image_online));


                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        }

        public  void stopThread(){
//            thread.stop();

            thread = null;

        }

    }


    class NetworkDownHandler extends Thread{

        Thread thread = this;
        volatile boolean isRunning = true;
        String ID = Constant.getRandomString();
        public void run() {
            thread = Thread.currentThread();
            isRunning = true;

           //Constant.getRandomString();

            while(isRunning ){
             //   System.out.println("network thread   "+isRunning);
                //System.out.println("Im running");
                if(controller.statusImageView ==null )
                    continue;

            try {

                Operator operator = new Operator(ID, ID);
                operator.create();
                boolean isConnected = operator.isConnected();
                operator.closeConnection();
                //         System.out.println("inside:  " + isOnline);
//                    Thread.sleep(100);

                if (isConnected) {
                    controller.statusImageView.setImage(image_online); //==========================
                    isOnline = true;


                } else {
                    controller.statusImageView.setImage(image_offline);//===========================
                    isOnline = false;
                    System.out.println("Resolving connection...");

                }
                operator = null;
            } catch (IllegalStateException e) {
              //  isOnline = false;
                try {
                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
              //  controller.statusImageView.setImage(image_offline);
            } catch (JMSException e) {
              //  isOnline = false;
                try {

                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
             //   controller.statusImageView.setImage(image_offline);
            }
                catch (Exception e){
                    isOnline =false;
               //     System.out.println("NULL Operator");
                    try {

                        thread.sleep(100);

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    try{
                        controller.statusImageView.setImage(image_offline);
                    }
                    catch(NullPointerException e2){
                        e.printStackTrace();
                    }

                }
                try {

                    thread.sleep(200);
                } catch (InterruptedException e1) {
                   // e1.printStackTrace();
                }
        }

//            stopThread();
        }

        public  void stopThread(){

            isRunning = false;
            //Thread t = thread;
            thread = null;
            //t.interrupt();
        }
    }



    class MessageDistributionHandler extends Thread{
        boolean  isRunning= true;
        Thread thread = this;

        public void run() {

            thread = Thread.currentThread();
            isRunning = true;

            while(isRunning){
                try {
                  //  System.out.println("Distributing:     "+    chatMessagess.size());
                    if(!chatMessagess.isEmpty()){
                        Platform.runLater(() -> {
                            try {
                                if(isReceived)
                                    routeChat(); //routeMessagetoThread();
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }
                            catch (Exception e) {
//                                e.printStackTrace();
                            }

                        });

                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                try {

                    thread.sleep(100);
                } catch (InterruptedException e1) {
                    // e1.printStackTrace();
                }
            }

//            stopThread();
        }

        public  void stopThread(){
            isRunning = false;
            Thread t = thread;
            thread.stop();
            thread = null;
            t.interrupt();
        }
    }



}
