package Controller;

import Model.Variable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mmursith on 12/12/2015.
 */

public class  VariablesController {

    private static ArrayList<Variable> variableList;
    public static void main(String[] args) {

  //      writeVariables();
//        System.out.println(configurationController.readConfig().toString());
        readVariables();


    }

    public static void writeVariables(ArrayList<Variable> variableList) {
        JSONObject object = new JSONObject();
        JSONArray list = new JSONArray();

//        JSONObject obj = new JSONObject();
//        obj.put("ID", "operator0");
//        obj.put("name", "chat.*");
//
//        JSONObject obj2 = new JSONObject();
//        obj2.put("ID", "chat.*");
//        obj2.put("name", "chat.*");
//        list.add(obj);
//        list.add(obj2);


        for (Variable variable:variableList) {
                JSONObject obj = new JSONObject();
                obj.put("ID", variable.getID());
                obj.put("name", variable.getName());
                list.add(obj);
        }




        object.put("variables", list);

        try {

            FileWriter file = new FileWriter("variables.json");
            file.write(object.toJSONString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

  //      System.out.println(object);
    }


    public static ArrayList readVariables() {

        variableList = new ArrayList<>();

        try {
 //           Scanner scanner = new Scanner(new File("variables.json"));
//

            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(new FileReader("variables.json")));

            JSONArray msg = (JSONArray) jsonObject.get("variables");


           for (int i=0; i<msg.size(); i++){
               JSONObject obj = (JSONObject) msg.get(i);
               variableList.add(new Variable((String) obj.get("ID"), (String) obj.get("name")));
 //              System.out.println(obj.get("ID")+"      "+obj.get("name"));

            }

        } catch (FileNotFoundException | ParseException e) {
            setVariable();
            e.printStackTrace();
        } catch (Exception e){

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