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

    private String time;
    private String textMessage;

    private String Owner;



    public ChatMessage(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy/MM/dd  hh:mm a ");
        time = ft.format(dNow);


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

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getOwner() {
        if(Owner==null)
            Owner ="";
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }
}
