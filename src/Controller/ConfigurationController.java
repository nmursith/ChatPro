package Controller;

import Model.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mmursith on 12/9/2015.
 */
 public final class  ConfigurationController {
    private static Configuration configuration = null;

//    public static void main(String[] args) {
//        ConfigurationController configurationController = new ConfigurationController();
//        //configurationController.writeConfig();
//        System.out.println(configurationController.readConfig().toString());
//
//
//
//    }

    public void writeConfig(){
        JSONObject obj = new JSONObject();
        obj.put("operator", "operator0");
        obj.put("topic", "chat.*");
        obj.put("subscription", "chat.*");
        obj.put("destination", "chat.*");
        obj.put("URL", "tcp://cmterainsight:61616?trace=false&soTimeout=60000");

//        JSONArray list = new JSONArray();
//        list.add("msg 1");
//        list.add("msg 2");
//        list.add("msg 3");
//
//        obj.put("messages", list);

        try {

            FileWriter file = new FileWriter("config.json");
            file.write(obj.toJSONString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(obj);
    }

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
//            System.out.println(operator );
//            System.out.println(topic );
            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("messages");
//            Iterator<String> iterator = msg.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }

        } catch (FileNotFoundException e) {
            setConfiguration();
            e.printStackTrace();
        } catch (IOException e) {
            setConfiguration();
            e.printStackTrace();
        } catch (ParseException e) {
            setConfiguration();
            e.printStackTrace();
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