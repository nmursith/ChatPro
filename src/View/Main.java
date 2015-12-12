package View;

import Controller.ChatController;
import com.guigarage.flatterfx.FlatterFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.jms.JMSException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
      //  AquaFx.style();
        FlatterFX.style();
        //Parent root = FXMLLoader.load(getClass().getResource("Operator.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Operator.fxml"));

        Parent root = fxmlLoader.load();
        ChatController chatController = fxmlLoader.<ChatController>getController();
        Scene scene = new Scene(root, 550, 605);


        chatController.setScene(scene);
        primaryStage.setTitle("Chat Desktop");
        primaryStage.setScene(scene);
        System.out.println("show");
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            try {
                chatController.closeAllConnections();
                System.exit(0);
            } catch (JMSException e) {

            }
        });



    }


    public static void main(String[] args) {
        System.out.println("main");
        launch(args);
    }
}
