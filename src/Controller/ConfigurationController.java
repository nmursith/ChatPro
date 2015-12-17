package Controller;

import Model.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by mmursith on 12/9/2015.
 */
 public class  ConfigurationController {
    private static Configuration configuration = null;

//    public static void main(String[] args) {
//        ConfigurationController configurationController = new ConfigurationController();
//        //configurationController.writeConfig();
//        System.out.println(configurationController.readConfig().toString());
//
//
//
//    }



    public static Configuration readConfig(){
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("config.json"));
            configuration = new Configuration();
            JSONObject jsonObject = (JSONObject) obj;
            String operator = (String) jsonObject.get("operator");
            String topic = (String) jsonObject.get("topic");
            String subscription = (String) jsonObject.get("subscription");
            String destination = (String) jsonObject.get("destination");
            String URL = (String) jsonObject.get("URL");

            configuration.setOperator(operator);
            configuration.setDestination(destination);
            configuration.setSubscription(subscription);
            configuration.setTopic(topic);
            configuration.setURL(URL);

//            System.out.println("Read:       "+URL );
//            System.out.println(destination );
            System.out.println(operator );
//            System.out.println(topic );
            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("messages");
//            Iterator<String> iterator = msg.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }

        } catch (IOException | ParseException e) {
            setConfiguration();
      //      e.printStackTrace();
        }
        return configuration;
    }

    private static void setConfiguration(){
        configuration = new Configuration();
        configuration.setOperator("operator1");
        configuration.setDestination("chat.*");
        configuration.setSubscription("chat.*");
        configuration.setTopic("chat.*");
        configuration.setURL("tcp://cmterainsight:61616?trace=false&soTimeout=60000");
    }
}