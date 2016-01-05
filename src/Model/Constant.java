package Model;

import Controller.ConfigurationController;

import java.util.Random;

/**
 * Created by mmursith on 12/13/2015.
 */
public class Constant {
    public static final String correalationID = "60e232e2a20efb61";
    public static final String JMSmessageID = "60e232e2a20tef54";
    public static final String prefix = ConfigurationController.readConfig().getOperator();
    public static final String operatorID =  prefix+"60e232e2a20tafwe";

    public static String [] usernames = {"Operator","Pubudu", "Shannon", "Nimashi","Damith","Mursith","Januka","Prabudhika","Thuan","Sameera"};

    public static String getRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();

        return Long.toHexString(randomLong);
    }

}
