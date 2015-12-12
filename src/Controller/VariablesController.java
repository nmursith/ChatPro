package Controller;

import Model.Variable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mmursith on 12/12/2015.
 */

public final class  VariablesController {

    private static ArrayList<Variable> variableList;
    public static void main(String[] args) {
        VariablesController configurationController = new VariablesController();
        //configurationController.writeConfig();
//        System.out.println(configurationController.readConfig().toString());
        readVariables();


    }

    public void writeConfig() {
        JSONObject obj = new JSONObject();
//        obj.put("operator", "operator0");
//        obj.put("topic", "chat.*");
//        obj.put("subscription", "chat.*");
//        obj.put("destination", "chat.*");
//        obj.put("URL", "tcp://localhost:61616");

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);

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


    public static ArrayList readVariables() {

        variableList = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File("variables.json"));
//            JSONParser parser = new JSONParser();
//            Object obj = parser.parse(new FileReader("variables.json"));
//            JSONObject jsonObject = (JSONObject) obj;
//
//            JSONArray msg = (JSONArray) jsonObject.get("variables");
//            Iterator<String> iterator = msg.iterator();
//
//           while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }

            while (scanner.hasNext()) {
                JSONObject obj = (JSONObject) new JSONParser().parse(scanner.nextLine());
                variableList.add(new Variable((String) obj.get("ID"), (String) obj.get("name")));
              //  System.out.println(obj.get("ID")+"      "+obj.get("name"));
            }
        } catch (FileNotFoundException e) {
            setVariable();
            e.printStackTrace();
        } catch (ParseException e) {
            setVariable();
            e.printStackTrace();
        }
        catch (Exception e){

        }


      return variableList;
    }

    private static void setVariable() {
        variableList = new ArrayList<>();
        variableList.add(new Variable("userName","Username" ));
        variableList.add(new Variable("introducedViolations","Introduced Violations"));
        variableList.add(new Variable("openViolations", "Open Violations"));
        variableList.add(new Variable("runtimeViolations", "Runtime Violations"));

    }
}