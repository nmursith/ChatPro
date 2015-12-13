package sample;

import Controller.OperatorController;
import Model.ChatMessage;

import javax.jms.JMSException;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by mmursith on 11/24/2015.
 */
public class Test {
    //                                        controller.messageDisplay.setContent( bindOperator.getChatHolder());
//                                        controller.getUsername().setText(controller.getListItems().get(0).getUser().getUserName());
//                                        controller.setChatHolder(bindOperator.getChatHolder());
//                                        controller.setOperatorController(bindOperator.getOperatorController());
//                                        controller.setHistoryController(bindOperator.getHistoryController());




    public static void main(String []args) throws JMSException {
        OperatorController operatorController = new OperatorController("operator0", "chat.*"); //subscription name, topicnaem
        Scanner in  = new Scanner(System.in);
        String send = "", reply="";
        System.out.println("Started");
        Queue<ChatMessage> durablechatMessage = operatorController.getChatMessagess();
        ChatMessage chatMessage =null;

        while (!durablechatMessage.isEmpty()){
            chatMessage =durablechatMessage.remove();

            System.out.println("Offline:  "+ chatMessage.getTextMessage());
        }



        while(!reply.equals("exit")){


            chatMessage= null;
            durablechatMessage = operatorController.getChatMessagess();


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!durablechatMessage.isEmpty()) {
                while (!durablechatMessage.isEmpty()) {
  //                  System.out.println("internal");
                    try {

                        try {
                             Thread.sleep(10);
                        } catch (InterruptedException e) {
                               e.printStackTrace();
                        }
                        chatMessage =durablechatMessage.remove();
                        reply = chatMessage.getTextMessage();
                        String correID = chatMessage.getMessage().getJMSCorrelationID();





                        if( correID == null ) {
                            System.out.println("User:  " + reply);
                   //         System.out.println(chatMessage.getMessage());
                            System.out.print("Operator:   ");
                            send = in.nextLine();
                             //operatorController.sendMessage(send,operatorController);

                          try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            break;

                        }

                    }
                    catch (NullPointerException e){
                        System.out.println("Null");
                        e.printStackTrace();
                     //   break;
                    }

                }

            }




        }









    }
}
