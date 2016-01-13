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
    private boolean isAlreadyShown =false;
    private BindOperator bindOperator;
    private Node root =null;
    private  GridPane oldhistory;
    private int tracker;
    private ArrayList<HistoryMessage> historyMessages;
    private Image showImage =new Image(getClass().getResourceAsStream("minus75.png"));
    private Image hideImage =new Image(getClass().getResourceAsStream("add139.png"));
    private int id =0;
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
        System.out.println(this+"   "+ isAlreadyShown +"  " + historyMessages.size());
        if(!flag){
            flag = true;

            if(!isAlreadyShown){
                isAlreadyShown = true;

                String defaultOperator = ConfigurationController.readConfig().getOperator();
                Image botImage =new Image(getClass().getResourceAsStream("robotic.png"));

                if(root==null ) {
                    if(tracker ==0)
                        tracker = 1;
                    root = bindOperator.getChatHolder().getChildren().get(tracker-1);//bindOperator.getChatHolder().getChildren().get(0);
                }
                //bindOperator.getChatHolder().addRow(1,bindOperator.getChatHolder().getChildren().get(0));//+++++++++++++++++++++
                if(oldhistory==null) {
                    oldhistory =bindOperator.getOperatorController().getGridPane();
                    oldhistory.setPrefHeight(50);
                }
                //oldhistory.getChildren().clear();

                if(historyMessages!=null) {
//            for (Node node : bindOperator.getChatHolder().getChildren()) {
//                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
//                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                //System.out.println(GridPane.getRowIndex(node));
//            }
                    for (HistoryMessage history: historyMessages) {

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

                }
            }

            historyButton_view.setImage(showImage);
            middle_label.setText(" Hide History");

            System.out.println(bindOperator.getChatHolder().getChildren().remove(root));

            if(bindOperator.getChatHolder().getChildren().contains(oldhistory))
                bindOperator.getChatHolder().getChildren().remove(oldhistory);

            bindOperator.getChatHolder().add(oldhistory,0,tracker);

            if(!oldhistory.getChildren().contains(root))
                oldhistory.addRow(id+1,root);
            System.out.println("show history   "+ id);
        }
        else{

            historyButton_view.setImage(hideImage);
            System.out.println("HIDE :PPPPPPPPPPPPPPPPPPPPPPP");
           // bindOperator.getOldchatHolder().getChildren().clear();
            bindOperator.getChatHolder().getChildren().remove(oldhistory);

            flag = false;
            middle_label.setText(" Show History");
            // bindOperator.getChatHolder().getChildren().remove(0);
            oldhistory.getChildren().remove(root);
            bindOperator.getChatHolder().add(root,0,tracker);//.addRow(0, root);

        }


    }

    public ArrayList<HistoryMessage> getHistoryMessages() {
        return historyMessages;
    }

    public void setHistoryMessages(ArrayList<HistoryMessage> historyMessages) {
        this.historyMessages = historyMessages;
    }

    public int getTracker() {
        return tracker;
    }

    public void setTracker(int tracker) {
        this.tracker = tracker;
    }

    public BindOperator getBindOperator() {
        return bindOperator;
    }

    public void setBindOperator(BindOperator bindOperator) {
        this.bindOperator = bindOperator;
    }
}


