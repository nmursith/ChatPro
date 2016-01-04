package Model;

import Controller.HistoryController;
import Controller.OperatorController;
import javafx.scene.layout.GridPane;


/**
 * Created by mmursith on 12/2/2015.
 */
public class BindOperator {

    private  OperatorController operatorController;
    private HistoryController historyController;
    private GridPane chatHolder;
    private GridPane oldchatHolder;
    private String typedMessage;


    public BindOperator(OperatorController operatorController, GridPane gridPane){
        this.operatorController = operatorController;
        this.chatHolder = gridPane;
        this.historyController = new HistoryController(operatorController.getSubscriptionName());

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

    public GridPane getOldchatHolder() {
        return oldchatHolder;
    }

    public void setOldchatHolder(GridPane oldchatHolder) {
        this.oldchatHolder = oldchatHolder;
    }

    public String getTypedMessage() {
        return typedMessage;
    }

    public void setTypedMessage(String typedMessage) {
        this.typedMessage = typedMessage;
    }
}
