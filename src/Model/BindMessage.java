package Model;

import Controller.OperatorController;

/**
 * Created by mmursith on 12/10/2015.
 */
public class BindMessage {
    private ChatMessage message;
    private OperatorController operatorController;

    public BindMessage(ChatMessage message, OperatorController operatorController){
        this.operatorController = operatorController;
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public OperatorController getOperatorController() {
        return operatorController;
    }

    public void setOperatorController(OperatorController operatorController) {
        this.operatorController = operatorController;
    }
}
