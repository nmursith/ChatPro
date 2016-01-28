package Model;

import Controller.HistoryController;
import Controller.OperatorController;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


/**
 * Created by mmursith on 12/2/2015.
 */
public class BindOperator {

    private volatile OperatorController operatorController;
    private volatile HistoryController historyController;
    private volatile GridPane chatHolder;

    private String typedMessage;
    private volatile ArrayList<HistoryMessage> historyMessages;

    private String clientName;

    public BindOperator(OperatorController operatorController, GridPane gridPane){
        this.operatorController = operatorController;
        this.chatHolder = gridPane;
        this.historyController = new HistoryController(operatorController.getSubscriptionName());
        this.historyMessages = new ArrayList<>();
        this.clientName = null;


    }

    public OperatorController getOperatorController() {
        return operatorController;
    }

    public void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }

    public GridPane getChatHolder() {
        return chatHolder;
    }


    public HistoryController getHistoryController() {
        return historyController;
    }

    public void setChatHolder(GridPane chatHolder) {
        this.chatHolder = chatHolder;
    }



    public ArrayList<HistoryMessage> getHistoryMessages() {
        return historyMessages;
    }

    public void setHistoryMessages(ArrayList<HistoryMessage> historyMessages) {
        this.historyMessages = historyMessages;
    }


    public String getTypedMessage() {
        return typedMessage;
    }

    public void setTypedMessage(String typedMessage) {
        this.typedMessage = typedMessage;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


}
