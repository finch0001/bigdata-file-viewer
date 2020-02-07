package org.eugene.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.avro.generic.GenericData;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private Stage stage;
    private VBox vBox;
    private TableView<List<StringProperty>> tableView;
    private Pagination pagination;

    public Table(Stage stage){
        this.stage = stage;
    }

    public void setVBox(VBox vBox){
        this.vBox = vBox;
    }

    public void initTable(){
        if (tableView != null)
            vBox.getChildren().remove(tableView);
        if (pagination != null)
            vBox.getChildren().remove(pagination);
        tableView = new TableView();
        pagination = new Pagination();
        tableView.prefHeightProperty().bind(stage.heightProperty());
        tableView.prefWidthProperty().bind(stage.widthProperty());
        vBox.getChildren().add(tableView);
        vBox.getChildren().add(pagination);
    }



    public void refresh(List<String> showingList, List<String> propertyList, int rowNumber, int columnNumber, List<GenericData.Record> data){
        initTable();

        int index = 0;
        for (String property: propertyList){
            if (!showingList.contains(property)){
                continue;
            }
            TableColumn<List<StringProperty>, String> tableColumn = new TableColumn<>(property);
            int finalIndex = index;
            tableColumn.setCellValueFactory(colData -> colData.getValue().get(finalIndex));
            tableView.getColumns().add(tableColumn);
            index++;
        }

        int pageCount = rowNumber / Constants.MAX_ROW_NUM + 1;
        if (rowNumber % Constants.MAX_ROW_NUM == 0) {
            pageCount--;
        }
        int colNumber = propertyList.size();
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(10);

        pagination.setPageFactory((pageIndex) -> {
            generatePage(data, tableView, pageIndex, Constants.MAX_ROW_NUM, colNumber, showingList, propertyList);
            VBox vbox = new VBox();
            vbox.getChildren().add(tableView);
            return vbox;
        });
    }

    private void generatePage(List<GenericData.Record> list, TableView tableView, int pageIndex, int pageRowNum, int colNumber, List<String> showingList, List<String> propertyList){
        ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
        int start = pageIndex * pageRowNum;
        int end = start + pageRowNum;
        if (end > list.size()){
            end = list.size();
        }
        for (int i = start; i < end; i++) {
            GenericData.Record r = list.get(i);
            List<StringProperty> row = new ArrayList<StringProperty>();
            int index = 0;
            for (int j = 0; j < colNumber; j++){
                if(showingList.contains(propertyList.get(j))){
                    if (r.get(j) == null){
                        row.add(index, new SimpleStringProperty("NULL"));
                    }
                    else{
                        row.add(index, new SimpleStringProperty(r.get(j).toString()));
                    }
                    index++;
                }
            }
            data.add(row);
        }
        tableView.setItems(data);
    }
}