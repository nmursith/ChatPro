package Controller;

/**
 * Created by mmursith on 12/19/2015.
 */
public class VariablesSettingController {
}


/*
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FxTableViewExample2 extends Application {

    private TableView table;
    private ObservableList data;
    private Text actionStatus;

    public static void main(String [] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Table View Example 2");

        // Books label
        Label label = new Label("Books");
        label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));
        HBox labelHb = new HBox();
        labelHb.setAlignment(Pos.CENTER);
        labelHb.getChildren().add(label);

        // Table view, data, columns and properties
        table = new TableView();
        data = getInitialTableData();
        table.setItems(data);
        table.setEditable(true);

        TableColumn titleCol = new TableColumn("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titleCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent t) {
                ((Book) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                ).setTitle(t.getNewValue());
            }
        });

        TableColumn authorCol = new TableColumn("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory("author"));
        authorCol.setCellFactory(TextFieldTableCell.forTableColumn());
        authorCol.setOnEditCommit(new EventHandler<CellEditEvent>() {
             @Override
             public void handle(CellEditEvent t) {

                ((Book) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                ).setAuthor(t.getNewValue());
             }
        });

        table.getColumns().setAll(titleCol, authorCol);
        table.setPrefWidth(450);
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedIndexProperty().addListener(
            new RowSelectChangeListener());

        // Add and delete buttons
        Button addbtn = new Button("Add");
        addbtn.setOnAction(new AddButtonListener());
        Button delbtn = new Button("Delete");
        delbtn.setOnAction(new DeleteButtonListener());
        HBox buttonHb = new HBox(10);
        buttonHb.setAlignment(Pos.CENTER);
        buttonHb.getChildren().addAll(addbtn, delbtn);

        // Status message text
        actionStatus = new Text();
        actionStatus.setFill(Color.FIREBRICK);

        // Vbox
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));;
        vbox.getChildren().addAll(labelHb, table, buttonHb, actionStatus);

        // Scene
        Scene scene = new Scene(vbox, 500, 550); // w x h
        primaryStage.setScene(scene);
        primaryStage.show();

        // Select the first row
        table.getSelectionModel().select(0);
        Book book = table.getSelectionModel().getSelectedItem();
        actionStatus.setText(book.toString());

    } // start()

    private class RowSelectChangeListener implements ChangeListener {

        @Override
        public void changed(ObservableValue ov,
                Number oldVal, Number newVal) {

            int ix = newVal.intValue();

            if ((ix = data.size())) {

                return; // invalid data
            }

            Book book = data.get(ix);
            actionStatus.setText(book.toString());
        }
    }

    private ObservableList getInitialTableData() {

        List list = new ArrayList();

        list.add(new Book("The Thief", "Fuminori Nakamura"));
        list.add(new Book("Of Human Bondage", "Somerset Maugham"));
        list.add(new Book("The Bluest Eye", "Toni Morrison"));
        list.add(new Book("I Am Ok You Are Ok", "Thomas Harris"));
        list.add(new Book("Magnificent Obsession", "Lloyd C Douglas"));
        list.add(new Book("100 Years of Solitude", "Gabriel Garcia Marquez"));
        list.add(new Book("What the Dog Saw", "Malcolm Gladwell"));

        ObservableList data = FXCollections.observableList(list);

        return data;
    }

    private class AddButtonListener implements EventHandler {

        @Override
        public void handle(ActionEvent e) {

            // Create a new row after last row
            Book book = new Book("...", "...");
            data.add(book);
            int row = data.size() - 1;

            // Select the new row
            table.requestFocus();
            table.getSelectionModel().select(row);
            table.getFocusModel().focus(row);

            actionStatus.setText("New book: Enter title and author. Press .");
        }
    }

    private class DeleteButtonListener implements EventHandler {

        @Override
        public void handle(ActionEvent e) {

            // Get selected row and delete
            int ix = table.getSelectionModel().getSelectedIndex();
            Book book = (Book) table.getSelectionModel().getSelectedItem();
            data.remove(ix);
            actionStatus.setText("Deleted: " + book.toString());

            // Select a row

            if (table.getItems().size() == 0) {

                actionStatus.setText("No data in table !");
                return;
            }

            if (ix != 0) {

                ix = ix -1;
            }

            table.requestFocus();
            table.getSelectionModel().select(ix);
            table.getFocusModel().focus(ix);
        }
    }
}


 */