package Controller;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ProducerInfo;

import javax.jms.*;
import java.util.Map;

/**
 * Created by mmursith on 1/6/2016.
 */
public class ActiveClientController implements MessageListener{
    String messageBrokerUrl ="tcp://localhost:61616";
    int ackMode = Session.AUTO_ACKNOWLEDGE;
    static String topicName = "chat.*";

    public static void main(String []args) throws Exception {
        ActiveClientController activeClientController = new ActiveClientController();
//        BrokerService broker = new BrokerService();
//
//// configure the broker
//        broker.addConnector("tcp://localhost:61616");
//        broker.setBrokerName("fred");
//        broker.start();
//
//        ActiveMQDestination destination = ActiveMQDestination.createDestination(topicName, ActiveMQDestination.TOPIC_TYPE);
//
//        System.out.println(getDestinationMap(broker,destination));

    }


    private static Map<ActiveMQDestination, org.apache.activemq.broker.region.Destination> getDestinationMap(BrokerService target,
                                                                                                             ActiveMQDestination destination) {
        RegionBroker regionBroker = (RegionBroker) target.getRegionBroker();
        return destination.isQueue() ?
                regionBroker.getQueueRegion().getDestinationMap() :
                regionBroker.getTopicRegion().getDestinationMap();
    }

    public ActiveClientController() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID("Active");
        boolean transacted = false;
        Session session =connection.createSession(transacted, ackMode);

        Destination destination = session.createTopic(topicName);

        Topic topic = session.createTopic(topicName);
        MessageProducer messageProducer = session.createProducer(topic);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        connection.start();

        Destination advisoryDestination = session.createTopic(topicName);//AdvisorySupport.getProducerAdvisoryTopic(destination);
        MessageConsumer consumer = session.createConsumer(advisoryDestination);
        consumer.setMessageListener(this);

    }



    @Override
    public void onMessage(Message msg) {
        try {
            System.out.println(msg.getJMSCorrelationID().toString());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        if (msg instanceof ActiveMQMessage){
            ActiveMQMessage aMsg =  (ActiveMQMessage)msg;
            ProducerInfo prod = (ProducerInfo) aMsg.getDataStructure();
            System.out.println("Active:   "+prod.getDestination());

        }
    }
}
