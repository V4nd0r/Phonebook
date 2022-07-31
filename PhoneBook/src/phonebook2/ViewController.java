
package phonebook2;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class ViewController implements Initializable {
    
//<editor-fold defaultstate="collapsed" desc="FXML annotációk">
    @FXML
    AnchorPane anchor;
    @FXML
    SplitPane mainSplit;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    TableView table;
    @FXML
    TextField inputLastName;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    TextField inputExport;
    @FXML
    Button addNewContactButton;
    @FXML
    Button exportButton;  
    @FXML
    Label exportedLabel;
    
//</editor-fold>
    
    
//<editor-fold defaultstate="collapsed" desc="Osztályváltozók">
    private final String MENU_CONTACTS="Kontaktok";
    private final String MENU_LIST="Lista";
    private final String MENU_EXPORT="Exportálás";
    private final String MENU_EXIT="Kilépés";
    
//</editor-fold>
    DB db = new DB();
    
    private final ObservableList<Person> data =
    FXCollections.observableArrayList();  
    
 //Új kapcsolat hozzáadása
    @FXML
    private void addContact(ActionEvent event){  
        String email =inputEmail.getText();
        if (email.length() > 3 && email.contains("@") && email.contains(".")){
        Person newPerson = new Person(inputLastName.getText(), inputFirstName.getText(), email);
        data.add(newPerson);
        db.addContact(newPerson);
        inputLastName.clear();
        inputFirstName.clear();
        inputEmail.clear();
        }
        else{
        alert("Hibás e-mail formátum.");
        }
    }
    
 //PDF-dokumentum lementése
        @FXML
    private void exportList(ActionEvent event){  
        String fileName =inputExport.getText();
        fileName = fileName.replaceAll("//s", "");//Ezzel eltávolítom a whitespaceket a fileNameből, hogy ne tudjon csak whitespaceket beírni
        if (fileName != null && !fileName.equals("")){
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data); 
            inputExport.clear();
            exportedLabel.setVisible(true);
        }
        else{ 
            alert("Fájlnév megadása kötelező!");
        }
    }

 //Táblázat megjelenítése és módosítása
    public void setTableData(){
//Vezetéknév-oszlop beállítása
        TableColumn lastNameCol = new TableColumn("Vezetéknév");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory <Person, String>("lastName"));
        
        //Vezetéknév-oszlop módosíthatósága
        lastNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t) {
                    Person actualPerson =(Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                    actualPerson.setLastName(t.getNewValue());
                    db.updateContact(actualPerson);
                }
            }
        );
        
        //Keresztnév-oszlop beállítása
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory <Person, String>("firstName"));
        
        //Keresztnév-oszlop módosíthatósága
        firstNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t) {
                    Person actualPerson =(Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                    actualPerson.setFirstName(t.getNewValue());
                    db.updateContact(actualPerson);
                }
            }
        );
        
        //E-mail-oszlop beállítása
        TableColumn emailCol = new TableColumn("E-mail");
        emailCol.setMinWidth(200);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory <Person, String>("email"));
        
        //E-mail-oszlop módosíthatósága
        emailCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t) {
                    Person actualPerson =(Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                    actualPerson.setEmail(t.getNewValue());
                    db.updateContact(actualPerson);
                }
            }
        );
        
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol);
        data.addAll(db.getAllContacts());
        table.setItems(data);
}

//Bal oldali menüsáv megjelenítése    
    private void setMenuData() {
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menü");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);
        
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);
        
        nodeItemA.setExpanded(true);
        
        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));
        
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST, contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT,exportNode);
        
        nodeItemA.getChildren().addAll(nodeItemA1,nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA,nodeItemB);
        menuPane.getChildren().add(treeView);
        
        
        //Menüpontok interaktivitésa
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue){
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu;
                selectedMenu = selectedItem.getValue();
                //System.out.println("A " + selectedMenu +" -ra kattintottál. ");
                
                if(null != selectedMenu){
                    switch(selectedMenu){
                    case MENU_CONTACTS:
                            try {selectedItem.setExpanded(true);
                            }catch (Exception ex){
                            }
                            break;
                            
                    case MENU_LIST:
                        contactPane.setVisible(true);
                        exportPane.setVisible(false);
                        break;
                        
                    case MENU_EXPORT:
                        contactPane.setVisible(false);
                        exportPane.setVisible(true);
                        break;
                        
                    case MENU_EXIT:
                        System.exit(0);
                        break;
                    }
                }
            }
        });
    }
     
//Hibaüzenetek megjelenítése
    private void alert(String text){
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.5);
        
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label, alertButton);
        vbox.setAlignment(Pos.CENTER);
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 300.0);
        anchor.setLeftAnchor(vbox, 300.0);
        
        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vbox.setVisible(false);
            }
        });
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
       }    

}
