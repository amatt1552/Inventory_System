package inventorysystemproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;


/**
 * FXML PartForm Controller class. 
 *
 * @author Austin Matthews
 */
public class PartFormController implements Initializable 
{
    @FXML
    private Label partFormTitle;
    @FXML
    private RadioButton inHouseRadio;
    @FXML
    private RadioButton outSourcedRadio;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label inventoryLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label machineAndCompanyLabel;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField inventoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField maxField;
    @FXML
    private TextField machineAndCompanyField;
    @FXML
    private TextField minField;
    @FXML
    private Label consoleLabel;
    
    //listens for the loadComplete event
    private final InventoryMain.SceneLoadedListener loadComplete = () -> initializeValues();
    
    //switches to inHouse
    @FXML
    private void switchToInHouseOnAction(ActionEvent event) 
    {
        machineAndCompanyLabel.setText("Machine ID");
    }

    //switches to outSourced
    @FXML
    private void switchToOutSourcedOnAction(ActionEvent event) 
    {
        machineAndCompanyLabel.setText("Company Name");
    }

    @FXML
    private void savePartOnAction(ActionEvent event) 
    {
        //check if modifying of adding
        switch(InventoryMain.getPartFormState())
        {
            case AddPart: //adding
                //decide what kind of part it is
                if(inHouseRadio.isSelected())//in house
                {
                    //add the part
                    setInHouse();
                }
                else //outsourced
                {
                    //add the part
                    setOutSourced();

                }
                break;
            case ModifyPart: //modifying
                //allParts.indexOf(selectedPart);
                if(inHouseRadio.isSelected())//in house
                {
                    
                    modifyInHouse();
                }
                else //outsourced
                {
                    
                    modifyOutSourced();

                }
                break;
        }
        
                
    }
    
    //cancels modifying or adding part
    @FXML
    private void cancelPartOnAction(ActionEvent event) 
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
        
    }

    /**
     * Meant to set values after done loading scene. 
     */
    public void initializeValues()
    {
        switch(InventoryMain.getPartFormState())
        {
            case AddPart:
                partFormTitle.setText("Add Part");
                break;
            case ModifyPart:
                partFormTitle.setText("Modify Part");
                //Inventory.displayError(Inventory.modifiedPartIndex.getClass().getSimpleName());
                //maybe make simpler
                if(InventoryMain.modifiedPart.getClass().getSimpleName().equals("InHouse"))
                {
                    //cast to inHouse
                    InHouse modifiedPart = (InHouse)InventoryMain.modifiedPart;
                    //enable inHouse radio
                    inHouseRadio.setSelected(true);
                    //id 
                    idField.setText("" + modifiedPart.getId());
                    //name
                    nameField.setText(modifiedPart.getName());
                    //price
                    priceField.setText("" + modifiedPart.getPrice());
                    //stock
                    inventoryField.setText("" + modifiedPart.getStock());
                    //min
                    minField.setText("" + modifiedPart.getMin());
                    //max
                    maxField.setText("" + modifiedPart.getMax());
                    //machineId
                    machineAndCompanyField.setText("" + modifiedPart.getMachineId());
                }
                else
                {
                    //cast to outsourced
                    OutSourced modifiedPart = (OutSourced)InventoryMain.modifiedPart;
                    //enable outsourced radio
                    outSourcedRadio.setSelected(true);
                    //id 
                    idField.setText("" + modifiedPart.getId());
                    //name
                    nameField.setText(modifiedPart.getName());
                    //price
                    priceField.setText("" + modifiedPart.getPrice());
                    //stock
                    inventoryField.setText("" + modifiedPart.getStock());
                    //min
                    minField.setText("" + modifiedPart.getMin());
                    //max
                    maxField.setText("" + modifiedPart.getMax());
                    //change label text
                    machineAndCompanyLabel.setText("Company Name");                    
                    //machineId
                    machineAndCompanyField.setText(modifiedPart.getCompanyName());
                }
                
                break;
            default:
                break;
        }
    }
    
    //adds inhouse part
    private void setInHouse()
    {
        
        //set to inhouse
        //decided to set the values with functions instead of using the constructor so I can check all the inputs
        InHouse newInHouse = new InHouse(); 
        String message = ""; //this is used to display all the errors.

        //id will be set in inventory
        //name
        if(!nameField.getText().equals(""))
        {
            newInHouse.setName(nameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        //price
        if(InventoryMain.isNumeric(priceField.getText()))
        {
            newInHouse.setPrice(Double.parseDouble(priceField.getText()));
        }
        else
        {
            message += priceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(inventoryField.getText()))
        {
            newInHouse.setStock(Integer.parseInt(inventoryField.getText()));
        }
        else
        {
            message += inventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(minField.getText()))
        {
            
             newInHouse.setMin(Integer.parseInt(minField.getText()));
        }
        else
        {
            message += minLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(maxField.getText()))
        {
             newInHouse.setMax(Integer.parseInt(maxField.getText()));
        }
        else
        {
            message += maxLabel.getText() + " must be a number!\n";
        }
            
        //machineId
        if(InventoryMain.isNumeric(machineAndCompanyField.getText()))
        {
            newInHouse.setMachineId(Integer.parseInt(machineAndCompanyField.getText()));
             
        }
        else
        {
            message += machineAndCompanyLabel.getText() + " must be a number!";
        }
        
        //check if any errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        
        //try save
        trySave(newInHouse);
        
    }
    
    //sets outsourced part
    private void setOutSourced()
    {
        //set outsourced
        //decided to set the values with functions instead of using the constructor so I can check all the inputs
        OutSourced newOutSourced = new OutSourced();
       
        String message = ""; //this is used to display all the errors.
        
        //id is set in inventory
        //name
        if(!nameField.getText().equals(""))
        {
            newOutSourced.setName(nameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        //companyName
        newOutSourced.setCompanyName(machineAndCompanyField.getText());

        //price
        if(InventoryMain.isNumeric(priceField.getText()))
        {
            newOutSourced.setPrice(Double.parseDouble(priceField.getText()));
        }
        else
        {
            message += priceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(inventoryField.getText()))
        {
            newOutSourced.setStock(Integer.parseInt(inventoryField.getText()));
        }
        else
        {
            message += inventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(minField.getText()))
        {
            
             newOutSourced.setMin(Integer.parseInt(minField.getText()));
        }
        else
        {
            message += minLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(maxField.getText()))
        {
             newOutSourced.setMax(Integer.parseInt(maxField.getText()));
        }
        else
        {
            message += maxLabel.getText() + " must be a number!\n";
        }
            
        //check if any errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        
        //try save
        trySave(newOutSourced);
    }
    
    //modifies inhouse part
    private void modifyInHouse()
    {
        //cast to inhouse
        InHouse newInHouse = new InHouse();
        String message = ""; 

        //id 
        newInHouse.setId(InventoryMain.modifiedPart.getId());
        
        //name
        if(!nameField.getText().equals(""))
        {
            newInHouse.setName(nameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        
        //price
        if(InventoryMain.isNumeric(priceField.getText()))
        {
            newInHouse.setPrice(Double.parseDouble(priceField.getText()));
        }
        else
        {
            message += priceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(inventoryField.getText()))
        {
            newInHouse.setStock(Integer.parseInt(inventoryField.getText()));
        }
        else
        {
            message += inventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(minField.getText()))
        {
            
             newInHouse.setMin(Integer.parseInt(minField.getText()));
        }
        else
        {
            message += minLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(maxField.getText()))
        {
             newInHouse.setMax(Integer.parseInt(maxField.getText()));
        }
        else
        {
            message += maxLabel.getText() + " must be a number!\n";
        }
            
        //machineId
        if(InventoryMain.isNumeric(machineAndCompanyField.getText()))
        {
            newInHouse.setMachineId(Integer.parseInt(machineAndCompanyField.getText()));
             
        }
        else
        {
            message += machineAndCompanyLabel.getText() + " must be a number!";
        }
        
        //check if found errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        
        //try modify
        tryModify(InventoryMain.getAllParts().indexOf(InventoryMain.modifiedPart), newInHouse);
        
    }
    
    //modify outsourced part
    private void modifyOutSourced()
    {
        //cast to outsourced
        OutSourced newOutSourced = new OutSourced();
        
        String message = ""; 
        
        //id 
        newOutSourced.setId(InventoryMain.modifiedPart.getId());
        //name
        if(!nameField.getText().equals(""))
        {
            newOutSourced.setName(nameField.getText());
        }
        else
        {
            message += "Name cannot be empty.\n";
        }
        //companyName
        newOutSourced.setCompanyName(machineAndCompanyField.getText());

        //price
        if(InventoryMain.isNumeric(priceField.getText()))
        {
            newOutSourced.setPrice(Double.parseDouble(priceField.getText()));
        }
        else
        {
            message += priceLabel.getText() + " must be a number!\n";
        }
            
        //stock
        if(InventoryMain.isNumeric(inventoryField.getText()))
        {
            newOutSourced.setStock(Integer.parseInt(inventoryField.getText()));
        }
        else
        {
            message += inventoryLabel.getText() + " must be a number!\n";
        }
             
        //min
        if(InventoryMain.isNumeric(minField.getText()))
        {
            
             newOutSourced.setMin(Integer.parseInt(minField.getText()));
        }
        else
        {
            message += minLabel.getText() + " must be a number!\n";
        }
             
        //max
        if(InventoryMain.isNumeric(maxField.getText()))
        {
             newOutSourced.setMax(Integer.parseInt(maxField.getText()));
        }
        else
        {
            message += maxLabel.getText() + " must be a number!\n";
        }
            
        //check for errors
        if(!message.equals(""))
        {
            InventoryMain.displayMessageConsole(consoleLabel, message);
            return;
        }
        
        //try modify
        tryModify(InventoryMain.getAllParts().indexOf(InventoryMain.modifiedPart), newOutSourced);
    }
    
    //tries to add part to allParts
    private void trySave(Part newPart)
    {
        String message = ""; 
        
        //check stock value
        
        if(newPart.getStock() > newPart.getMax() || newPart.getStock() < newPart.getMin())
        {
            message += "Inventory must be between or equal to min and max.\n";
        }
        
        //check min and max
        if(newPart.getMin() > newPart.getMax())
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
        InventoryMain.addPart(newPart);
        //set scene
        InventoryMain.setScene("InventoryManagementForm");
    }
    
    //tries to update part
    private void tryModify(int index, Part modifiedPart)
    {
        String message = ""; 
        
        //check stock value
        if(modifiedPart.getStock() > modifiedPart.getMax() || modifiedPart.getStock() < modifiedPart.getMin())
        {
            message += "Inventory must be between or equal to min and max.\n";
        }
        
        //check min and max
        if(modifiedPart.getMin() > modifiedPart.getMax())
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
        InventoryMain.updatePart(index, modifiedPart);
        //set scene
        InventoryMain.setScene("InventoryManagementForm");
    }
    
}