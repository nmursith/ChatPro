package Controller;

/**
 * Created by mmursith on 12/9/2015.
 */

import Model.ChatMessage;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HistoryController {
    private String fileName;
//    public  static void main(String [] args){
//        HistoryController historyController = new HistoryController("users");
//  //      historyController.writehistroty();
//        historyController.readHistory();
//    }
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
