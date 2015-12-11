package extra;

import org.apache.activemq.ActiveMQConnection;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mmursith on 11/26/2015.
 */
public class ChatControllerDemo {

    public static void main(String[]args){

        try {
            System.out.println(ActiveMQConnection.DEFAULT_BROKER_URL);
            JMXServiceURL url = new JMXServiceURL("service:jmx:tcp://localhost:61616");//JMXServiceURL("rmi", "localhost", 0, "/jndi/jmx");
            Map environment = new HashMap();

            String[] credentials = new String[]{"admin", "admin"};
            environment.put(JMXConnector.CREDENTIALS, credentials);

     //       ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
            JMXConnector cntor = JMXConnectorFactory.connect(url, environment);

            MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

            ObjectName connectionNames = new ObjectName("org.apache.activemq:BrokerName=localhost," +
                    "Type=Connection,ConnectorName=openwire,Connection=*");
            Set<ObjectName> names = mbsc.queryNames(connectionNames, null);
            for(ObjectName name : names) {
                System.out.println("Name: "+name.getCanonicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
