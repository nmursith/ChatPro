package Controller;

import Model.ChatMessage;
import Model.Client;

import javax.jms.JMSException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mmursith on 11/24/2015.
 */
public class ClientController  {

    protected Client client;
    private Queue<ChatMessage>  chatMessagess= new LinkedList<>();

    public ClientController(Client client) throws JMSException {
        setClient(client);


    }

    public ClientController(String clientId, String topicName) throws JMSException {
        client = new Client(clientId, topicName);


    }

    public ClientController(String clientId, String topicName, String subscriptionName) throws JMSException{
        client = new Client(clientId,topicName, subscriptionName);




    }
    public ClientController(){

    }


    public Queue<ChatMessage> getMessage() throws JMSException {


        return this.chatMessagess;
    }





    public String getUserName() {
        return client.getUserName();
    }

    public void setUserName(String userName) {
        client.setUserName(userName);
    }

    public String getFirstName() {
        return client.getFirstName();
    }

    public void setFirstName(String firstName) {
        client.setFirstName(firstName);
    }

    public String getLastName() {
        return client.getLastName();
    }

    public void setLastName(String lastName) {
        client.setLastName(lastName);
    }

    public String getClientId() {
        return client.getClientId();
    }

    public void setClientId(String clientId) {
        client.setClientId(clientId);
    }


    public String getTopicName() {
        return client.getTopicName();
    }

    public void setTopicName(String topicName) {
        client.setTopicName(topicName);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setSubscriptionName(String subscriptionName) {
        client.setSubscriptionName(subscriptionName);
    }

    public String getSubscriptionName() {
        return client.getSubscriptionName();
    }



}
