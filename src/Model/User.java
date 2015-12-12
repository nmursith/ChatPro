package Model;

import Controller.ChatController;

import javax.jms.JMSException;

/**
 * Created by mmursith on 11/24/2015.
 */
public class User {

    private String userName;
    private String firstName;
    private String lastName;
    private String subscriptionName;

    private String userId;
    private String topicName;




    public User(){

    }

    public User(String userId, String topicName) throws JMSException {
        this.userId = userId;
        this.topicName = topicName;



    }

    public User(String userId, String topicName, String subscriptionName) throws JMSException {
        this.userId = userId;
        this.topicName = topicName;
        this.subscriptionName=subscriptionName;


    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }


    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }


    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }



}
