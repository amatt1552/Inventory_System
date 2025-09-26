package inventorysystemproject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.WindowEvent;

//used to change the part form to modify or add
enum PartFormState
{
    AddPart, ModifyPart
}
//used to change the product form to modify or add
enum ProductFormState
{
    AddProduct, ModifyProduct
}

/**
 * Main method for my inventory management project. 
 * <p><b>Possible updates for project:</b></p>
 * <p>Check for negative values,</p>
 * <p>Tool-tips,</p>
 * <p>Use listener for list changes,</p>
 * <p>Scroll-able list for consoles maybe,</p>
 * <p>Something for accessibility maybe,</p>
 * <p>Use CSS,</p>
 * <p>Reshow all parts after search changed,</p>
 * <p>A way to delete the database.</p>
 * 
 * <p><b>Sources:</b></p>
 * <p>https://programming.guide/java/create-a-custom-event.html</p>
 * <p>http://tutorials.jenkov.com/javafx/tableview.html#obtain-tableview-selectionmodel-instance</p>
 * <p>https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Alert.html</p>
 * <p>https://www.sqltutorial.org/sql-identity/</p>
 * <p>https://www.howtobuildsoftware.com/index.php/how-do/cuK/java-netbeans-jar-splash-screen-not-display-when-execute-by-jar-file-but-it-works-when-execute-from-netbeans-ide</p>
 * <p>https://stackoverflow.com/questions/17003906/prevent-cancel-closing-of-primary-stage-in-javafx-2-2</p>
 * @author Austin Matthews
 */

public class InventoryMain extends Application
{
    //data needed for forms
    private static PartFormState currentPartFormState;
    private static ProductFormState currentProductFormState;
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    /**
     * Part selected from table.
     */
    public static Part modifiedPart;
     
    /**
     * Product selected from table.
     */
    public static Product modifiedProduct; 
    
    //stage
    private static Stage currentStage;
    private static String currentSceneName;
    
    //database
    private static int partCount = 1;
    private static int productCount = 1;
    private static final String DATABASE_NAME = "InventoryDB";
    
    //splashscreen
    private static SplashScreen splashScreen;
    private static Graphics2D splashGraphics;               // graphics context for overlay of the splash image
    private static Rectangle2D.Double splashTextArea;       // area where we draw the text
    private static Rectangle2D.Double splashProgressArea;   // area where we draw the progress bar
    private static Font font;
    
    /**
     * Used for scene load event.
     */
    @FunctionalInterface
    public interface SceneLoadedListener
    {
      /**
        * Abstract method for scene load event.
        */
        public abstract void sceneLoaded();
    }
    private static LoadComplete loadComplete;
    
    
//---------------------------The code starts------------------------------
    
    /**
     * Sets the stage at start.
     *<p><b>Null reference problem at lines 138 and 142:</b></p> 
     *<p>Initially I just gave it a try catch with a null reference exception; I assumed that there was no way to set it properly.</p> 
     *<p>I later realized I needed to initialize the list in the methods as well to something other than null for it to work properly 
     *and I found the FXCollections.observableArrayList() to initialize the lists.</p> 
     * @param stage stage being set
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        if(!DatabaseManager.checkForDatabase(DATABASE_NAME))
        {
            splashText("Creating database..");
            createInventoryDatabase();
            splashText("Opening database..");
        }
        else
        {
            splashText("Database Found!");
        }
        
        DatabaseManager.openDatabase(DATABASE_NAME);
        splashProgress(90);
        
        //getting part values from database
        splashText("Getting parts..");
        
        allParts = getPartsFromDatabase("");
        
        //getting product values from database
        splashText("Getting products..");
        allProducts = getProductsFromDatabase("");
        
        //closing database
        splashText("closing database..");
        DatabaseManager.closeConnection();
        splashProgress(100);
        
        //start up the stage
        splashText("starting application..");
        loadComplete = new LoadComplete();
        currentStage = stage;
        
        //gives me control over how my stage closes
        //https://stackoverflow.com/questions/17003906/prevent-cancel-closing-of-primary-stage-in-javafx-2-2
        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event) 
            {
                if(!stopCheck())
                {
                    //cancels event?
                    event.consume();
                }
            }
        });
        
        //set the scene
        setScene("InventoryManagementForm");
        if(splashScreen != null)
        {
            splashScreen.close();
        }
    }
    
    /**
     * The main method.
     * @param args the command line arguments.
    */
    public static void main(String[] args) 
    {
        splashInit();
        launch(args);
    }
    
    /**
     * Adds a part.
     * @param newPart added part
     */
    public static void addPart(Part newPart)
    {
        newPart.setId(partCount);
        allParts.add(newPart);
        partCount++;
        setPartsToDatabase(newPart);
        
    }
    
    /**
     * Adds a product.
     * @param newProduct added product
     */
    public static void addProduct(Product newProduct)
    {
        newProduct.setId(productCount);
        allProducts.add(newProduct);
        productCount++;
        setProductsToDatabase(newProduct);
    }
    
    /**
     * Finds part using passed ID.
     * @param partId searched part ID
     * @return if found returns part else is null
     */
    public static Part lookupPart(int partId)
    {
        for(Part part: allParts)
        {
            if(partId == part.getId())
            {
               return part;
            }
        }
        return null;
    }
    /**
     * Finds part using passed name.
     * @param partName searched part name
     * @return if found returns part else is null
     */
    public static ObservableList<Part> lookupPart(String partName)
    {
        //initialize the returned list
        ObservableList<Part> partList = FXCollections.observableArrayList();
        Part partWithID = null;
        
        //tries to find with id 
        int parsedString;
        if(isNumeric(partName))
        {
            parsedString = Integer.parseInt(partName);
            partWithID = lookupPart(parsedString);
            if(partWithID != null)
            {
                partList.add(partWithID);
            }
        }
            
        //look up part by name
        for(Part part: allParts)
        {
            String partID = "";
            //used to check to see if I already found it while searching by id
            //if so gets the partID
            if(partWithID != null)
            {
                if(partWithID.equals(part))
                {
                    partID = Integer.toString(partWithID.getId());
                }
            }
            
            //adds value if i didn't already find it and matches name
            if(part.getName().toUpperCase().startsWith(partName.toUpperCase()) && !part.getName().equals(partID))
            {
                partList.add(part);
            }
        }
        //return the list
        return partList;
    }
    
    /**
     * Finds product using passed ID.
     * @param productId searched product ID
     * @return if found returns product else is null
     */
    public static Product lookupProduct(int productId)
    {
        for(Product product: allProducts)
        {
            if(productId == product.getId())
            {
                return product;
            }
        }
        return null;
    }
    /**
     * Finds product using passed name.
     * @param productName searched product name
     * @return if found returns product else is null
     */
    public static ObservableList<Product> lookupProduct(String productName)
    {
        //initialize the returned list
        ObservableList<Product> productList = FXCollections.observableArrayList();
        //used to check if found product with id
        Product productWithID = null;
        
        //tries to search for id
        int parsedString;
        if(isNumeric(productName))
        {
            parsedString = Integer.parseInt(productName);
            productWithID = lookupProduct(parsedString);
            if(productWithID != null)
            {
                productList.add(productWithID);
            }
        }
            
        //look up product by name
        for(Product product: allProducts)
        {
            String partID = "";
            //used to check to see if I already found the product in the id check
            //if so i get the id
            if(productWithID != null)
            {
                if(productWithID.equals(product))
                {
                    partID = Integer.toString(productWithID.getId());
                }
            }
            
            //adds value if i didn't already find it and matches name
            if(product.getName().toUpperCase().startsWith(productName.toUpperCase()) && !product.getName().equals(partID))
            {
                productList.add(product);
            }
        }
        return productList;
    }
    
    /**
     * Updates part in all parts at index.
     * @param index index of part being changed
     * @param selectedPart new part
     */
    public static void updatePart(int index, Part selectedPart)
    {
        allParts.set(index, selectedPart);
        
        //check if any products have the modified part
        for(Product product : allProducts)
        {
            for(int ii = 0; ii < product.getAllAssociatedParts().size(); ii++)
            {
                if(product.getAllAssociatedParts().get(ii).getId() == selectedPart.getId())
                {
                //int associatedIndex = product.getAllAssociatedParts().indexOf(selectedPart);
                product.getAllAssociatedParts().set(ii, selectedPart);
                }
            }
        }
        setPartsToDatabase(selectedPart);
    }
    
    /**
    * Changes product to a different product.
    * @param index index of product being changed
    * @param selectedProduct new product
    */
    public static void updateProduct(int index, Product selectedProduct)
    {
        allProducts.set(index, selectedProduct);
        setProductsToDatabase(selectedProduct);
    }
    
    /**
     * Deletes the part.
     * @param deletedPart Deleted part
     */
    public static void deletePart(Part deletedPart)
    {
        allParts.remove(deletedPart);
        if(partCount == deletedPart.getId() + 1)
        {
            partCount--;
        }
        deletePartFromDatabase(deletedPart);
        //reload
        loadComplete.loaded();
    }
    
    /**
     * Deletes the product.
     * @param deletedProduct Deleted product
     */
    public static void deleteProduct(Product deletedProduct)
    {
        allProducts.remove(deletedProduct);
        if(productCount == deletedProduct.getId() + 1)
        {
            productCount--;
        }
        deleteProductFromDatabase(deletedProduct);
        //reload
        loadComplete.loaded();
    }
    
    /**
     * Get all parts.
     * @return all the parts
     */
    public static ObservableList<Part> getAllParts()
    {
        return allParts;
    }
    
    /**
     * Get all products.
     * @return all the products
     */
    public static ObservableList<Product> getAllProducts()
    {
        return allProducts;
    }
    
    /**
     * Sets the current scene. 
     * file name does not need .fxml at the end.
     * @param fxmlFileName file name for the fxml file
     * @param title title for scene. Set to Inventory System by default
     */
    public static void setScene(String fxmlFileName, String title)
    {
        try
        {
            //close all listeners
            loadComplete.removeAllListeners();
            
            // Load the FXML file.
            Parent parent = FXMLLoader.load(InventoryMain.class.getResource(fxmlFileName.concat(".fxml")));
            Scene scene = new Scene(parent);
           
            // Display our window, using the scene graph.
            currentStage.setTitle(title);
            currentStage.setScene(scene);
            currentStage.show();
            
            //used to see what scene i am on currently
            currentSceneName = fxmlFileName;
            //fire event
            loadComplete.loaded();
        }
        catch(IOException ioE)
        {
            System.out.println("incorrect filename");
        }
    }
    /**
     * Sets the current scene with title Inventory System. 
     * file name does not need .fxml at the end
     * @param fxmlFileName file name for the fxml file
     */
    public static void setScene(String fxmlFileName)
    {
        try
        {
            //close all listeners
            loadComplete.removeAllListeners();
           
            //sort parts and products
            allProducts = sortProducts(allProducts);
            allParts = sortParts(allParts);

            // Load the FXML file.
            Parent parent = FXMLLoader.load(InventoryMain.class.getResource(fxmlFileName.concat(".fxml")));
            Scene scene = new Scene(parent);
            
            // Display our window, using the scene graph.
            currentStage.setTitle("Inventory System");
            currentStage.setScene(scene);
            currentStage.show();
            
            //used to see what scene i am on currently 
            currentSceneName = fxmlFileName;
            //fire event
            loadComplete.loaded();
        }
        catch(IOException ioE)
        {
            System.out.println("incorrect filename");
        }
    }
    
    /**
     * Tries to close the application.
     */
    public static void closeStage()
    {
        stopCheck();
    }
    
    //decides whether to close the application
    private static boolean stopCheck()
    {
        if(currentSceneName.equals("InventoryManagementForm"))
        {
            Platform.exit();
            return true;
        }
        else
        {
            setScene("InventoryManagementForm");
            return false;
        }
    }
    
    /**
     * Display an error in a label.
     * @param label label used to display error
     * @param message message displayed
     */
    public static void displayMessageConsole(Label label, String message)
    {
        label.setText(message);
    }
    
    /**
     * Creates a dialog box with a message. 
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Alert.html
     * @param title string passed to title
     * @param header string passed header text
     * @param message string passed to content text
     * @return result (0 = cancel, 1 = ok)
     */
    public static int displayMessageDialog(String title, String header, String message)
    {
        //setting alert
        Alert alertDialog = new Alert(AlertType.CONFIRMATION);
        alertDialog.setTitle(title);
        alertDialog.getDialogPane().setHeaderText(header);
        alertDialog.getDialogPane().setContentText(message);
        
        //check result
        //optional seems to be good for things where null checks wouldn't work.
        Optional<ButtonType> result = alertDialog.showAndWait();
        if (result.isPresent())
        {
            if(result.get() == ButtonType.CANCEL)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        return 0;
        //int input = JOptionPane.showConfirmDialog(null, message, "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        //JOptionPane.showConfirmDialog(null, message);
    }
    
    /**
     * Sets the current PartFormState.
     * @param newPartFormState the new state
     */
    public static void setPartFormState(PartFormState newPartFormState)
    {
        currentPartFormState = newPartFormState;
    }
    
    /**
     * Sets the current ProductFormState.
     * @param newProductFormState the new state
     */
    public static void setProductFormState(ProductFormState newProductFormState)
    {
        currentProductFormState = newProductFormState;
    }
    
    /**
     * Gets the current PartFormState.
     * @return returns the current PartFormState
     */
    public static PartFormState getPartFormState()
    {
        return currentPartFormState;
    }
    
    /**
     * Gets the current ProductFormState.
     * @return returns the current ProductFormState
     */
    public static ProductFormState getProductFormState()
    {
        return currentProductFormState;
    }
    
    //sorts a product list based on product id
    private static ObservableList<Product> sortProducts(ObservableList<Product> sortedProducts)
    {
        if(sortedProducts != null)
        {
            int minIndex;
            Product minProductValue;
            for(int i = 0; i < sortedProducts.size(); i++)
            {
                //moves starting point
                minIndex = i;
                minProductValue = sortedProducts.get(i);
                for(int ii = i + 1; ii < sortedProducts.size(); ii++)
                {
                    if (sortedProducts.get(ii).getId() < minProductValue.getId())
                    {
                        //find smallest value
                        minProductValue = sortedProducts.get(ii);

                        minIndex = ii;
                    }
                }
                //swap with starting point
                sortedProducts.set(minIndex, sortedProducts.get(i));
                sortedProducts.set(i, minProductValue);

                //move to next point
            }

            //sort associatedparts
            for(Product product:sortedProducts)
            {
                product.setAssociatedParts(sortParts(product.getAllAssociatedParts()));
            }
        }
        return sortedProducts;
    }
    
    //sorts a part list based on part id
    private static ObservableList<Part> sortParts(ObservableList<Part> sortedParts)
    {
        if(sortedParts != null)
        {
            int minIndex;
            Part minProductValue;
            for(int i = 0; i < sortedParts.size(); i++)
            {
                //moves starting point
                minIndex = i;
                minProductValue = sortedParts.get(i);
                for(int ii = i + 1; ii < sortedParts.size(); ii++)
                {
                    if (sortedParts.get(ii).getId() < minProductValue.getId())
                    {
                        //find smallest value
                        minProductValue = sortedParts.get(ii);

                        minIndex = ii;
                    }
                }
                //swap with starting point
                sortedParts.set(minIndex, sortedParts.get(i));
                sortedParts.set(i, minProductValue);
                //move to next point
            }
        }
        return sortedParts;
    }
    
    /**
     * Checks if a string is numeric.
     * Hopefully I'll find a better way to check it someday so i don't have to depend on a try catch.
     * @param value string being checked
     * @return 
     */
    public static boolean isNumeric(String value)
    {
        try
        {
            Double.parseDouble(value);
            return true;
        }
        catch(NumberFormatException nfE)
        {
            return false;
        }
    }
//----------------------------Code End------------------------------
    
//From here on this is just me being extra. 
//The database, listener, and splash screen setup is past here.
    
//----------------------------Database------------------------------
    
    //creates a database for inventory 
    private static void createInventoryDatabase()
    {
        //creates database 
        DatabaseManager.createDatabase(DATABASE_NAME);
        splashProgress(75);
        //adds tables 
        
        //part table
        DatabaseManager.createTable("Part", "(" 
                + "PartID INTEGER NOT NULL PRIMARY KEY, " 
                + "PartName VARCHAR(100), " 
                + "PartPrice DOUBLE, "
                + "PartStock INTEGER, "
                + "PartMin INTEGER, "
                + "PartMax INTEGER)");
        
        //outsourced
        DatabaseManager.createTable("OutSourced", "("
                + "PartID INTEGER NOT NULL PRIMARY KEY REFERENCES Part(PartID) ON DELETE CASCADE, "
                + "CompanyName VARCHAR(100))");
        
        //inhouse
        DatabaseManager.createTable("InHouse", "("
                + "PartID INTEGER NOT NULL PRIMARY KEY REFERENCES Part(PartID) ON DELETE CASCADE, "
                + "MachineID INTEGER)");
        
        //product
        DatabaseManager.createTable("Product", "(" 
                + "ProductID INTEGER NOT NULL PRIMARY KEY, " 
                + "ProductName VARCHAR(100), " 
                + "ProductPrice DOUBLE, "
                + "ProductStock INTEGER, "
                + "ProductMin INTEGER, "
                + "ProductMax INTEGER)");
        
        //product's associated parts
        DatabaseManager.createTable("AssociatedParts", "("
                + "AssociatedPartID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
                + "PartID INTEGER NOT NULL REFERENCES Part(PartID) ON DELETE CASCADE, "
                + "ProductID INTEGER NOT NULL REFERENCES Product(ProductID) ON DELETE CASCADE)");
        DatabaseManager.closeConnection();
    }
    
    //set parts to the database
    private static void setPartsToDatabase(Part part)
    {
        DatabaseManager.openDatabase(DATABASE_NAME);
        //modified part columns
        String[] partColumns = {
            "PartID",
            "PartName",
            "PartPrice",
            "PartStock",
            "PartMin",
            "PartMax"};
        
        //values set
        String[] partValues = {
            Integer.toString(part.getId()), 
            "'" + part.getName() + "'", 
            Double.toString(part.getPrice()), 
            Integer.toString(part.getStock()), 
            Integer.toString(part.getMin()), 
            Integer.toString(part.getMax())};
        
        //inhouse
        String[] inHouseColumns = {"PartID", "MachineID"};
        String[] inHouseValues = new String[2];
        
        //outsourced
        String[] outSourcedColumns = {"PartID", "CompanyName"};
        String[] outSourcedValues = new String[2];
        
        if(DatabaseManager.checkForRow("Part", "PartID","WHERE PartID = " + part.getId()))
        {
            //update
            DatabaseManager.updateRows("Part", partColumns, partValues, "WHERE PartID = " + part.getId());
        }
        else
        {
            //insert
             DatabaseManager.InsertRow("Part", partColumns, partValues);
        }
        
        if(part.getClass().getSimpleName().equals("InHouse")) //sets inhouse
        {
            //cast to inhouse
            InHouse inHouse = (InHouse)part;
            //sets values for inhouse
            inHouseValues[0] = Integer.toString(inHouse.getId());
            inHouseValues[1] = Integer.toString(inHouse.getMachineId());
            DatabaseManager.deleteRows("OutSourced", "WHERE PartID = " + part.getId());
            if(DatabaseManager.checkForRow("InHouse", "PartID", "WHERE PartID = " + part.getId()))
            {
                //update
                DatabaseManager.updateRows("InHouse", inHouseColumns, inHouseValues, "WHERE PartID = " + part.getId());
            }
            else
            {
                //insert
                 DatabaseManager.InsertRow("InHouse", inHouseColumns, inHouseValues);
            }
            
        }
        else //sets outSourced
        {
            //cast to outSourced
            OutSourced outSourced = (OutSourced)part;
            //sets values for outSourced
            outSourcedValues[0] = Integer.toString(outSourced.getId());
            outSourcedValues[1] = "'" + outSourced.getCompanyName() + "'";
            DatabaseManager.deleteRows("InHouse", "WHERE PartID = " + part.getId());
            if(DatabaseManager.checkForRow("OutSourced", "PartID","WHERE PartID = " + part.getId()))
            {
                //update
                DatabaseManager.updateRows("OutSourced", outSourcedColumns, outSourcedValues, "WHERE PartID = " + part.getId());
            }
            else
            {
                //insert
                 DatabaseManager.InsertRow("OutSourced", outSourcedColumns, outSourcedValues);
            }
        }
        
        DatabaseManager.closeConnection();
    }
    
    //gets parts from the database using a natural join
    //the amendedWhereClause is used in case we need another condition (like with getProductsFromDatabase)
    private static ObservableList<Part> getPartsFromDatabase(String amendedWhereClause)
    {
        //returned list
        ObservableList<Part> partList = FXCollections.observableArrayList();
        //set up the command        
        try
        {
            //inhouse
            ResultSet result;
            result = DatabaseManager.getRows("*", "Part, InHouse", " WHERE Part.PartID = InHouse.PartID " + amendedWhereClause);
            if(result != null)
            {
                while(result.next())
                {

                    DatabaseManager.displayResult(result);
                    InHouse part = new InHouse();
                    part.setId(result.getInt("PartID"));
                    if(partCount <= result.getInt("PartID"))
                    {
                        partCount = result.getInt("PartID") + 1;
                    }
                    part.setName(result.getString("PartName"));
                    part.setStock(result.getInt("PartStock"));
                    part.setPrice(result.getDouble("PartPrice"));
                    part.setMin(result.getInt("PartMin"));
                    part.setMax(result.getInt("PartMax"));
                    part.setMachineId(result.getInt("MachineID"));
                    partList.add(part);
                }
            }
            //outsourced

            result = DatabaseManager.getRows("*", "Part, OutSourced", " WHERE Part.PartID = OutSourced.PartID"  + amendedWhereClause);

            if(result != null)
            {
                while(result.next())
                {
                    DatabaseManager.displayResult(result);
                    OutSourced part = new OutSourced();
                    part.setId(result.getInt("PartID"));

                    //needed just in case it already found a part with a greater id
                    if(partCount <= result.getInt("PartID"))
                    {
                        partCount = result.getInt("PartID") + 1;
                    }
                    part.setName(result.getString("PartName"));
                    part.setStock(result.getInt("PartStock"));
                    part.setPrice(result.getDouble("PartPrice"));
                    part.setMin(result.getInt("PartMin"));
                    part.setMax(result.getInt("PartMax"));
                    part.setCompanyName(result.getString("CompanyName"));
                    partList.add(part);
                }
            }
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + "getPartsFromDatabase");
        }
        
        return partList;
    }
    
    //deletes part from database
    private static void deletePartFromDatabase(Part deletedPart)
    {
        DatabaseManager.openDatabase(DATABASE_NAME);
        DatabaseManager.deleteRows("Part", "WHERE PartID = " + deletedPart.getId());
        DatabaseManager.closeConnection();
    }
    
    //decides whether to update or insert a product into the database
    private static void setProductsToDatabase(Product product)
    {
        DatabaseManager.openDatabase(DATABASE_NAME);
        //modified columns
        String[] productColumns = {
            "ProductID",
            "ProductName",
            "ProductPrice",
            "ProductStock",
            "ProductMin",
            "ProductMax"};
        
        //values being set
        String[] productValues = {
            Integer.toString(product.getId()), 
            "'" + product.getName() + "'", 
            Double.toString(product.getPrice()), 
            Integer.toString(product.getStock()), 
            Integer.toString(product.getMin()), 
            Integer.toString(product.getMax())};
        
        
        //modified columns for associated parts
        String[] associatedPartsColumns = {"PartID", "ProductID"};
        //values
        String[] associatedPartsValues = {"", Integer.toString(product.getId())};
        
        //sets product
        if(DatabaseManager.checkForRow("Product", "ProductID","WHERE ProductID = " + product.getId()))
        {
            //update
            DatabaseManager.updateRows("Product", productColumns, productValues, "WHERE ProductID = " + product.getId());
        }
        else
        {
            //insert
             DatabaseManager.InsertRow("Product", productColumns, productValues);
        }
        
        //resets associated parts for this product
        DatabaseManager.deleteRows("AssociatedParts", "WHERE ProductID = " + product.getId());
        //sets associated parts
        
        for(Part part : product.getAllAssociatedParts())
        {
            associatedPartsValues[0] = Integer.toString(part.getId());
            DatabaseManager.InsertRow("AssociatedParts", associatedPartsColumns, associatedPartsValues);
        }
        
        DatabaseManager.closeConnection();
    }
    
    //gets products from database using natural join
    private static ObservableList<Product> getProductsFromDatabase(String amendedWhereClause)
    {
        //returned list
        ObservableList<Product> productList = FXCollections.observableArrayList();
        //set up the command
                
        try
        {
            ResultSet result;            
            //compared values
            String[] tables = {"AssociatedParts","Product"};
            String[] values = {"ProductID"};
            //check for products without associated parts
            result = DatabaseManager.getRowsROJ("Product.*, AssociatedParts.PartID AS ASPartID, AssociatedParts.ProductID AS ASProductID",
                    tables,
                    values,
                    " WHERE AssociatedParts.PartID IS NULL OR AssociatedParts.ProductID IS NULL ");
            if(result != null)
            {
                while(result.next())
                {
                    DatabaseManager.displayResult(result);
                    Product product = new Product();
                    //id
                    product.setId(result.getInt("ProductID"));
                    if(productCount <= result.getInt("ProductID"))
                    {
                        productCount = result.getInt("ProductID") + 1;
                    }
                    //name
                    product.setName(result.getString("ProductName"));
                    //stock
                    product.setStock(result.getInt("ProductStock"));
                    //price
                    product.setPrice(result.getDouble("ProductPrice"));
                    //min
                    product.setMin(result.getInt("ProductMin"));
                    //max
                    product.setMax(result.getInt("ProductMax"));

                    productList.add(product);
                }
            }
            //check for products with associated parts
            result = DatabaseManager.getRows("*", "Part, AssociatedParts, Product ", "WHERE Part.PartID = AssociatedParts.PartID AND AssociatedParts.ProductID = Product.ProductID " + amendedWhereClause);
            if(result != null)
            {
                while(result.next())
                {
                    DatabaseManager.displayResult(result);
                    Product product = new Product();
                    //id
                    product.setId(result.getInt("ProductID"));
                    if(productCount <= result.getInt("ProductID"))
                    {
                        productCount = result.getInt("ProductID") + 1;
                    }
                    //name
                    product.setName(result.getString("ProductName"));
                    //stock
                    product.setStock(result.getInt("ProductStock"));
                    //price
                    product.setPrice(result.getDouble("ProductPrice"));
                    //min
                    product.setMin(result.getInt("ProductMin"));
                    //max
                    product.setMax(result.getInt("ProductMax"));

                    //associatedPart
                    product.setAssociatedParts(getPartsFromDatabase(" AND Part.PartID = " + result.getInt("PartID")));

                    //I need to check if the product already exists first
                    int searchedId = -2;
                    for(int i = 0; i < productList.size() ;i++)
                    {
                        if(product.getId() == productList.get(i).getId())
                        {
                            searchedId = i;
                        }

                    }

                    if(searchedId == -2) //if it doesn't exist add to the list
                    {
                        productList.add(product);
                        //allProducts.add(product);
                    }
                    else //else modify the list
                    {
                        for(Part part : product.getAllAssociatedParts())
                        {
                            productList.get(searchedId).addAssociatedPart(part);
                        }
                    }
                }
            }
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " getProductsFromDatabase");
        }
        
        return productList;
    }
    
    //deletes a product from the database
    private static void deleteProductFromDatabase(Product deletedProduct)
    {
        DatabaseManager.openDatabase(DATABASE_NAME);
        DatabaseManager.deleteRows("Product", "WHERE ProductID = " + deletedProduct.getId());
        DatabaseManager.closeConnection();
    }
//----------------------------------database end----------------------------------------
    
//----------------------------------splash start----------------------------------------
    //I never would have figured this out without this:
    //https://www.howtobuildsoftware.com/index.php/how-do/cuK/java-netbeans-jar-splash-screen-not-display-when-execute-by-jar-file-but-it-works-when-execute-from-netbeans-ide
    
    //initialize spashscreen.
    private static void splashInit()
    {
    
        // the splash screen object is created by the JVM, if it is displaying a splash image

        splashScreen = SplashScreen.getSplashScreen();
        // if there are any problems displaying the splash image
        // the call to getSplashScreen will returned null

        if (splashScreen != null) 
        {
            // get the size of the image now being displayed
            Dimension splashDimensions = splashScreen.getSize();
            int height = splashDimensions.height;
            int width = splashDimensions.width;

            // picking an area for our loading information
            splashTextArea = new Rectangle2D.Double(20, height * 0.90, width * 0.95, 20);
            splashProgressArea = new Rectangle2D.Double(1.0, height * .87, width - 2, 3);

            // create the Graphics environment for diplaying the loading info
            splashGraphics = splashScreen.createGraphics();
            font = new Font("Consolas", Font.BOLD, 14);
            
            //add font to graphic
            splashGraphics.setFont(font);

            // initialize the status info
            splashText("Checking for database..");
            splashProgress(0);
        }
    }
    
   
    //Display text in status area of Splash.
    private static void splashText(String text) 
    {
        if (splashScreen != null && splashScreen.isVisible()) 
        {

           // sets background to gray
           splashGraphics.setPaint(new Color(235,235,235));
           splashGraphics.fill(splashTextArea);

           // draw the text
           splashGraphics.setPaint(Color.BLACK);
           splashGraphics.drawString(text, (int) (splashTextArea.getX() + 10), (int) (splashTextArea.getY() + 15));

           // make sure it's displayed
           splashScreen.update();
        }
    }

    
    //Display a basic progress bar.
    private static void splashProgress(int percent) 
    {
        if (splashScreen != null && splashScreen.isVisible()) 
        {
            // draw an outline
            splashGraphics.setPaint(Color.WHITE);
            splashGraphics.fill(splashProgressArea);

            //position
            int x = (int) splashProgressArea.getMinX();
            int y = (int) splashProgressArea.getMinY();
            //scale
            int width = (int) splashProgressArea.getWidth();
            int height = (int) splashProgressArea.getHeight();

            //makes percent into 0 to 1 then multiples by width
            //width is 500 percent is 50. returns 250
            int doneWidth = Math.round(percent * width / 100.f);
            //math.max returns the greater of 2 values. Min vice versa. 
            //thus it will always pick the largest value between 0 and min(donewidth, width -1)
            doneWidth = Math.max(0, Math.min(doneWidth, width - 1));

            //fill the progress bar
            splashGraphics.setPaint(Color.ORANGE);
            splashGraphics.fillRect(x, y, doneWidth, height);

            //make sure it's displayed
            splashScreen.update();
        }
    }
 
//---------------------------Splash End-------------------------------
    
//---------------------------Event Class------------------------------
    
    //https://programming.guide/java/create-a-custom-event.html
    //Used for an event to check if fxml is done loading
    private class LoadComplete
    {
        private final List<SceneLoadedListener> listeners;
        public LoadComplete()
        {
            listeners = new ArrayList<>();
        }
        
        /**
         * Adds to listener.
         * @param toAdd class added
         */
        public void addListener(SceneLoadedListener toAdd) 
        {
            listeners.add(toAdd);
        }
        
        /**
         * Removes from listener.
         * @param toRemove class removed
         */
        public void removeListener(SceneLoadedListener toRemove) 
        {
            listeners.remove(toRemove);
        }
        
         /**
         * Removes all listeners.
         */
        public void removeAllListeners()
        {
            listeners.clear();
        }
        
        /**
         * Informs listeners. 
         * fires the scene loaded event
         */ 
        public void loaded()
        {
            for(SceneLoadedListener loadComplete : listeners)
            {
                loadComplete.sceneLoaded();
            }
        }
    }
    
    /**
     * Adds listener to loadComplete. 
     * @param listeningClass class added to the listener
     */
    public static void addListener(SceneLoadedListener listeningClass) 
    {
        loadComplete.addListener(listeningClass);
    }
    
    /**
     * Removes listener from loadComplete. 
     * @param listeningClass class removed from the listener
     */
    public static void removeListener(SceneLoadedListener listeningClass) 
    {
        loadComplete.removeListener(listeningClass);
    }
    
}
    
    
