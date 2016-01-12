package Controller;

import Model.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SeparatorController {
    public Button historyButton;
    public Label middle_label;
    public ImageView historyButton_view;
    private boolean flag = false;
    private BindOperator bindOperator;
    private Node root =null;


    public static void main(String [] args) throws IOException {
    //    System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
//        URL url = new URL("https://www.ietf.org/rfc/rfc2560.txt");
//        Scanner s = new Scanner(url.openStream());
//
//        System.out.println(s.next());
    }

//    public void showHistory(ActionEvent actionEvent) {
//        String defaultOperator = ConfigurationController.readConfig().getOperator();
//        Image botImage =new Image(getClass().getResourceAsStream("robotic.png"));
//
//        bindOperator.getChatHolder().getChildren().remove(0);
//        ArrayList<HistoryMessage> historyMessages = bindOperator.getHistoryMessages();
//
//        GridPane oldhistory = bindOperator.getOldchatHolder();
//
//        if(bindOperator.getOldchatHolder()!=null && historyMessages!=null) {
////            for (Node node : bindOperator.getChatHolder().getChildren()) {
////                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
////                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
////                //System.out.println(GridPane.getRowIndex(node));
////            }
//            for (HistoryMessage history : historyMessages) {
//                try {
//                    int id = Integer.parseInt(history.getID());
//                    //         System.out.println("ID:  "+ id);
//
//                    if (history.getFrom().equals(Constant.operatorhistoryID)) {
//                        OperatorBubble bubble = new OperatorBubble(defaultOperator, history.getMessage(), history.getTime());
//                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
//                        oldhistory.addRow(id, bubble.getRoot());
//                        // bindOperator.getChatHolder().addRow(id, bubble.getRoot());
//
//
//                    } else if (history.getFrom().equalsIgnoreCase(Constant.BOT_TAG)) {
//                        OperatorBubble bubble = new OperatorBubble(Constant.BOT_TAG, history.getMessage(), history.getTime());
//                        bubble.setImage(botImage);
//                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
//                        oldhistory.addRow(id, bubble.getRoot());
//                    } else {
//                        UserBubble bubble = new UserBubble(history.getFrom(), history.getMessage(), history.getTime());
//                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
//                        oldhistory.addRow(id, bubble.getRoot());
//                        //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
//                    }
//                } catch (IOException e) {
//                    System.out.println("Problem in loading history");
//                }
//
//            }
//
//        }
//
//            bindOperator.getChatHolder().addRow(0, oldhistory);
//
//        System.out.println("show history");
//
//    }



    public void showHistory(ActionEvent actionEvent) {
        int id =0;
        if(!flag){
            Image showImage =new Image(getClass().getResourceAsStream("minus75.png"));
            historyButton_view.setImage(showImage);
            flag = true;
            System.out.println("ShOwHiStOrY :PPPPPPPPPPPPPPPPPPPP");
            String defaultOperator = ConfigurationController.readConfig().getOperator();
            Image botImage =new Image(getClass().getResourceAsStream("robotic.png"));

            if(root==null)
                root = bindOperator.getChatHolder().getChildren().remove(0);//bindOperator.getChatHolder().getChildren().get(0);

            //bindOperator.getChatHolder().addRow(1,bindOperator.getChatHolder().getChildren().get(0));//+++++++++++++++++++++

            ArrayList<HistoryMessage> historyMessages = bindOperator.getHistoryMessages();

            GridPane oldhistory = bindOperator.getOldchatHolder();
            oldhistory.getChildren().clear();

            if(bindOperator.getOldchatHolder()!=null && historyMessages!=null) {
//            for (Node node : bindOperator.getChatHolder().getChildren()) {
//                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
//                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                //System.out.println(GridPane.getRowIndex(node));
//            }
                for (HistoryMessage history : historyMessages) {
                    try {
                        id = Integer.parseInt(history.getID());
                        //         System.out.println("ID:  "+ id);

                        if (history.getFrom().equals(Constant.operatorhistoryID)) {
                            OperatorBubble bubble = new OperatorBubble(defaultOperator, history.getMessage(), history.getTime());
                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                            oldhistory.addRow(id, bubble.getRoot());
                            // bindOperator.getChatHolder().addRow(id, bubble.getRoot());


                        } else if (history.getFrom().equalsIgnoreCase(Constant.BOT_TAG)) {
                            OperatorBubble bubble = new OperatorBubble(Constant.BOT_TAG, history.getMessage(), history.getTime());
                            bubble.setImage(botImage);
                            //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                            oldhistory.addRow(id, bubble.getRoot());
                        } else {
                            UserBubble bubble = new UserBubble(history.getFrom(), history.getMessage(), history.getTime());
                            //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                            oldhistory.addRow(id, bubble.getRoot());
                            //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
                        }

                    }
                    catch (IOException e) {
                        System.out.println("Problem in loading history");
                    }

                }
                oldhistory.addRow(id+1,root);

            }
            middle_label.setText(" Hide History");
            bindOperator.getChatHolder().getChildren().remove(oldhistory);
            bindOperator.getChatHolder().add(oldhistory,0,0);
            System.out.println("show history");
        }
        else{
            Image hideImage =new Image(getClass().getResourceAsStream("add139.png"));
            historyButton_view.setImage(hideImage);
            System.out.println("HIDE :PPPPPPPPPPPPPPPPPPPPPPP");
            bindOperator.getOldchatHolder().getChildren().clear();
            flag = false;
            middle_label.setText(" Show History");

            // bindOperator.getChatHolder().getChildren().remove(0);
            bindOperator.getChatHolder().add(root,0,0);//.addRow(0, root);

        }


    }


    public BindOperator getBindOperator() {
        return bindOperator;
    }

    public void setBindOperator(BindOperator bindOperator) {
        this.bindOperator = bindOperator;
    }
}


