package inventorysystemproject;

/**
 * Class extended from Part.
 * @author Austin Matthews
 */
public class InHouse extends Part
{
    private int machineId;
    /**
     * Class constructor setting values to InHouse part. 
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max
     * @param machineId 
     */
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId)
    {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }
    
    /**
     * Class constructor. 
     */
    public InHouse()
    {
        super(-1,"",-1,-1,-1,-1);
        this.machineId = -1;
    }
    /**
     * Set machine id. 
     * @param machineId new ID
     */
    public void setMachineId(int machineId)
    {
        this.machineId = machineId; 
    }
    
    /**
     * Get MachineId. 
     * @return the machine ID
     */
    public int getMachineId()
    {
        return machineId;
    }
}
