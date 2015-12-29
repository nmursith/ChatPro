package Model;

import Controller.ChatController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * Created by PPNPERERA on 11/24/2015.
 */
public class UserItem extends GridPane implements   Runnable, EventHandler<MouseEvent> {



    private ImageView userImage;
    private volatile Label thumbUserName;
    private Rectangle statusBar;
    private Button closeButton;
    private Image thumbImage;
    private User user;
    private ChatController chatController;
    private static UserItem userItem;  //addded latest
    private volatile Boolean running;
    private Thread blink;
    public UserItem(User user, ChatController controller){

        this.user = user;
        this.chatController = controller;
        this.setPrefSize(210.2,35);
        this.setMaxWidth(210.2);

        this.setHgap(8);
        this.setStyle("-fx-background-color:transparent; -fx-border-color:transparent;");
        userItem = this;                //


        thumbImage = new Image(getClass().getResourceAsStream("dummyImage.png"));
        userImage = new ImageView();
        userImage.setFitHeight(30);
        userImage.setFitWidth(30);
        userImage.setImage(thumbImage);
        userImage.setStyle("-fx-padding:2px;-fx-border-radius:30px; -fx-background-radius:30px;");
        GridPane.setHalignment(userImage, HPos.RIGHT );

        thumbUserName = new Label();
        thumbUserName.setText(user.getUserName());
        thumbUserName.setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold;");
        //thumbUserName.setStyle("-fx-text-color:#fff; -fx-font-size:11px");
        //thumbUserName.setPrefSize(119, 20); //119,20
        thumbUserName.setFont(Font.font(null, FontWeight.BOLD, 14));
        thumbUserName.setMaxWidth(130);
        thumbUserName.setPrefWidth(130);
        GridPane.setHalignment(thumbUserName, HPos.CENTER );


//        ImageView  close = new ImageView(new Image(getClass().getResourceAsStream("close.png")));
//        close.setFitHeight(20);
//        close.setFitWidth(20);
//
//        closeButton = new Button("",close);
//
//        closeButton.setMaxHeight(15);
//        closeButton.setMaxWidth(15);
//        closeButton.setStyle("-fx-background-radius: 100; -fx-border-radius: 100; -fx-background-color: transparent;-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-background-insets: 0 0 0 0, 0, 0, 0;");
//        GridPane.setHalignment(closeButton, HPos.LEFT );
//        closeButton.setOnAction(this);
      // = new Image(new Label(getClass().getResourceAsStream("closeButton.png"))); //();

        ImageView close = new ImageView(new Image(getClass().getResourceAsStream("close.png")));
        close.setFitHeight(9);
        close.setFitWidth(9);
        Label closeLabel = new Label("", close);
        closeLabel.setOnMouseClicked(this);

        this.addRow(0,userImage);
        this.addRow(0,thumbUserName);
        this.addRow(0,closeLabel);





        this.setOnMouseClicked(event -> {
        //    System.out.println("clicked");
               // blink.interrupt();
     //           stop();
               // blink = null;
       //         System.out.println("stopping");
            this.thumbUserName.setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; ");

            //blink = new Blink();

        });

   //   System.out.println("focueed:  "+this.isFocused());

    }

    public void startBlink(){
        blink = null;
        blink = new Thread(this, user.getUserName());
        blink.start ();

    }






    @Override
    public void run() {

        for (int i = 0; i < 3; i++) {
            this.thumbUserName.setStyle("-fx-text-fill:#ffa500; -fx-font-size:12px; -fx-font-weight:bold;");
            try {
                blink.sleep(250);
            } catch (InterruptedException e) {

            }
            this.thumbUserName.setStyle("-fx-text-fill: #1e90ff; -fx-font-size:12px; -fx-font-weight:bold;");
            try {
                blink.sleep(250);
            } catch (InterruptedException e) {
                ///  e.printStackTrace();

            }
        //    System.out.println("Loop: " + running);
        }

        if(chatController.getChatUsersList().getSelectionModel().getSelectedItem().equals(userItem) && userItem!=null) {

            this.thumbUserName.setStyle("-fx-text-fill:#696969; -fx-font-size:12px; -fx-font-weight:bold; ");
        }
        else
            this.thumbUserName.setStyle("-fx-text-fill:#ffa500; -fx-font-size:12px; -fx-font-weight:bold;");


        try {
            blink.sleep(250);
        } catch (InterruptedException e) {
            ///  e.printStackTrace();

        }



    }
    public ImageView getUserImage() {
        return userImage;
    }

    public void setUserImage(ImageView userImage) {
        this.userImage = userImage;
    }

    public Label getThumbUserName() {
        return thumbUserName;
    }

    public void setThumbUserName(Label thumbUserName) {
        this.thumbUserName = thumbUserName;
    }

    public Rectangle getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(Rectangle statusBar) {
        this.statusBar = statusBar;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(Button closeButton) {
        this.closeButton = closeButton;
    }

    public Image getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(Image thumbImage) {
        this.thumbImage = thumbImage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }


    @Override
    public void handle(MouseEvent event) {
        final UserItem userItem = this;


        chatController.setUsername(userItem);
        Platform.runLater(() -> {
            try {
                chatController.closeChat(userItem);
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        //chatController.closeChat((UserItem)event.getTarget());
    }
}
