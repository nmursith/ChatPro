package sample;/**
 * Created by mmursith on 12/4/2015.
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import rumorsapp.BubbleSpec;

public class Chat extends Application {

//    @Override
//    public void start(Stage primaryStage) {
//
//        GridPane chat = new GridPane();
//        chat.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//
//        ColumnConstraints c1 = new ColumnConstraints();
//        c1.setPercentWidth(100);
//        chat.getColumnConstraints().add(c1);
//
//        for (int i = 0; i < 20; i++) {
//            Label chatMessage = new Label("Hi " + i);
//            chatMessage.getStyleClass().add("chat-bubble");
////            GridPane.setHalignment(chatMessage, i % 2 == 0 ? HPos.LEFT
////                    : HPos.RIGHT);
//            chat.addRow(i, chatMessage);
//        }
//
//        ScrollPane scroll = new ScrollPane(chat);
//        scroll.setFitToWidth(true);
//
//        Scene scene = new Scene(scroll, 500, 500);
//        scene.getStylesheets().add(getClass().getResource("Test.css").toExternalForm());
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Group root = new Group();
            Scene scene = new Scene(root,400,400);

    //        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            Pane p = new Pane();
            p.setPrefSize(400, 400);
//            p.setBackground(new Background(new BackgroundFill(Color.GOLD,                    null, null)));
            root.getChildren().add(p);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Conversation about Bubbles with Elltz");
            primaryStage.show();
            Label bl1 = new Label(BubbleSpec.FACE_LEFT_CENTER.toString());
            bl1.relocate(10, 50);
            bl1.setText("Hi Elltz -:)\n sure");
            //bl1.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN,                    null, null)));


            Label bl2 = new Label(BubbleSpec.FACE_RIGHT_CENTER.toString());
            bl2.relocate(310, 100);
            bl2.setText("Heloooo Me");
            //bl2.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,                    null, null)));

            Label bl3 = new Label(BubbleSpec.FACE_LEFT_CENTER.toString());

            bl3.relocate(10, 150);
            bl3.setText("you know this would be a nice library");
            //bl3.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN,                    null, null)));


            Label bl4 = new Label(BubbleSpec.FACE_RIGHT_CENTER.toString());
            bl4.relocate(165, 200);
            bl4.setText("uhmm yea, kinda, but yknow,im tryna \nact like im not impressed");
            //bl4.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,                    null, null)));

            Label bl5 = new Label(BubbleSpec.FACE_LEFT_CENTER.toString());
            bl5.relocate(10, 250);
            bl5.setText("yea! yea! i see that, lowkey.. you not gonna\n get upvotes though..lmao");
            //bl5.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN,                    null, null)));


            Label bl6 = new Label(BubbleSpec.FACE_RIGHT_CENTER.toString());
            bl6.relocate(165, 300);
            bl6.setText("Man! shut up!!.. what you know about\n upvotes.");
            //bl6.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,                    null, null)));


            p.getChildren().addAll(bl1, bl2, bl3, bl4, bl5, bl6);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("main");
        launch(args);
    }



}
