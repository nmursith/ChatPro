package Controller;

/**
 * Created by mmursith on 12/9/2015.
 */

import Model.BindOperator;
import Model.ChatMessage;
import Model.Constant;
import Model.HistoryMessage;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HistoryController {
    private String fileName;
    private String directory;
    private int tracker = 0;
    public  static void main(String [] args){
//        HistoryController historyController = new HistoryController("users");
////  //      historyController.writehistroty();
//        historyController.readHistory();
//        String history = "\"VARHISTORY\":[{\"from\":\"BOT\",\"msg\":\"Hi Pubudu, Can I help you improve your code quality today?\"},{\"from\":\"Pubudu\",\"msg\":\"no\"},{\"from\":\"BOT\",\"msg\":\"Do send me a message if you need any more help. Bye for now.\"},{\"from\":\"Pubudu\",\"msg\":\"what is insightlive?\"},{\"from\":\"BOT\",\"msg\":\"ERA Insight helps you manage code quality by creating the visibility to your ongoing development as you scale up.\"},{\"from\":\"Pubudu\",\"msg\":\"what is your name?\"},{\"from\":\"BOT\",\"msg\":\"Operator Connected\"}]";
//        try {
//            //historyController.writeHistory(history,null);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
    public HistoryController(String fileName){
        this.directory = "C:\\vAssistant\\history\\";

        System.out.println("Directory:   "+new File(directory).mkdir());
        this.fileName = "C:\\vAssistant\\history\\"+fileName+".csv";


    }

    public void writehistory(int id, String from, ChatMessage message){


        // before we open the file check to see if it already exists
        boolean alreadyExists = new File(fileName).exists();

        try {
            // use FileWriter constructor that specifies open for appending
            CsvWriter csvOutput = null;
            try {
                csvOutput = new CsvWriter(new FileWriter(fileName, true), ',');
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if the file didn't already exist then we need to write out the header line
            if (!alreadyExists)
            {
                csvOutput.write("id");
                csvOutput.write("from");
                csvOutput.write("message");
                csvOutput.write("time");
                csvOutput.endRecord();
            }
            // else assume that the file already has the correct header line

            // write out a few records
            csvOutput.write(id+"");
            csvOutput.write(from);
            //csvOutput.write(message);
            csvOutput.write(message.getTextMessage());
            csvOutput.write(message.getTime());

            csvOutput.endRecord();

            csvOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String writeHistory(String botHistory, BindOperator bindOperator, boolean isLatestHistory ) throws ParseException {
        String userName=null;
        OperatorController operatorController = bindOperator.getOperatorController();
        ArrayList<HistoryMessage> historyMessages = bindOperator.getHistoryMessages();
        int tracker = operatorController.getIDtracker();
        setTracker(tracker);

        try {

            String history="{"+botHistory+"}";
            System.out.println(history);
            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(history));
            System.out.println(jsonObject);
            JSONArray msg = (JSONArray) jsonObject.get("VARHISTORY");

            boolean alreadyExists = new File(fileName).exists();

            try {
                // use FileWriter constructor that specifies open for appending
                CsvWriter csvOutput = null;
                try {
                    csvOutput = new CsvWriter(new FileWriter(fileName, true), ',');
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // if the file didn't already exist then we need to write out the header line
                if (!alreadyExists)
                {
                    csvOutput.write("id");
                    csvOutput.write("from");
                    csvOutput.write("message");
                    csvOutput.write("time");
                    csvOutput.endRecord();
                }
                // else assume that the file already has the correct header line

                // write out a few records


                for (int i=0; i<msg.size(); i++){




                    JSONObject obj = (JSONObject) msg.get(i);
                    //variableList.add(new Variable((String) obj.get("ID"), (String) obj.get("name")));
              //      System.out.println(obj);

                    String from = (String)obj.get("from");
                    String messg = (String)obj.get("msg");
                //    System.out.println(from+"      "+messg);
                    String time = (String)obj.get("time");


                    if(!from.equals(Constant.BOT_TAG) && userName==null) {

                        userName = from;
                    }

                    if(messg.equalsIgnoreCase(Constant.ConnectedMessage) || messg.equalsIgnoreCase(Constant.DisConnectedMessage)){
                        continue;
                    }
                        if(i<(msg.size()) ){
                            int ID = operatorController.getMessageCounter();
                            csvOutput.write(ID + "");
                            csvOutput.write(from);
                            //csvOutput.write(message);
                            csvOutput.write(messg);
                            csvOutput.write(time);

                            csvOutput.endRecord();
                            //if(historyMessages!=null)
                            historyMessages.add(new HistoryMessage(i + "", from, messg, time));
                        }
                        else {
                            bindOperator.setHistoryMessages(historyMessages);
                            break;
                        }


                }

                csvOutput.close();
                return userName;

            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int getTracker() {
        return tracker;
    }

    public void setTracker(int tracker) {
        this.tracker = tracker;
    }

    public CsvReader readHistory(){
        CsvReader messages = null;
        try {
            messages = new CsvReader(fileName);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return messages;
    }
}
