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

    public void setChatHolder(GridPane chatHolder) {
        this.chatHolder = chatHolder;
    }

    public HistoryController getHistoryController() {
        return historyController;
    }

    public void setHistoryController(HistoryController historyController) {
        this.historyController = historyController;
    }
}
