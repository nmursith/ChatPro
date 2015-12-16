package Model;

import javax.jms.Message;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mmursith on 11/24/2015.
 */
public class ChatMessage {


    private Message message;
    private String producerID;
    private String userName;
    private String time;
    private String textMessage;



    public ChatMessage(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy/MM/dd  hh:mm a ");
        time = ft.format(dNow);


    }
    public static void main(String []args) {
        ChatMessage chatMessage = new ChatMessage();
        //System.out.println(chatMessage.ft.format(chatMessage.dNow));

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
