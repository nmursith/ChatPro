package Controller;

import Model.BindOperator;
import javafx.event.ActionEvent;

import java.io.IOException;

/**
 * Created by mmursith on 12/19/2015.
 */
public class SeparatorController {
    private BindOperator bindOperator;


    public static void main(String [] args) throws IOException {
    //    System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
//        URL url = new URL("https://www.ietf.org/rfc/rfc2560.txt");
//        Scanner s = new Scanner(url.openStream());
//
//        System.out.println(s.next());
    }

    public void showHistory(ActionEvent actionEvent) {
        bindOperator.getChatHolder().getChildren().remove(0);
        if(bindOperator.getOldchatHolder()!=null){
//            for (Node node : bindOperator.getChatHolder().getChildren()) {
//                //System.out.print("index:    "+ GridPane.getRowIndex(node)+"     ");
//                GridPane.setRowIndex(node, GridPane.getRowIndex(node)+1);
//                //System.out.println(GridPane.getRowIndex(node));
//            }
            bindOperator.getChatHolder().addRow(0, bindOperator.getOldchatHolder());
        }
        System.out.println("show history");

    }

    public BindOperator getBindOperator() {
        return bindOperator;
    }

    public void setBindOperator(BindOperator bindOperator) {
        this.bindOperator = bindOperator;
    }
}


