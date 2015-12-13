package Model;

import Controller.ChatController;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.jms.JMSException;

/**
 * Created by PPNPERERA on 11/24/2015.
 */
public class UserItem extends GridPane implements  EventHandler<javafx.event.ActionEvent>, Runnable {



    private ImageView userImage;
    private Label thumbUserName;
    private Rectangle statusBar;
    private Button closeButton;
    private Image thumbImage;
    private User user;
    private ChatController chatController;
    private Thread blink;        //addded lates
    private static UserItem userItem;  //addded latest
    private volatile boolean running;

    public UserItem(User user, ChatController controller){

        this.user = user;
        this.chatController = controller;
        this.setPrefSize(188,32);
        setHgap(5);
        setVgap(2);
        userItem = this;                //


        thumbImage = new Image(getClass().getResourceAsStream("dummyImage.jpg"));
        userImage = new ImageView();
        userImage.setFitHeight(30);
        userImage.setFitWidth(30);
        userImage.setImage(thumbImage);
        userImage.setStyle("-fx-padding:2px;-fx-border-radius:30px; -fx-background-radius:30px;");
        GridPane.setHalignment(userImage, HPos.RIGHT );

        thumbUserName = new Label();
        thumbUserName.setText(user.getUserName());

        thumbUserName.setStyle("-fx-text-fill:#696969; -fx-font-size:11px");
        //thumbUserName.setPrefSize(119, 20); //119,20
        thumbUserName.setFont(Font.font(null, FontWeight.BOLD, 14));
        thumbUserName.setMaxWidth(105);
        thumbUserName.setPrefWidth(105);
        GridPane.setHalignment(thumbUserName, HPos.CENTER );


        ImageView  close = new ImageView(new Image(getClass().getResourceAsStream("close.png")));
        close.setFitHeight(20);
        close.setFitWidth(20);

        closeButton = new Button("",close);

        closeButton.setMaxHeight(15);
        closeButton.setMaxWidth(15);
        closeButton.setStyle("-fx-background-radius: 100; -fx-border-radius: 100; -fx-background-color: transparent;-fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-background-insets: 0 0 0 0, 0, 0, 0;");
        GridPane.setHalignment(closeButton, HPos.LEFT );
        closeButton.setOnAction(this);
      // = new Image(new Label(getClass().getResourceAsStream("closeButton.png"))); //();

        this.addRow(0,userImage);
        this.addRow(0,thumbUserName);
        this.addRow(0,closeButton);
        this.setStyle("-fx-background-color:#f0ffff;-fx-border:5px");
      //  this.addRow(1,statusBar);
     //   System.out.println(getWidth()+"     "+getHeight());

        this.setOnMouseClicked(event -> {
            System.out.println("clicked");
               // blink.interrupt();
                stop();
               // blink = null;
                System.out.println("stopping");
            this.setStyle("-fx-background-color:#f0ffff;-fx-border:5px");
            //blink = new Blink();

        });

    }

    public void startBlink(){
        System.out.println("invoked");
        System.out.println("Starting "  );
        running = true;
        blink = new Thread (this, user.getUserName());
        blink.start ();

    }



    public void stop(){
        running = false;


    }


    @Override
    public void run() {
        while(running){
            userItem.setStyle("-fx-background-color:#00ffff;-fx-border:5px");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                // e.printStackTrace();
                running = false;
            }
            userItem.setStyle("-fx-background-color:#fff8dc;-fx-border:5px");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                ///  e.printStackTrace();
                running = false;
            }

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
    public void handle(javafx.event.ActionEvent event) {
        try {

            chatController.closeChat(this);
            //chatController.closeChat((UserItem)event.getTarget());
        } catch (JMSException e) {
            System.out.println("problem");
            //e.printStackTrace();
        }

    }




}
