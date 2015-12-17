package Controller;

import Model.*;
import com.csvreader.CsvReader;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.GridPane;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.IllegalStateException;
import javax.jms.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mmursith on 11/24/2015.
 */
public class OperatorController implements MessageListener {

    private Operator operator;
    private java.util.Queue<ChatMessage> chatMessagess;
    private Vector<String> messageProduceID;

    private ChatController controller;
    private MessageConsumer messageConsumer;
    private long messageCounter;



    private OperatorController operatorController;      //static or volatile or something
    private static String defaultOperator;
    private volatile boolean isOnline;
    private NetworkDownHandler networkHandler;
    private volatile boolean isFirstime =true;
    private final Queue<ChatMessage> cachedMessages =  new LinkedList<>();
    private volatile Queue<Notification> pendingNotification;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);  // 100 blink at a time




    public OperatorController(String subscriptionName, String topicName, ChatController controller) throws JMSException {
        this.operator = new Operator(subscriptionName, topicName);
        this.isOnline = true;
        this.messageProduceID = new Vector<>();
        this.chatMessagess= new LinkedList<>();

        this.controller = controller.getInstance();
        this.operatorController = this;
        this.networkHandler = new NetworkDownHandler();
        this.pendingNotification = new LinkedList<>();



        defaultOperator = ConfigurationController.readConfig().getOperator();//"operator1";
     //   this.notificationController = new NotificationController();
        try {
            //if
            if(subscriptionName.equalsIgnoreCase(defaultOperator)) {
                messageConsumer = operator.getSession().createDurableSubscriber(getTopic(), getSubscriptionName());
                messageConsumer.setMessageListener(this);
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
        //

   //     notificationHandler.start();

   //     System.out.println("Listening..");
    }


    public void sendMessage(ChatMessage chatMessage,OperatorController operator) throws JMSException {

        // check if a message was received
        if (chatMessage != null) {
                try{

                    TextMessage response = operator.getSesssion().createTextMessage();
                    String myMessage = chatMessage.getTextMessage();

                    response.setText(myMessage.trim().equalsIgnoreCase("exit") ? "DIRROUTETOBOT":myMessage);
                   // System.out.println("offline:   "+myMessage);

                    String random = Constant.correalationID;
                    response.setJMSCorrelationID(random);
                    operator.getMessageProducer().send(response);


                }
                catch (IllegalStateException e){
                    isOnline = false;

           //         networkHandler.stop();
                    if(networkHandler.isAlive()) {
                            networkHandler.stopThread();
                    }
                        isOnline = true;
                        cachedMessages.add(chatMessage);
              //      System.out.println("Message Added:  "+ chatMessage.getTextMessage() +"   "+cachedMessages.size());
                    networkHandler = new NetworkDownHandler();
                    networkHandler.start();

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


                GridPane chatHolder = getGridPane();
                OperatorController operatorController = new OperatorController(producerID, "chat."+producerID, controller);

//                if(!producerID.equals(defaultOperator))
//                    operatorController.getMessageConsumer().setMessageListener(null);
                BindOperator bindOperator = new BindOperator(operatorController, chatHolder);
                controller.getHashMapOperator().put(producerID, bindOperator);

                Thread.sleep(20);

                if(controller!=null){
                    int count  = loadHistory(controller.getHashMapOperator().get(producerID));
                    operatorController.setMessageCounter(count);        //starting
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
                        System.out.println("Initial startup fails");
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

                        User user = new User();
                        user.setuserId(tempName);
                        user.setUserName("user "+messageProduceID.size());
                        user.setSubscriptionName(tempName);
                        user.setTopicName("chat." + tempName);

                        controller.getListItems().add(new UserItem(user,controller));
                        controller.getChatUsersList().setItems(controller.getListItems());
                        System.out.println("updated");

                    }



            }
        }

        durablechatMessage = getChatMessagess();


            if(!durablechatMessage.isEmpty()) {
                while (!durablechatMessage.isEmpty()) {
                    //                  System.out.println("internal");
                    try {

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                            UserBubble bubble = new UserBubble(username, chatMessage.getTextMessage(), chatMessage.getTime());
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


                            BindOperator bindOperator =  controller.getHashMapOperator().get(chatMessage.getProducerID());
                            int counter = (int)bindOperator.getOperatorController().getMessageCounter();
//                            bindOperator.getTextArea().appendText("User:  " + reply+"\n\n");
                            bindOperator.getChatHolder().addRow(counter, bubble.getRoot());


                            try {
                                Thread.sleep(20);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


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

                                    pendingNotification.add(new Notification(reply, username, index));
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

    private int loadHistory(BindOperator bindOperator) {

        int count = 0;

        CsvReader messages = bindOperator.getHistoryController().readHistory();
        if(messages != null){

            try {
                messages.readHeaders();


                while (messages.readRecord()) {
                    String ID = messages.get("id");
                    String from = messages.get("from");
                    String message = messages.get("message");
                    String time = messages.get("time");
                    int id = Integer.parseInt(ID);
////working here


                    if(from.equalsIgnoreCase(defaultOperator)) {
                        OperatorBubble bubble = new OperatorBubble(defaultOperator, message, time);
                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                        bindOperator.getChatHolder().addRow(id, bubble.getRoot());


                    }
                    else {
                        UserBubble bubble = new UserBubble(from, message, time);
                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                        bindOperator.getChatHolder().addRow(id, bubble.getRoot());
                    }


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

    public long getMessageCounter() {
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

    private Queue<ChatMessage> getChatMessagess() {
        return operatorController.chatMessagess;
    }

    private Topic getTopic() {
        return operatorController.operator.getTopic();
    }

    public Vector<String> getMessageProduceID() {
        return operatorController.messageProduceID;
    }


    public void closeConnection() throws JMSException {
        operatorController.operator.closeConnection();
    }

    private void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }

    public String getSubscriptionName() {
        return operator.getSubscriptionName();
    }

    private Session getSesssion() {
        return operator.getSession();
    }

    private MessageProducer getMessageProducer() {
        return operator.getMessageProducer();
    }

    private MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }
    private void setMessageCounter(long messageCounter) {
        this.messageCounter = messageCounter;
    }

    private class NetworkDownHandler extends Thread{
    Thread thread = null;
        public void run() {
            super.run();
        thread = Thread.currentThread();
        System.out.println(isOnline);
            String ID = Constant.getRandomString();
        synchronized (cachedMessages) {
            while (!isOnline) {
                try {
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


                        Thread.sleep(20);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Offline: Session");
                    System.out.println("Re-connected");
                } catch (JMSException e) {
                    isOnline = false;

                    try {

                        Thread.sleep(20);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Offline: JMS");
                    System.out.println("Re-connected");
                }
            }


            if (isOnline) {
                try {
                    for (int index = 0; index < controller.getListItems().size(); index++) {

                        UserItem useritem = controller.getListItems().get(index);
                        String producerID =useritem.getUser().getSubscriptionName();
                        System.out.println(producerID);
                        OperatorController operatorController = new OperatorController(producerID, "chat." + producerID, controller);

                        ChatMessage chat = new ChatMessage();
                        chat.setTextMessage("sdfsdfssdgs");
                      //  operatorController.sendMessage(chat, operatorController);
                        controller.getHashMapOperator().get(producerID).setOperatorController(operatorController);
                    }
                    OperatorController operatorController = new OperatorController(OperatorController.defaultOperator, "chat.*", controller);
                    controller.getHashMapOperator().get(OperatorController.defaultOperator).setOperatorController(operatorController);
                    setOperatorController(controller.getHashMapOperator().get(OperatorController.defaultOperator).getOperatorController());

                    System.out.println("contained:   " + cachedMessages.size());

                    while (!cachedMessages.isEmpty()) {
                        System.out.println("Re - Sending: ");
                        ChatMessage chatMessage = cachedMessages.remove();
                        BindOperator bindOperator = controller.getHashMapOperator().get(chatMessage.getProducerID());
                        sendMessage(chatMessage, bindOperator.getOperatorController());
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
            Thread t = thread;
            thread = null;
            t.interrupt();
        }

    }


}
