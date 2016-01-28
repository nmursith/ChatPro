package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SeparatorController {
    public Button historyButton;
    public Label middle_label;
    public ImageView historyButton_view;
    public Label time_label;
    private boolean flag = false;
    private boolean isAlreadyShown =false;
    private BindOperator bindOperator;
    private Node root =null;
    private  GridPane oldhistory;
    private int tracker;
    private ArrayList<HistoryMessage> historyMessages;
    private Image showImage =new Image(getClass().getResourceAsStream("downarrow.png"));
    //private Image showImage =new Image(getClass().getResourceAsStream("minus75.png"));
    //private Image hideImage =new Image(getClass().getResourceAsStream("add139.png"));
    private int id =0;
    private Stage historyStage;




    public void showHistory(ActionEvent actionEvent) {
            loadHistory();

    }

    private void loadHistory(){

        if(!historyMessages.isEmpty()) {

//            if (!flag) {
//                flag = true;
            //    System.out.println("show history   " + historyMessages.size());
            if (!isAlreadyShown) {
                isAlreadyShown = true;
                String defaultOperator = ConfigurationController.readConfig().getOperator();
                Image botImage = new Image(getClass().getResourceAsStream("robotic.png"));

                if (root == null) {
                    if (tracker == 0)
                        tracker = 1;
                    root = getRoot();//bindOperator.getChatHolder().getChildren().get(tracker - 1);//bindOperator.getChatHolder().getChildren().get(0);
                }
                //bindOperator.getChatHolder().addRow(1,bindOperator.getChatHolder().getChildren().get(0));//+++++++++++++++++++++
                if (oldhistory == null) {
                    oldhistory = bindOperator.getOperatorController().getGridPane();
                    oldhistory.setPrefHeight(50);
                    //oldhistory.setPrefWidth(425);
                    //oldhistory.setMaxWidth(425);
                }
                //oldhistory.getChildren().clear();

                if (historyMessages != null) {
                    //            for (Node node : bindOperator.getChatHolder().getChildren()) {
                    //                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
                    //                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
                    //                //System.out.println(GridPane.getRowIndex(node));
                    //            }

                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            for (HistoryMessage history : historyMessages) {

                                try {
                                    id = Integer.parseInt(history.getID())+1;
                                    //         System.out.println("ID:  "+ id);

                                    if (history.getFrom().equals(Constant.operatorhistoryID)) {
                                        OperatorBubble bubble = new OperatorBubble(defaultOperator, history.getMessage(), history.getTime());
                                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                                        oldhistory.add(bubble.getRoot(), 0, id);
                                        // bindOperator.getChatHolder().addRow(id, bubble.getRoot());


                                    } else if (history.getFrom().equalsIgnoreCase(Constant.BOT_TAG)) {
                                        OperatorBubble bubble = new OperatorBubble(Constant.BOT_TAG, history.getMessage(), history.getTime());
                                        bubble.setImage(botImage);
                                        //    GridPane.setHalignment(bubble.getFromBubble(), HPos.RIGHT);
                                        oldhistory.add(bubble.getRoot(), 0, id);
                                    } else {
                                        UserBubble bubble = new UserBubble(history.getFrom(), history.getMessage(), history.getTime());
                                        //         GridPane.setHalignment(bubble.getToBubble(), HPos.LEFT);
                                        oldhistory.add(bubble.getRoot(), 0, id);
                                        //    bindOperator.getChatHolder().addRow(id, bubble.getRoot());
                                    }

                                } catch (IOException e) {
                                    System.out.println("Problem in loading history");
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            //changeView();
                            showHistory();
                        }


                    });



                }
            }
            else{
               // changeView();
                historyStage.show();
            }





/*            }

            else {

                historyButton_view.setImage(hideImage);
             //   System.out.println("HIDE :PPPPPPPPPPPPPPPPPPPPPPP");
                // bindOperator.getOldchatHolder().getChildren().clear();
                bindOperator.getChatHolder().getChildren().remove(oldhistory);

                flag = false;
                middle_label.setText("Show History on ");
                // bindOperator.getChatHolder().getChildren().remove(0);
                oldhistory.getChildren().remove(root);
                bindOperator.getChatHolder().add(root, 0, tracker);//.addRow(0, root);

            }*/

        }
    }

    private void showHistory(){
        System.out.println("History on stage");

        ScrollPane historyPane = new ScrollPane();
        historyPane.setStyle("-fx-background-color:white;");

        oldhistory.setStyle("-fx-background-color:white;");

        historyPane.setContent(oldhistory);
        historyPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Scene scene = new Scene(historyPane);

        historyStage.setWidth(425);
        historyStage.setMaxWidth(425);
        historyStage.setHeight(oldhistory.getHeight());
        historyStage.setMaxWidth(413);




        historyStage.setScene(scene);
        historyStage.show();
        historyStage.setResizable(false);
        historyStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                    historyStage.close();
            }
        });



    }

    public  void changeView(){
        historyButton_view.setImage(showImage);
        middle_label.setText(" Hide History on ");

        System.out.println(bindOperator.getChatHolder().getChildren().remove(root));

        if (bindOperator.getChatHolder().getChildren().contains(oldhistory))
            bindOperator.getChatHolder().getChildren().remove(oldhistory);

        bindOperator.getChatHolder().add(oldhistory, 0, tracker);

        if (!oldhistory.getChildren().contains(root))
            oldhistory.addRow(id + 1, root);
    }
    public  void setTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy/MM/dd  hh:mm a ");
        String time = ft.format(dNow);
        time_label.setText(time);

    }
    public  void setTime(String time){
        time_label.setText(time);

    }
    public ArrayList<HistoryMessage> getHistoryMessages() {
        return historyMessages;
    }

    public void setHistoryMessages(ArrayList<HistoryMessage> historyMessages) {
        this.historyMessages = historyMessages;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
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

    public void hideHistory(Event event) {
        if(historyStage!=null && historyStage.isShowing()) {
            historyStage.close();
            System.out.println("Mouse exited");
        }
    }

    public void showHistoryonHover(Event event) {
        if(historyStage==null) {
            historyStage = new Stage(StageStyle.UNDECORATED);
            historyStage.initOwner(bindOperator.getOperatorController().getController().getStage());
        }

        if(!historyStage.isShowing()) {
            loadHistory();
            System.out.println("Mouse enterd");
        }
    }

    public void showArrow(Event event) {
        historyButton_view.setImage(showImage);

    }

    public void hideArrow(Event event) {
        historyButton_view.setImage(null);
    }
}


