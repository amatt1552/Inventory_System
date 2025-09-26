package inventorysystemproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * FXML ProductForm Controller class. 
 *
 * @author Austin Matthews
 */
public class ProductFormController implements Initializable 
{

    @FXML
    private Label productFormTitle; 
    @FXML
    private Pane container;
    @FXML
    private Rectangle rectangle;
    @FXML
    private TextField partSearch;
    @FXML
    private TextField productIDField;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField productInventoryField;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField productMaxField;
    @FXML
    private TextField productMinField;
    @FXML
    private TableView<Part> partTable;
    @FXML
    private TableColumn<?, ?> partID;
    @FXML
    private TableColumn<?, ?> partName;
    @FXML
    private TableColumn<?, ?> partInventoryLevel;
    @FXML
    private TableColumn<?, ?> partPrice;
    @FXML
    private TableView<Part> associatedPartTable;
    @FXML
    private TableColumn<?, ?> associatedPartID;
    @FXML
    private TableColumn<?, ?> associatedPartName;
    @FXML
    private TableColumn<?, ?> associatedPartInventoryLevel;
    @FXML
    private TableColumn<?, ?> associatedPartPrice;
    @FXML
    private Label productIDLabel;
    @FXML
    private Label productNameLabel;
    @FXML
    private Label productInventoryLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private Label productMaxLabel;
    @FXML
    private Label productMinLabel;
    @FXML
    private Label consoleLabel;
    
    private Part selectedPart;
    private ObservableList<Part> associatedPartsList = FXCollections.observableArrayList();
    private final InventoryMain.SceneLoadedListener loadComplete = () -> initializeValues(); //listens for the load complete event
    private ObservableList<Part> searchedParts = FXCollections.observableArrayList(); //for search
    private String lastSearchedPart = ""; //for search
    
    //selects searched parts while typing
    @FXML
    private void selectPartOnType(KeyEvent event) 
    {
        selectSearchedParts();
    }
    
    //shows searched parts on enter
    @FXML
    private void searchPartOnAction(ActionEvent event) 
    {
        searchParts();
    }
    
    //adds parts to product's associated parts
    @FXML
    private void addPartOnAction(ActionEvent event) 
    {
        selectedPart = partTable.getSelectionModel().getSelectedItem();
        if(selectedPart != null)
        {
            associatedPartsList.add(selectedPart);
            associatedPartTable.setItems(associatedPartsList);
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
        
    }
    
    //removes associated parts
    @FXML
    private void removeAssociatedPartOnAction(ActionEvent event) 
    {
        selectedPart = associatedPartTable.getSelectionModel().getSelectedItem();
        if(selectedPart != null)
        {
            int result = InventoryMain.displayMessageDialog("Part", "Removing Part from Product", "Are you sure you want to remove the associated part " + selectedPart.getName() + "?");
            if(result == 1)
            {
                associatedPartsList.remove(selectedPart);
                associatedPartTable.setItems(associatedPartsList);
            }
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
    }

    //adds or modifies product
    @FXML
    private void saveOnAction(ActionEvent event) 
    {
        //save
        switch(InventoryMain.getProductFormState())
        {
            case AddProduct: //adding
                
                //add the product
                addProduct();
                
                break;
            case ModifyProduct: //modifying
                
                //modify the product
                modifyProduct();
                
                break;
        }
        
    }

    //sends back to inventory form empty handed
    @FXML
    private void cancelOnAction(ActionEvent event) 
    {
        InventoryMain.setScene("InventoryManagementForm");
    }
    
    
    /**
     * Seems to be called while scene is creating. 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
        InventoryMain.addListener(loadComplete);
        //set table
        partTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //makes setting values for tables faster
        //part columns
        partID.setCellValueFactory(new PropertyValueFactory("id"));
        partName.setCellValueFactory(new PropertyValueFactory("name"));
        partInventoryLevel.setCellValueFactory(new PropertyValueFactory("stock"));
        partPrice.setCellValueFactory(new PropertyValueFactory("price"));
        //product columns
        associatedPartID.setCellValueFactory(new PropertyValueFactory("id"));
        associatedPartName.setCellValueFactory(new PropertyValueFactory("name"));
        associatedPartInventoryLevel.setCellValueFactory(new PropertyValueFactory("stock"));
        associatedPartPrice.setCellValueFactory(new PropertyValueFactory("price"));
        
    }
    
    /**
     * Meant to set values after scene is done loading. 
     */
    public void initializeValues()
    {
        
        //sets the rectangle to be the size of the pane.
        //I should probably do this in css but there is no time.
        rectangle.setLayoutX(0);
        rectangle.setLayoutY(0);
        rectangle.setWidth (container.getWidth());
        rectangle.setHeight (container.getHeight());
        //sets part table
        partTable.setItems(InventoryMain.getAllParts());
        
        switch(InventoryMain.getProductFormState())
        {
            case AddProduct:
                productFormTitle.setText("Add Product");
                break;
            case ModifyProduct:
                Product modifiedProduct = InventoryMain.modifiedProduct;
                productFormTitle.setText("Modify Product");
                //id 
                productIDField.setText("" + modifiedProduct.getId());
                //name
                productNameField.setText(modifiedProduct.getName());
                //price
                productPriceField.setText("" + modifiedProduct.getPrice());
                //stock
                productInventoryField.setText("" + modifiedProduct.getStock());
                //min
                productMinField.setText("" + modifiedProduct.getMin());
                //max
                productMaxField.setText("" + modifiedProduct.getMax());
                //associated Parts
                associatedPartsList = modifiedProduct.getAllAssociatedParts();
                break;
            default:
                break;
        }
        associatedPartTable.setItems(associatedPartsList);
    }

    //selects 
    private void selectSearchedParts()
    {
        String searchedString = partSearch.getText();
        
        //used to "reset" console
        if(!lastSearchedPart.equals(searchedString))
        {
           InventoryMain.displayMessageConsole(consoleLabel, "");
            
           lastSearchedPart = searchedString;
        }
        
        if(!searchedString.equals(""))
        {
            //find rows
            searchedParts = InventoryMain.lookupPart(searchedString);
            partTable.getSelectionModel().clearSelection();
            //sets from searched parts
            for(Part part : searchedParts)
            {
                partTable.getSelectionModel().select(part);
            }
            //PartTable.setItems(InventoryMain.lookupPart(searchedString));
        }
        else
        {
            partTable.getSelectionModel().clearSelection();
            searchedParts.clear();
           
            //PartTable.setItems(InventoryMain.getAllParts());
        }
    }
    
    //displays result
    private void searchParts()
    {
        if(searchedParts.size() > 0)
        {
            partTable.setItems(searchedParts);
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Could not find Part!");
            partTable.setItems(InventoryMain.getAllParts());
        }
    }
     
    //adds product
    private void addProduct()
    {
        Product newProduct = new Product();
        String message = ""; //this is used to diplay the errors
        //id will be set in inventory
        //name
        if(!productNameField.getText().equals(""))
        {
            newProduct.setName(productNameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        //price
        if(InventoryMain.isNumeric(productPriceField.getText()))
        {
            newProduct.setPrice(Double.parseDouble(productPriceField.getText()));
        }
        else
        {
            message += productPriceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(productInventoryField.getText()))
        {
            newProduct.setStock(Integer.parseInt(productInventoryField.getText()));
        }
        else
        {
            message += productInventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(productMinField.getText()))
        {
            
             newProduct.setMin(Integer.parseInt(productMinField.getText()));
        }
        else
        {
            message += productMinLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(productMaxField.getText()))
        {
             newProduct.setMax(Integer.parseInt(productMaxField.getText()));
        }
        else
        {
            message += productMaxLabel.getText() + " must be a number!\n";
        }
        
        //check for errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        //associated parts
        newProduct.setAssociatedParts(associatedPartTable.getItems());
        trySave(newProduct);
    }
    
    //updates product
    private void modifyProduct()
    {
        
        Product newProduct = new Product();
        String message = ""; //this is used to see what the field name is so i can use one try catch.

        //id 
        newProduct.setId(InventoryMain.modifiedProduct.getId());
        //name
        if(!productNameField.getText().equals(""))
        {
            newProduct.setName(productNameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        //price
        if(InventoryMain.isNumeric(productPriceField.getText()))
        {
            newProduct.setPrice(Double.parseDouble(productPriceField.getText()));
        }
        else
        {
            message += productPriceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(productInventoryField.getText()))
        {
            newProduct.setStock(Integer.parseInt(productInventoryField.getText()));
        }
        else
        {
            message += productInventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(productMinField.getText()))
        {
            
             newProduct.setMin(Integer.parseInt(productMinField.getText()));
        }
        else
        {
            message += productMinLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(productMaxField.getText()))
        {
             newProduct.setMax(Integer.parseInt(productMaxField.getText()));
        }
        else
        {
            message += productMaxLabel.getText() + " must be a number!\n";
        }
        
        //check for errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        //associated parts
        newProduct.setAssociatedParts(associatedPartTable.getItems());
        //try modify
        tryModify(InventoryMain.getAllProducts().indexOf(InventoryMain.modifiedProduct), newProduct);
    }
    
    //tries to add product to allProducts
    private void trySave(Product newProduct)
    {
        String message = ""; 
        
        //check stock value
        if(newProduct.getStock() > newProduct.getMax() || newProduct.getStock() < newProduct.getMin())
        {
            message += "Inventory must be between or equal to min and max.\n";
        }
        
        //check min and max
        if(newProduct.getMin() > newProduct.getMax())
        {
            message += "Min cannot be greater than max.";
        }
        
        //check if errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        //save
        InventoryMain.addProduct(newProduct);
        //set scene
        InventoryMain.setScene("InventoryManagementForm");
    }
    
    //tries to update the product at an index
    private void tryModify(int index, Product modifiedProduct)
    {
        String message = "";
        //check stock value
        if(modifiedProduct.getStock() > modifiedProduct.getMax() || modifiedProduct.getStock() < modifiedProduct.getMin())
        {
            message += "Inventory must be between or equal to min and max.\n";
        }
        
        //check min and max
        if(modifiedProduct.getMin() > modifiedProduct.getMax())
        {
            message += "Min cannot be greater than max.";
        }
        
        //check if errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        //save
        InventoryMain.updateProduct(index, modifiedProduct);
        //set scene
        InventoryMain.setScene("InventoryManagementForm");
    }

}
