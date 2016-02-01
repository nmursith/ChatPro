package Model;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by mmursith on 11/24/2015.
 */
public class Operator{
    private volatile String subscriptionName;
    private volatile Connection connection;
    private volatile Session session;
    private volatile MessageProducer messageProducer;
    private volatile String topicName;
    private volatile Topic topic;
    private volatile Destination destination;
    private String operatorID;

    private static int ackMode;
    private static String messageBrokerUrl;
    private boolean isConnected;
    private boolean isAnswered;

    static {
        messageBrokerUrl = Constant.configuration.getURL();////ActiveMQConnection.DEFAULT_BROKER_URL;//"tcp://localhost:61616";
      //  messageBrokerUrl ="tcp://localhost:61616";
        ackMode = Session.AUTO_ACKNOWLEDGE;

    }


    public Operator(String subscriptionName, String topicName) throws JMSException {
        this.subscriptionName = subscriptionName;
        this.topicName = topicName;
    //    this.create();

    }


//    public Operator(String operatorID, String subscriptionName, String topicName) throws JMSException {
//        this.subscriptionName = subscriptionName;
//        this.topicName = topicName;
//        this.operatorID = operatorID;
//        this.create();
//
//    }

    public void create() {

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
            connection = connectionFactory.createConnection();
            connection.setClientID(subscriptionName);
            boolean transacted = false;
            session =connection.createSession(transacted, ackMode);

            destination = this.session.createTopic(topicName);
            topic = session.createTopic(topicName);
            messageProducer = session.createProducer(topic);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            connection.start();
            isConnected =true;
        }
        catch (JMSException e){
         //   e.printStackTrace();
            if(e instanceof InvalidClientIDException)
                isAnswered = true;
            isConnected = false;
        }


    }


    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public MessageProducer getMessageProducer() {
        return messageProducer;
    }

    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }


    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void closeConnection()  {
        try{
            connection.close();
        }
         catch (JMSException e){
             e.printStackTrace();
         }
    }

    public Destination getDestination() {
        return destination;
    }

    public Topic getTopic() {
        return topic;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}
