
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
 * FXML InventoryManagementForm Controller class. 
 *
 * @author Austin Matthews
 */
public class InventoryManagementFormController implements Initializable 
{

    @FXML
    private Pane partPane;
    @FXML
    private Rectangle partRect;
    @FXML
    private TableView<Part> partTable;
    @FXML
    private TableColumn<?, ?> partID;
    @FXML
    private TableColumn<?, ?> partName;
    @FXML
    private TableColumn<?, ?> partInventoryLevel;
    @FXML
    private TableColumn<?, ?> partPricePerUnit;
    @FXML
    private TextField partSearch;
    @FXML
    private Pane productPane;
    @FXML
    private Rectangle productRect;
    @FXML
    private TextField productSearch;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<?, ?> productID;
    @FXML
    private TableColumn<?, ?> productName;
    @FXML
    private TableColumn<?, ?> productInventoryLevel;
    @FXML
    private TableColumn<?, ?> productPricePerUnit;
    @FXML
    private Label consoleLabel;

    //listens for the load complete event
    private final InventoryMain.SceneLoadedListener loadComplete = () -> initializeValues();
    
    private ObservableList<Product> searchedProducts = FXCollections.observableArrayList();
    private ObservableList<Part> searchedParts = FXCollections.observableArrayList();
    
    //used to check if the value has changed
    private String lastSearchedProduct = ""; //for search
    private String lastSearchedPart = ""; //for search
    
    //Goes to the add part form
    @FXML
    private void addPartOnAction(ActionEvent event) 
    {
        //makes it an add part form
        InventoryMain.setPartFormState(PartFormState.AddPart);
        InventoryMain.setScene("PartForm");
    }

    //Goes to the modify part form
    @FXML
    private void modifyPartOnAction(ActionEvent event) 
    {
        //makes it a modify part form
        InventoryMain.modifiedPart = partTable.getSelectionModel().getSelectedItem();
        if(InventoryMain.modifiedPart != null)
        {
            InventoryMain.setPartFormState(PartFormState.ModifyPart);
            InventoryMain.setScene("PartForm");
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
    }
    
    //deletes a part
    @FXML
    private void deletePartOnAction(ActionEvent event) 
    {
        Part deletedPart = InventoryMain.modifiedPart = partTable.getSelectionModel().getSelectedItem();
        
        if(deletedPart != null)
        {
             //confirmation
            int result = InventoryMain.displayMessageDialog("Part", "Deleting Part", "Are you sure you want to delete the part " + deletedPart.getName() + "?");
            if(result != 1)
            {
                return;
            }
            
            //delete
            InventoryMain.deletePart(deletedPart);
            //check for associated parts
            String deleteMessage = "Part " + deletedPart.getName() + " deleted.";
            String deletedAssociatedParts = "";
            for(int i = 0; i < InventoryMain.getAllProducts().size(); i++)//get product
            {
                if(InventoryMain.getAllProducts().get(i).getAllAssociatedParts().contains(deletedPart))
                {
                    System.out.println(i);
                    //remove deleted part
                    InventoryMain.getAllProducts().get(i).getAllAssociatedParts().remove(deletedPart);
                   
                    //if else is for the commas
                    if(deletedAssociatedParts.equals(""))
                    {
                        deletedAssociatedParts +=  InventoryMain.getAllProducts().get(i).getName();
                    }
                    else
                    {
                        deletedAssociatedParts += ", " + InventoryMain.getAllProducts().get(i).getName();
                    }
                }
            }
            
            //check if found any associated parts
            if(!deletedAssociatedParts.equals(""))
            {
                deleteMessage += "\nRemoved part from products: " + deletedAssociatedParts;
            }
            
            InventoryMain.displayMessageConsole(consoleLabel, deleteMessage);
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
    }
    
    //selects searched parts when typing
    @FXML
    private void selectPartsOnType(KeyEvent event) 
    {
        selectSearchedParts();
    }
    
    //selects searched products when typing
    @FXML
    private void selectProductsOnType(KeyEvent event) 
    {
        selectSearchedProducts();
        
    }
    
    //search parts on enter
    @FXML
    private void searchPartOnAction(ActionEvent event) 
    {
        searchParts();
    }
    
    //search product on enter
    @FXML
    private void searchProductOnAction(ActionEvent event) 
    {
        searchProducts();
    }
    
    //Goes to the add product form
    @FXML
    private void addProductOnAction(ActionEvent event) 
    {
        //makes it a add product form
        InventoryMain.setProductFormState(ProductFormState.AddProduct);
        InventoryMain.setScene("ProductForm");
    }

    //Goes to the modify product form
    @FXML
    private void modifyProductOnAction(ActionEvent event) 
    {
        //makes it a modify product form
        InventoryMain.modifiedProduct = productTable.getSelectionModel().getSelectedItem();
        if(InventoryMain.modifiedProduct != null)
        {
            
            InventoryMain.setProductFormState(ProductFormState.ModifyProduct);
            InventoryMain.setScene("ProductForm");
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
        
    }
    
    //Deletes selected product
    @FXML
    private void deleteProductOnAction(ActionEvent event) 
    {
        Product deletedProduct = InventoryMain.modifiedProduct = productTable.getSelectionModel().getSelectedItem();
        
        if(deletedProduct != null)
        {
            //check for associated parts
            if(deletedProduct.getAllAssociatedParts().size() > 0)
            {
                InventoryMain.displayMessageConsole(consoleLabel, "Must remove all parts from " + deletedProduct.getName() + " before deleting this product.");
                return;
            }
            //confirmation
            int result = InventoryMain.displayMessageDialog("Product", "Deleting Product", "Are you sure you want to delete the product " + deletedProduct.getName() + "?");
            if(result != 1)
            {
                return;
            }
            
            //delete
            InventoryMain.deleteProduct(deletedProduct);
            InventoryMain.displayMessageConsole(consoleLabel, "Deleted product " + deletedProduct.getName() + ".");
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Nothing selected!");
        }
    }
    
    //closes scene
    @FXML
    private void exitOnAction(ActionEvent event) 
    {
        InventoryMain.closeStage();
    }
    
   /**
    * Seems to be called while scene is creating. 
    * However changing values for things like the rectangle, or text fields from this did not seem to work properly.
    * @param url
    * @param rb 
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        
        InventoryMain.addListener(loadComplete);
        
        //set tables
        partTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        productTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        //makes setting values for tables simpler
        //part columns
        partID.setCellValueFactory(new PropertyValueFactory("id"));
        partName.setCellValueFactory(new PropertyValueFactory("name"));
        partInventoryLevel.setCellValueFactory(new PropertyValueFactory("stock"));
        partPricePerUnit.setCellValueFactory(new PropertyValueFactory("price"));
        //product columns
        productID.setCellValueFactory(new PropertyValueFactory("id"));
        productName.setCellValueFactory(new PropertyValueFactory("name"));
        productInventoryLevel.setCellValueFactory(new PropertyValueFactory("stock"));
        productPricePerUnit.setCellValueFactory(new PropertyValueFactory("price"));
    }

    
     /**
     * Meant to set values after completely done loading the scene. 
     * It has so far been called a bit after initialize.
     */
    public void initializeValues()
    {
        //sets the part rectangle to be the size of the part pane.
        //should do this in css
        partRect.setLayoutX(0);
        partRect.setLayoutY(0);
        partRect.setWidth (partPane.getWidth());
        partRect.setHeight (partPane.getHeight());
        
        //sets the product rectangle to be the size of the product pane.
        //should do this in css
        productRect.setLayoutX(0);
        productRect.setLayoutY(0);
        productRect.setWidth (productPane.getWidth());
        productRect.setHeight (productPane.getHeight());
        
        //sets part table
        searchParts();
        //sets product table
        searchProducts();
        //clears console
        InventoryMain.displayMessageConsole(consoleLabel, "");
        
    }
    
    // selects searched parts
    // http://tutorials.jenkov.com/javafx/tableview.html#select-rows-programmatically
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
    
    //selects searched products
    private void selectSearchedProducts()
    {
        String searchedString = productSearch.getText();
        
        //used to "reset" console
        if(!lastSearchedProduct.equals(searchedString))
        {
           InventoryMain.displayMessageConsole(consoleLabel, "");
            
           lastSearchedProduct = searchedString;
        }
        
        if(!searchedString.equals(""))
        {
            //find rows
            searchedProducts = InventoryMain.lookupProduct(searchedString);
            productTable.getSelectionModel().clearSelection();
            //sets from searched products
            for(Product product : searchedProducts)
            {
                productTable.getSelectionModel().select(product);
            }
            //ProductTable.setItems(InventoryMain.lookupProduct(searchedString));
        }
        else
        {
            productTable.getSelectionModel().clearSelection();
            searchedProducts.clear();
            //ProductTable.setItems(InventoryMain.getAllProducts());
        }
    }
    
    //hide values not selected from search
    private void searchParts()
    {
        if(!searchedParts.isEmpty())
        {
            partTable.setItems(searchedParts);
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Could not find Part!");
            partTable.setItems(InventoryMain.getAllParts());
        }
    }
    
    //hide values not selected from search
    private void searchProducts()
    {
        if(!searchedProducts.isEmpty())
        {
            productTable.setItems(searchedProducts);
        }
        else
        {
            InventoryMain.displayMessageConsole(consoleLabel, "Could not find Product!");
            productTable.setItems(InventoryMain.getAllProducts());
        }
    }

   
}
