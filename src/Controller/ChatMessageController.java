package Controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by mmursith on 1/22/2016.
 */
public class ChatMessageController {


    public static void main(String[] args){
        String json = createJSONmessage("nifras", "Hello");
        System.out.println(json);
        String [] strings = readJSONmessage(json);
        System.out.println(strings[0]);
        System.out.println(strings[1]);
    }

    public static String createJSONmessage(String owner, String text){
        JSONObject obj = new JSONObject();
        obj.put("owner", owner);
        obj.put("text",text);
        return obj.toString();


    }

    public static String [] readJSONmessage(String JSON){

        String [] messg = new String[2];
        try {
            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(JSON));

            String text = (String) jsonObject.get("text");
            String owner = (String) jsonObject.get("owner");
            messg[0] = text;
            messg[1] = owner;

        } catch (ParseException e) {
            messg[0] = JSON;
            messg[1] = null;

            e.printStackTrace();
        }



        return messg;
    }
}
