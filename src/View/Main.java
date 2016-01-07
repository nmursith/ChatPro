package View;

import Controller.ChatController;
import Controller.NotificationController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.jms.JMSException;

public class Main extends Application {
    /*one default variable needs to be added*/
    @Override
    public void start(Stage primaryStage) throws Exception{
      //  AquaFx.style();


        //Parent root = FXMLLoader.load(getClass().getResource("Operator.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Operator.fxml"));
        Parent root = fxmlLoader.load();

        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);

        Scene scene = new Scene(root);//, 550, 605);
        scene.getStylesheets().add(getClass().getResource("theme.css").toExternalForm());

        System.out.println("ChatController Starting");
        ChatController chatController = fxmlLoader.<ChatController>getController();
        System.out.println("ChatController Started");

        chatController.setScene(scene, primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    chatController.closeAllConnections();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(NotificationController.stage.isShowing())
                    NotificationController.stage.close();
            }
        });
        Image ico = new Image(getClass().getResourceAsStream("appIcon.png"));
        primaryStage.getIcons().add(ico);


        primaryStage.setTitle("vAssistant");
        primaryStage.setScene(scene);
        System.out.println("show");
        //FlatterFX.style();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.setResizable(false);




    }


    public static void main(String[] args) {
        System.out.println("main");
        launch(args);
    }
}
