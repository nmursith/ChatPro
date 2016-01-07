package Controller;

import Model.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SeparatorController {
    private BindOperator bindOperator;


    public static void main(String [] args) throws IOException {
    //    System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
//        URL url = new URL("https://www.ietf.org/rfc/rfc2560.txt");
//        Scanner s = new Scanner(url.openStream());
//
//        System.out.println(s.next());
    }

    public void showHistory(ActionEvent actionEvent) {
        String defaultOperator = ConfigurationController.readConfig().getOperator();
        bindOperator.getChatHolder().getChildren().remove(0);
        ArrayList<HistoryMessage> historyMessages = bindOperator.getHistoryMessages();

        GridPane oldhistory = bindOperator.getOldchatHolder();

        if(bindOperator.getOldchatHolder()!=null && historyMessages!=null){
//            for (Node node : bindOperator.getChatHolder().getChildren()) {
//                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
//                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                //System.out.println(GridPane.getRowIndex(node));
//            }
            for (HistoryMessage history: historyMessages) {
                try{
                    int id = Integer.parseInt(history.getID());

                    if(history.getFrom().equals(Constant.operatorhistoryID)) {
                        OperatorBubble bubble = new OperatorBubble(defaultOperator, history.getMessage(), history.getTime());
                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                        oldhistory.addRow(id, bubble.getRoot());
                        // bindOperator.getChatHolder().addRow(id, bubble.getRoot());


                    }
                    else {
                        UserBubble bubble = new UserBubble(history.getFrom(), history.getMessage(), history.getTime());
                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                        oldhistory.addRow(id, bubble.getRoot());
                        //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
                    }
                }
                catch (IOException e){
                        System.out.println("Problem in loading history");
                }

            }



            bindOperator.getChatHolder().addRow(0, oldhistory);
        }
        System.out.println("show history");

    }

    public BindOperator getBindOperator() {
        return bindOperator;
    }

    public void setBindOperator(BindOperator bindOperator) {
        this.bindOperator = bindOperator;
    }
}


