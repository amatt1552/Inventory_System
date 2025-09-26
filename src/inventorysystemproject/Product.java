
package inventorysystemproject;
import javafx.collections.*;

/**
 * The product. 
 * Depends on parts.
 * @author Austin Matthews
 */
public class Product 
{
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;
    
    /**
     * Class constructor. 
     */
    public Product()
    {
        id = -1;
        name = "";
        price = -1;
        stock = -1;
        min = -1;
        max = -1;
    }
    
    /**
     * Class constructor setting values to product. 
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max 
     */
    public Product(int id, String name, double price, int stock, int min, int max)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    /**
     * Get the id. 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id. 
     * @param id the id to set
     */
    public void setId(int id) 
    {
        this.id = id;
    }

    /**
     * Get the name. 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the price. 
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Set the price.
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Get the stock. 
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Set the stock.
     * @param stock the new stock
     */
    public void setStock(int stock) 
    {
        this.stock = stock;
    }

    /**
     * Get min value.
     * @return the min
     */
    public int getMin() 
    {
        return min;
    }

    /**
     * Set min value.
     * @param min the new minimum value
     */
    public void setMin(int min) 
    {
        this.min = min;
    }

    /**
     * Get max value. 
     * @return the max
     */
    public int getMax() 
    {
        return max;
    }

    /**
     * Set max value. 
     * @param max the new maximum value
     */
    public void setMax(int max) 
    {
        this.max = max;
    }
    
    /**
     * Adds a part to the product. 
     * @param part added part
     */
    public void addAssociatedPart(Part part)
    {
        associatedParts.add(part);
    }
    
    /**
     * Sets associated parts for the product. 
     * @param parts partList
     */
    public void setAssociatedParts(ObservableList<Part> parts)
    {
        
        associatedParts = parts;
    }
    
    /**
     * Removes a part from the product. 
     * @param part removed part
     * @return delete successful?
     */
    public boolean deleteAssociatedPart(Part part)
    {
        return associatedParts.remove(part);
    }
    
    /**
     * Get all associated parts. 
     * @return all associated parts 
     */
    public ObservableList<Part> getAllAssociatedParts()
    {
        return associatedParts;
    }
}
