package Model;

import javax.jms.JMSException;

/**
 * Created by mmursith on 11/24/2015.
 */
public class Client {

    private String userName;
    private String firstName;
    private String lastName;
    private String subscriptionName;

    private String clientId;
    private String topicName;


    private Operator operator;

    public Client(){

    }

    public Client(String clientId, String topicName) throws JMSException {
        this.clientId = clientId;
        this.topicName = topicName;



    }

    public Client(String clientId, String topicName,String subscriptionName) throws JMSException {
        this.clientId = clientId;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }



    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }



}
