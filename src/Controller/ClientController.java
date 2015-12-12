package Controller;

import Model.ChatMessage;
import Model.User;

import javax.jms.JMSException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mmursith on 11/24/2015.
 */
public class ClientController  {

    protected User user;
    private Queue<ChatMessage>  chatMessagess= new LinkedList<>();

    public ClientController(User user) throws JMSException {
        setUser(user);


    }

    public ClientController(String clientId, String topicName) throws JMSException {
        user = new User(clientId, topicName);


    }

    public ClientController(String clientId, String topicName, String subscriptionName) throws JMSException{
        user = new User(clientId,topicName, subscriptionName);




    }
    public ClientController(){

    }


    public Queue<ChatMessage> getMessage() throws JMSException {


        return this.chatMessagess;
    }





    public String getUserName() {
        return user.getUserName();
    }

    public void setUserName(String userName) {
        user.setUserName(userName);
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
    }

    public String getLastName() {
        return user.getLastName();
    }

    public void setLastName(String lastName) {
        user.setLastName(lastName);
    }



    public String getTopicName() {
        return user.getTopicName();
    }

    public void setTopicName(String topicName) {
        user.setTopicName(topicName);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSubscriptionName(String subscriptionName) {
        user.setSubscriptionName(subscriptionName);
    }

    public String getSubscriptionName() {
        return user.getSubscriptionName();
    }



}
