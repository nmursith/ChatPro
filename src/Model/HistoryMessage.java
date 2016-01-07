package Model;

/**
 * Created by mmursith on 1/7/2016.
 */
public class HistoryMessage {
    private String ID ;
    private String from;
    private String message;
    private String time;

    public HistoryMessage(String ID, String from, String message, String time) {
        this.ID = ID;
        this.from = from;
        this.message = message;
        this.time = time;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
