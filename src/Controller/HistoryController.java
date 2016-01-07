package Controller;

/**
 * Created by mmursith on 12/9/2015.
 */

import Model.ChatMessage;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HistoryController {
    private String fileName;
    public  static void main(String [] args){
        HistoryController historyController = new HistoryController("users");
//  //      historyController.writehistroty();
        historyController.readHistory();
        String history = "\"VARHISTORY\":[{\"from\":\"BOT\",\"msg\":\"Hi Pubudu, Can I help you improve your code quality today?\"},{\"from\":\"Pubudu\",\"msg\":\"no\"},{\"from\":\"BOT\",\"msg\":\"Do send me a message if you need any more help. Bye for now.\"},{\"from\":\"Pubudu\",\"msg\":\"what is insightlive?\"},{\"from\":\"BOT\",\"msg\":\"ERA Insight helps you manage code quality by creating the visibility to your ongoing development as you scale up.\"},{\"from\":\"Pubudu\",\"msg\":\"what is your name?\"},{\"from\":\"BOT\",\"msg\":\"Operator Connected\"}]";
        try {
            historyController.writeHistory(history);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public HistoryController(String fileName){
        this.fileName = "C:\\history\\"+fileName+".csv";


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

    public void writeHistory(String botHistory) throws ParseException {
        try {

            String history="{"+botHistory+"}";
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
                    System.out.println(obj.get("from")+"      "+obj.get("msg"));
                    String from = (String)obj.get("from");
                    String messg = (String)obj.get("msg");



                    csvOutput.write("");
                    csvOutput.write(from);
                    //csvOutput.write(message);
                    csvOutput.write(messg);
                    csvOutput.write("");

                    csvOutput.endRecord();



                }

                csvOutput.close();


            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        catch (Exception e){

        }
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
