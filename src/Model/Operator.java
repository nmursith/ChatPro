package Model;

import Controller.ConfigurationController;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by mmursith on 11/24/2015.
 */
public class Operator{
    private String subscriptionName;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private ConnectionFactory connectionFactory;
    private String topicName;
    private Topic topic;
    private Destination destination;



    private static int ackMode;
    private static String messageBrokerUrl;
    private boolean transacted = false;
    private boolean isConnected;



    static {
        messageBrokerUrl = ConfigurationController.readConfig().getURL();//"tcp://localhost:61616";//ActiveMQConnection.DEFAULT_BROKER_URL;//"tcp://localhost:61616";
        //messageBrokerUrl ="tcp://cmterainsight:61616";
     //  messageBrokerUrl ="tcp://cmterainsight:61616?trace=false&soTimeout=60000";
        ackMode = Session.AUTO_ACKNOWLEDGE;

    }


    public Operator(String subscriptionName, String topicName) throws JMSException {
        this.subscriptionName = subscriptionName;
        this.topicName = topicName;
        this.create();

    }


    public void create() throws JMSException {

        try {
            connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
            connection =connectionFactory.createConnection();
            connection.setClientID(subscriptionName);
            session =connection.createSession(transacted, ackMode);
            destination = this.session.createTopic(topicName);
            topic = session.createTopic(topicName);
            messageProducer = session.createProducer(topic);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            connection.start();
            isConnected =true;
    }
        catch (JMSException e){
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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void closeConnection() throws JMSException {
        connection.close();
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
}
