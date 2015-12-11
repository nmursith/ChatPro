package extra;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

public class AdvisoryConsumerApp implements MessageListener {

    private final String connectionUri = "tcp://localhost:8161";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer advisoryConsumer;
    private Destination monitored;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        monitored = session.createTopic("chat.*");
//        destination = session.createTopic(
//                        AdvisorySupport.getConsumerAdvisoryTopic(monitored).getPhysicalName() + "," +
//                        AdvisorySupport.getProducerAdvisoryTopic(monitored).getPhysicalName());


        destination=monitored;
        System.out.println(destination.toString());
        advisoryConsumer = session.createConsumer(destination);
        advisoryConsumer.setMessageListener(this);
        connection.start();
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void onMessage(Message message) {
        try {

            Destination source = message.getJMSDestination();
            System.out.println(message);
            System.out.println(message.getJMSType());
            System.out.println("ID:   "+message.getJMSMessageID());
            System.out.println("dest:   "+message.getJMSDestination().toString());




            if (source.equals(AdvisorySupport.getConsumerAdvisoryTopic(monitored))) {
                int consumerCount = message.getIntProperty("consumerCount");
                System.out.println("New Consumer Advisory, Consumer Count: " + consumerCount);
            } else if (source.equals(AdvisorySupport.getProducerAdvisoryTopic(monitored))) {
                int producerCount = message.getIntProperty("producerCount");
                System.out.println("New Producer Advisory, Producer Count: " + producerCount);
            }
        } catch (JMSException e) {
         //   e.printStackTrace();
        }
    }

    public void run() throws Exception {
        TimeUnit.MINUTES.sleep(10);
    }

    public static void main(String[] args) {
        AdvisoryConsumerApp example = new AdvisoryConsumerApp();
        System.out.print("\n\n\n");
        System.out.println("Starting Advisory Consumer example now...");
        try {
            example.before();
            example.run();
            example.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the Advisory Consumer example.");
        System.out.print("\n\n\n");
    }
}