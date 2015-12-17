package Controller;


import Model.UserItem;
import javafx.animation.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Created by dwijewardana on 12/13/2015.
 */
public class NotificationController {


    private volatile static ChatController chatController;
    private volatile static  int index;
    private static final Stage stage = new Stage(StageStyle.UNDECORATED);
    private static Popup createPopup(final String message, final String userName) {

        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);


        Label user_name = new Label(userName);
        user_name.setPrefWidth(200);
        user_name.setPrefHeight(20);
        user_name.setWrapText(true);
        user_name.relocate(65,10);
        user_name.setTextFill(Color.WHITE);
        user_name.setStyle("-fx-font-size: 16px; -fx-font-weight: BOLD; -fx-background-image: url('Background.png')");
        user_name.setOnMouseClicked(event -> {
            System.out.println("user name clicked");
            // ADD CODE TO FOCUS TO THE APPLICATION
        });

        Image image = new Image(NotificationController.class.getResourceAsStream("Background.png"));
        Label user_pic = new Label("", new ImageView(image));
        user_pic.relocate(3,10);
        user_pic.setOnMouseClicked(event -> {


            System.out.println("user pic clicked");
            // ADD CODE TO FOCUS TO THE APPLICATION
        });

        Label label = new Label(message);
        label.setPrefWidth(210);
        label.setPrefHeight(20);
        label.setWrapText(true);
        label.relocate(65,40);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: BOLD;");
        label.setOnMouseClicked(event -> {
            System.out.println(" message clicked");
            // ADD CODE TO FOCUS TO THE APPLICATION
        });

        Image image1 = new Image(NotificationController.class.getResourceAsStream("close.png"));
        ImageView closeView = new ImageView(image1);
        closeView.setFitHeight(12);
        closeView.setFitWidth(12);
        Label closeLabel = new Label("", closeView);
        closeLabel.setMaxSize(5,5);
        closeLabel.setLayoutX(270);
        closeLabel.setLayoutY(0);
        closeLabel.setOnMouseClicked(event -> {
            //boolean s = event.getSource().equals(closeLabel);
            System.out.println("close button clicked");
            //Platform.exit();
        });

        popup.getContent().add(closeLabel);
        popup.getContent().add(user_pic);
        popup.getContent().add(label);
        popup.getContent().add(user_name);
        return popup;
    }

    private static void showPopupMessage(final String message,  Stage stage,  String userName) {

        Popup popup = createPopup(message, userName);

        popup.setOnShown(e -> {
            popup.setX(stage.getX()+10);
            popup.setY(stage.getY()+5);
            //stage.getScene().setFill(Color.valueOf("#f4f4f4"));
            stage.getScene().setFill(Color.valueOf("#0B3861"));
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            //PauseTransition delay1 = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished( event -> stage.close() );
            //delay1.setOnFinished( event -> stage.getScene().setFill(Color.valueOf("#00AFF0")));
            //delay1.setOnFinished( event -> stage.getScene().setFill(Color.valueOf("#708090")));
            delay.play();
            //delay1.play();
        });

        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            //cnt--;
            ///       System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            //////////////////
            notifyLocations();
            stage.close();
        });


        popup.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
/////////////////
            notifyLocations();
            //cnt--;
            //System.out.println("$$$$$$$$$$$$$$$iiii$$$$$$$$$$$$$");
            stage.close();
        });


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 300);
        stage.setY(primaryScreenBounds.getMinY() + 25);//primaryScreenBounds.getHeight() - 85);// -((cnt-1)*82));
        //position[cnt-1]=true;
        stage.setAlwaysOnTop(true);
        /*if(!stage.isShowing()){
            System.out.println("###########");
            stage.show();
        }else{
            System.out.println("@@@@@@@@@@@@@@");
            stage.toFront();
        }*/
        popup.show(stage);

        Interpolator bugFixInterpolator = new Interpolator() {
            @Override
            protected double curve(double t) {
                return t;
            }

            @Override
            public String toString() {
                return "Interpolator.LINEAR";
            }
        };

        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(popup.opacityProperty(), 1, bugFixInterpolator)),
                new KeyFrame(Duration.millis(600), new KeyValue(popup.opacityProperty(), 0, bugFixInterpolator))
        );
        t.setAutoReverse(true);
        t.setCycleCount(4);
        t.playFromStart();
    }

    private static void notifyLocations(){
        UserItem userItem = chatController.getChatUsersList().getItems().get(getIndex());
        chatController.setUsername(userItem);
        chatController.getChatUsersList().getSelectionModel().select(getIndex());
        chatController.getStage().setIconified(false);
        chatController.getStage().requestFocus();

    }

    public static synchronized  void getNotification(String message, String userName, ChatController controller, int index){
        chatController =controller;
        setIndex(index);
        NotificationController.index = index;

        stage.close();
        stage.setTitle("Notification");
        //stage.initStyle(StageStyle.UNDECORATED);
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: TRANSPARENT; -fx-border-color: #0B3861");
        Scene scene = new Scene(root, 300, 80, Color.valueOf("#0B3861"));
        stage.setScene(scene);

        //stage.initOwner(stageINIT);
        stage.show();
        /*Interpolator bugFixInterpolator = new Interpolator() {
            @Override
            protected double curve(double t) {
                return t;
            }

            @Override
            public String toString() {
                return "Interpolator.LINEAR";
            }
        };

        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(stage.opacityProperty(), 1, bugFixInterpolator)),
                new KeyFrame(Duration.millis(600), new KeyValue(stage.opacityProperty(), 0, bugFixInterpolator))
        );
        t.setAutoReverse(true);
        t.setCycleCount(4);
        t.playFromStart();*/
        //stage.getIcons().
        //stage.getIcons().add(null);
        //stage.initStyle(StageStyle.UTILITY);
        showPopupMessage(message, stage, userName);
    }

    private static int getIndex() {
        return index;
    }

    private static void setIndex(int index) {
        NotificationController.index = index;
    }
}
