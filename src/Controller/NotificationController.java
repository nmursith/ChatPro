package Controller;


import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

/**
 * Created by dwijewardana on 12/13/2015.
 */
public class NotificationController {



    public static Popup createPopup(final String message, final String userName) {

        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);

        String usernamelabel = userName;
        Label user_name = new Label(usernamelabel);
        user_name.setPrefWidth(200);
        user_name.setPrefHeight(20);
        user_name.setWrapText(true);
        user_name.relocate(65,10);
        user_name.setTextFill(Color.BLACK);
        user_name.setStyle("-fx-font-size: 16px; -fx-font-weight: BOLD; -fx-background-image: url('Background.png')");
        user_name.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("user name clicked");
                // ADD CODE TO FOCUS TO THE APPLICATION
            }
        });

        Image image = new Image(NotificationController.class.getResourceAsStream("Background.png"));
        Label user_pic = new Label("", new ImageView(image));
        user_pic.relocate(3,10);
        user_pic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                System.out.println("user pic clicked");
                // ADD CODE TO FOCUS TO THE APPLICATION
            }
        });

        Label label = new Label(message);
        label.setPrefWidth(210);
        label.setPrefHeight(20);
        label.setWrapText(true);
        label.relocate(65,40);
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: BOLD;");
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(" message clicked");
                // ADD CODE TO FOCUS TO THE APPLICATION
            }
        });

        Image image1 = new Image(NotificationController.class.getResourceAsStream("close.png"));
        ImageView closeView = new ImageView(image1);
        closeView.setFitHeight(12);
        closeView.setFitWidth(12);
        Label closeLabel = new Label("", closeView);
        closeLabel.setMaxSize(5,5);
        closeLabel.setLayoutX(270);
        closeLabel.setLayoutY(0);
        closeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //boolean s = event.getSource().equals(closeLabel);
                System.out.println("close button clicked");
                Platform.exit();
            }
        });

        popup.getContent().add(closeLabel);
        popup.getContent().add(user_pic);
        popup.getContent().add(label);
        popup.getContent().add(user_name);
        return popup;
    }

    public static void showPopupMessage(final String message, final Stage stage, final String userName) {

        Popup popup = createPopup(message, userName);

        popup.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                popup.setX(stage.getX()+10);
                popup.setY(stage.getY()+5);
                //stage.getScene().setFill(Color.valueOf("#f4f4f4"));
                stage.getScene().setFill(Color.valueOf("#00AFF0"));
                PauseTransition delay = new PauseTransition(Duration.seconds(10));
                //PauseTransition delay1 = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished( event -> stage.close() );
                //delay1.setOnFinished( event -> stage.getScene().setFill(Color.valueOf("#00AFF0")));
                //delay1.setOnFinished( event -> stage.getScene().setFill(Color.valueOf("#708090")));
                delay.play();
                //delay1.play();
            }
        });

        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //cnt--;
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                stage.close();
            }
        });


        popup.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                //cnt--;
                //System.out.println("$$$$$$$$$$$$$$$iiii$$$$$$$$$$$$$");
                stage.close();
            }
        });


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 305);
        stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 85);// -((cnt-1)*82));
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
        t.setCycleCount(6);
        t.playFromStart();
    }



    public static void getNotification(String message, String userName){

        stage.close();
        stage.setTitle("Notification");
        //stage.initStyle(StageStyle.UNDECORATED);
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: TRANSPARENT; -fx-border-color: #00AFF0");
        Scene scene = new Scene(root, 300, 80, Color.valueOf("#00AFF0"));
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

    private static Stage stage = new Stage(StageStyle.UNDECORATED);
}
