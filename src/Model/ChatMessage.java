package Model;

import javax.jms.Message;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mmursith on 11/24/2015.
 */
public class ChatMessage {

    public static DateFormat dateFormat;
    public static Calendar calendar;

    private Message message;
    private String producerID;

    private String time;
    private String textMessage;



    public ChatMessage(){
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calendar = Calendar.getInstance();
    }
    public static void main(String []args) {
        ChatMessage chatMessage = new ChatMessage();
        System.out.println(chatMessage.getDateFormat().format(chatMessage.getCalendar().getTime()));
    }

    public Calendar getCalendar() {
        return calendar;
    }



    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getProducerID() {
        return producerID;
    }

    public void setProducerID(String producerID) {
        this.producerID = producerID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
