package Controller;

import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellResponse;

/**
 * Created by mmursith on 1/11/2016.
 */

public class SpellCheckController  {




    public static void main(String[] args) {
    //    Application.launch(args);



//
//
        SpellChecker checker = new SpellChecker();
        checker.setOverHttps(false);
        SpellResponse spellResponse = checker.check( "helloo worlrd" );

        for( SpellCorrection sc : spellResponse.getCorrections() )
            System.out.println( sc.getValue() );
    }
//    @Override
//    public void start(Stage stage) {
//        final TextArea text = new TextArea("Here asd is some textz to highlight");
//
//        text.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 20px;");
//        text.setEditable(false);
//        text.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent t) { t.consume(); }
//        });
//
//        stage.setScene(new Scene(text));
//        stage.show();
//
//        Platform.runLater(new Runnable() {
//            @Override public void run() { text.selectRange(13, 18); }
//        });
//    }
}