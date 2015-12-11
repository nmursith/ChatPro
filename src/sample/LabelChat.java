package sample;/**
 * Created by mmursith on 12/7/2015.
 */

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LabelChat extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane chat = new GridPane();
        //FlowPane chat = new FlowPane(Orientation.VERTICAL,0, 20);

        chat.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane scroll = new ScrollPane(chat);

        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 500, 500);
        chat.setVgap(10);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(100);

        chat.getColumnConstraints().add(c1);
//        Bubble bubble = new Bubble();
//        chat.setPrefWrapLength(200);
        for (int i = 0; i < 20; i++) {
            BubbleDemo chatMessage = new BubbleDemo("Hi " + i, scene);
            //chatMessage.getStyleClass().add("chat-bubble");
            if(i%2==0) {
                GridPane.setHalignment(chatMessage.getFromBubble(), HPos.LEFT );
            //    chat.setAlignment(Pos.TOP_RIGHT);
//                chat.setColumnHalignment(HPoss.RIGHT);
//                chat.setRowValignment(VPos.CENTER);
//               chat.getChildren().add(chatMessage.getFromBubble());
                chat.addRow(i, chatMessage.getFromBubble());


            }
            else {
                GridPane.setHalignment(chatMessage.getToBubble(),HPos.RIGHT);
                //chat.addRow(i, chatMessage.getToBubble());
       //         chat.setAlignment(Pos.TOP_LEFT);
//              chat.setColumnHalignment(HPos.LEFT);
 //               chat.getChildren().add(chatMessage.getToBubble());
                chat.addRow(i, chatMessage.getToBubble());
            }
        }




      //  scene.getStylesheets().add(getClass().getResource("Test.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
