//package sample;
//
///**
// * Created by mmursith on 12/11/2015.
// */
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import org.controlsfx.control.Notifications;
//
//public class notification extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//    }
//
//
//    public static void main(String[] args) {
//        new JFXPanel();
//        notifier("Good!", "It's working now!");
//    }
//
//    private static void notifier(String pTitle, String pMessage) {
//        Platform.runLater(() -> {
//                    Stage owner = new Stage(StageStyle.TRANSPARENT);
//                    StackPane root = new StackPane();
//                    root.setStyle("-fx-background-color: TRANSPARENT");
//                    Scene scene = new Scene(root, 1, 1);
//                    scene.setFill(Color.TRANSPARENT);
//                    owner.setScene(scene);
//                    owner.setWidth(1);
//                    owner.setHeight(1);
//                    owner.toBack();
//                    owner.show();
//                    Notifications.create().title(pTitle).text(pMessage).showInformation();
//                }
//        );
//    }
//}
//
////public class notification {
////
//////    public  static  void main(String []args){
//////
//////        final Stage newConnDialog = new Stage();
//////        newConnDialog.initStyle(StageStyle.UNDECORATED);
//////        newConnDialog.initModality(Modality.WINDOW_MODAL);
//////
//////        // Set pisition
//////        newConnDialog.setX(1050); //secondStage.setX(primaryStage.getX() + 250);
//////        newConnDialog.setY(150);
//////
//////        GridPane grid = new GridPane();
//////        grid.setAlignment(Pos.CENTER);
//////        grid.setHgap(5);
//////        grid.setVgap(5);
//////        grid.setPadding(new Insets(20, 20, 20, 20));
//////
//////        // text
//////        Text productName = new Text("Test");
//////        productName.setFont(Font.font("Verdana", 12));
//////        grid.add(productName, 0, 2);
//////
//////        // Configure dialog size and background color
//////        Scene aboutDialogScene = new Scene(grid, 200, 100, Color.WHITESMOKE);
//////        newConnDialog.setScene(aboutDialogScene);
//////        newConnDialog.show();
//////        /*Notification info = new Notification("Title", "Info-Message");
//////        Notifier.INSTANCE.notify(info);
//////        Notifier.INSTANCE.notifyWarning("This is a warning");*/
//////    }
////}
