package inventorysystemproject;

/**
 * Class extended from Part. 
 * @author Austin Matthews
 */
public class OutSourced extends Part
{
    private String companyName;
    /**
     * Class constructor setting values to OutSourced part. 
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max
     * @param companyName 
     */
    public OutSourced(int id, String name, double price, int stock, int min, int max, String companyName)
    {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }
    
    /**
     * Class constructor. 
     */
    public OutSourced()
    {
        super(-1,"",-1,-1,-1,-1);
        this.companyName = "";
    }
    
    /**
     * Set company name. 
     * @param companyName New name
     */
    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }
    
    /**
     * Get company name. 
     * @return the company name
     */
    public String getCompanyName()
    {
        return companyName;
    }
}
