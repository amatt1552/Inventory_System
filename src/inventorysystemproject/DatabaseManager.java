package inventorysystemproject;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * Helps Manage databases.
 * @author Austin Matthews
 */
public class DatabaseManager 
{
    private static final ObservableList<Statement> statements = FXCollections.observableArrayList();
    private static Connection connection;
    
    /**
     * <p>Only creates a database.</p> 
     * <p>If it already exists will not create another one.</p>
     * @param databaseName database name
     */
    protected static void createDatabase(String databaseName)
    {
        final String DB_URL = databaseName + ";create=true";
        openDatabase(DB_URL);
        System.out.println("Database " + databaseName + " created or exists.");
        
    }
    
    /**
     * Creates a database and tables. 
     * Created tables have no check if they exist but should still work fine.
     * @param databaseName database name
     * @param command command for creating tables.
     * @throws Exception 
     */
    protected static void createDatabase(String databaseName, String command) throws Exception
    {
        final String DB_URL = databaseName + ";create=true";
        openDatabase(DB_URL);
        if(!command.equals(""))
        {
            createTable(command);
        }
        System.out.println("Database " + databaseName + " created.");
        
    }
    
    /**
     * Opens the database.
     * @param databaseName database name
     */
    protected static void openDatabase(String databaseName)
    {
        final String DB_URL = "jdbc:derby:" + databaseName; 
        Statement statement = null;
        //adds new statements to be closed just incase i miss closing them
        statements.add(statement);
        try
        {
            // Create a connection to the database.
            connection = DriverManager.getConnection(DB_URL);
            // Create a Statement object.
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            
        }
        catch(SQLException sqlE)
        {
             System.out.println("ERROR: " + sqlE.getMessage() + " openDatabase");
        }
        closeStatement(statement);
    }
    
    /**
     * Checks if database exists.
     * @param databaseName database name
     * @return 
     */
    protected static Boolean checkForDatabase(String databaseName)
    {
        final String DB_URL = "jdbc:derby:" + databaseName; 
        try
        {
            // Get a connection to the database.
            DriverManager.getConnection(DB_URL);
            //if no error database exists
            return true;
        }
        catch(SQLException sqlE)
        {
             System.out.println("Could not find database " + databaseName + ".");
        }
        return false;
    }
    /**
     * Creates table with given name. 
     * Has a check if table name exists. 
     * How it is formatted: "CREATE TABLE " + tableName + command
     * @param tableName table name
     * @param command sql command
     */
    protected static void createTable(String tableName, String command)
    {
        if(connection == null)
        {
            return;
        }
        
        Statement statement = null;
        statements.add(statement);
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //gets database data
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet result = metadata.getTables(null, null, "%", null);
            //checks if table exists
            while (result.next()) 
            {
                //System.out.println(rs.getString(3));
                if(result.getString(3).equals(tableName.toUpperCase()))
                {
                    
                    System.out.println("Table with name " + tableName + " already created!");
                    closeStatement(statement);
                    return;
                }
            }
            
            // Create the table.
            System.out.println("Creating the " + tableName + " table...");
            statement.execute("CREATE TABLE " + tableName + command);
            
            System.out.println("Table " + tableName + " created.");
            
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " createTable(with tableName)");
        }
        closeStatement(statement);
    }
    
    /**
     * Creates a table by only using a command. 
     * Has no check if table exists.
     * @param command sql command
     */
    protected static void createTable(String command)
    {
        if(connection == null)
        {
            return;
        }
        
        Statement statement = null;
        statements.add(statement);
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            // Create the table.
            System.out.println("Creating the table...");
            statement.execute(command);
            
            System.out.println("Table created.");
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " createTable");
        }
        
        closeStatement(statement);
    }
    /**
     * Deletes a table.
     * goes like: "DROP TABLE " + tableName + command
     * @param tableName table name
     * @param command sql command
     */
    protected static void dropTable(String tableName, String command)
    {
        if(connection == null)
        {
            return;
        }
        
        Statement statement = null;
        statements.add(statement);
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //try to drop the table.
            System.out.println("Dropping the " + tableName + " table...");
            statement.execute("DROP TABLE " + tableName + command);           
            System.out.println("Table " + tableName + " dropped.");
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " dropTable");
        }
        closeStatement(statement);
    }
    
    
    //Closes the statement.
    private static void closeStatement(Statement statement)
    {
        if(connection == null)
        {
            return;
        }
        
        try
        {
            if(statement != null)
            {
                statement.close();
            }
            statements.remove(statement);
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " closeStatement");
        }
    }
    
    /**
     * Closes the connection and all the leftover statements.
     */
    protected static void closeConnection()
    {
        if(connection == null)
        {
            return;
        }
        
        try
        {        
            // Close the resources.
            //close all the statements
            for(Statement statement : statements)
            {
                if(statement != null)
                {
                    statement.close();
                }
            }
            connection.close();
            System.out.println("Database closed.");
        }
        catch(SQLException sqlE)
        {
             System.out.println("ERROR: " + sqlE.getMessage() + " closeDatabase");
        }
    }
    
    //time for some dml
    
    /**
     * Adds a row to a table.
     * @param tableName table name 
     * @param columns columns being modified
     * @param values values being inserted
     */
    protected static void InsertRow(String tableName, String[] columns, String[] values)
    {
        if(connection == null)
        {
            return;
        }
        
        //sets up the command
        //My rule of thumb with concat vs + with strings is if its a loop or complex then concat. 
        //Could get messy if there are a lot of iterations in my mind.
        //using concat too often is not as easy to read as + to me, so i dont really like to use it.
        String command = "INSERT INTO ".concat(tableName).concat("(");
        if(columns.length == values.length)
        {
            //adds column names
            for (int i = 0; i < columns.length;i++) 
            {
               
                command = command.concat(columns[i]);
                if(i != columns.length - 1)
                {
                    command = command.concat(", ");
                }
            }
            
            command = command.concat(") VALUES(");
                
            //adds values
            for (int i = 0; i < values.length; i++) 
            {
               
                command = command.concat(values[i]);
                if(i != values.length - 1)
                {
                    command = command.concat(", ");
                }
            }
            command = command.concat(")");
        }
        else
        {
            System.out.println("column length does not equal value length! Make sure they have the same length.");
            return;
        }
        
        //excute command
        Statement statement = null;
        statements.add(statement);
        try
        { 
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("inserting into table " + tableName);
            System.out.println(command);
            int count = statement.executeUpdate(command);
            System.out.println("rows affected: " + count);
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " insertRow");
        }
        closeStatement(statement);
    }
    
    /**
     * Updates rows.
     * @param tableName
     * @param columns
     * @param values
     * @param whereClause 
     */
    protected static void updateRows(String tableName, String[] columns, String[] values, String whereClause)
    {
        if(connection == null)
        {
            return;
        }
        
        //setting up the command
        String command = "UPDATE ".concat(tableName).concat(" SET ");
        if(columns.length == values.length)
        {
            for(int i = 0; i < columns.length; i++)
            {
                command = command.concat(columns[i])
                .concat(" = ")
                .concat(values[i]);
                if(i != columns.length - 1)
                {
                    command = command.concat(", ");
                }
                
            }
            command = command.concat(" ").concat(whereClause);
        }
        else
        {
            System.out.println("column length does not equal value length! Make sure they have the same length.");
            return;
        }
        
        //execute command
        Statement statement = null;
        statements.add(statement);
        try
        { 
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("updating table " + tableName);
            System.out.println(command);
            int count = statement.executeUpdate(command);
            System.out.println("rows affected: " + count);
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " updateRows");
        }
        closeStatement(statement);
    }
    
    /**
     * Deletes rows. 
     * looks like: "DELETE FROM " + tableName + " " + whereClause;
     * @param tableName
     * @param whereClause 
     */
    protected static void deleteRows(String tableName, String whereClause)
    {
        if(connection == null)
        {
            return;
        }
        
        //set up the command
        String command = "DELETE FROM "
                + tableName
                + " "
                + whereClause;
        
        //execute command
        Statement statement = null;
        statements.add(statement);
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println(command);
            int rows = statement.executeUpdate(command);
            System.out.println(rows);
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " deleteRows");
        }
        closeStatement(statement);
    }
    /**
     * Get rows from tables. 
     * Can be used to do joins.
     * looks like: "SELECT " + columns + " FROM " + tableNames + whereClause;
     * @param columns
     * @param tableNames
     * @param whereClause
     * @return 
     */
    protected static ResultSet getRows(String columns, String tableNames, String whereClause)
    {
        if(connection == null)
        {
            return null;
        }
        
        Statement statement = null;
        statements.add(statement);
        //set up the command
        String command = "SELECT "
                + columns
                + " FROM "
                + tableNames
                + whereClause;
        //execute command
        try
        { 
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println(command);
            ResultSet result = statement.executeQuery(command);
            return result;
        }
        catch(SQLException e)
        {
            System.out.println("ERROR: " + e.getMessage() + " getRows");
        }
        closeStatement(statement);
        return null;
    }
    
    /**
     * A left outer join. 
     * only works when foreign keys have the same name. 
     * Example: "SELECT " + columns + " FROM " + table[0] + " LEFT OUTER JOIN " + table[1] 
     * + "ON" + table[0] + "." + comparedAtributes[0] + " = " + table[1] + "." + comparedAtributes[0]
                
     * @param columns
     * @param tables
     * @param comparedAttributes list of compared attributes. should be one less than tables
     * @param whereClause
     * @return 
     */
    protected static ResultSet getRowsLOJ(String columns, String[] tables, String[] comparedAttributes, String whereClause)
    {
        if(connection == null)
        {
            return null;
        }

        //set up the command
        String command;
        command = "SELECT "
                + columns
                + " FROM ";
        
        //check if you have the right amount of attributes
        if(comparedAttributes.length == tables.length - 1)
        {
            //start the left outer join 
            for(int i = 0; i < tables.length; i++)
            {
                if(i < 1)
                {
                    command = command.concat(tables[i]);
                }
                else
                {
                    command = command.concat(" LEFT OUTER JOIN ")
                            .concat(tables[i]);
                    //left side of on
                    command = command.concat(" ON ")
                            .concat(tables[i-1])
                            .concat(".")
                            .concat(comparedAttributes[i-1])
                            .concat(" = ");
                    //right side of on
                    command = command.concat(tables[i])
                            .concat(".")
                            .concat(comparedAttributes[i-1]);
                    
                }
                
            }
        }
        else
        {
            System.out.println("wrong amount of attributes! needs to be one less than the table length");
        }
        command += whereClause;
        
        //execute command
        Statement statement = null;
        statements.add(statement);
        try
        { 
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println(command);
            ResultSet result = statement.executeQuery(command);
            return result;
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " getRowsLOJ");
        }
        closeStatement(statement);
        return null;
    }
    
    /**
     * A right outer join. 
     * Only works when foreign keys have the same name. 
     * How it looks:"SELECT " + columns + " FROM " + table[0] + " RIGHT OUTER JOIN " + table[1] 
     * + "ON" + table[0] + "." + comparedAtributes[0] + " = " + table[1] + "." + comparedAtributes[0]
     * @param columns
     * @param tables
     * @param comparedAttributes list of compared attributes. should be one less than tables
     * @param whereClause
     * @return 
     */
    protected static ResultSet getRowsROJ(String columns, String[] tables, String[] comparedAttributes, String whereClause)
    {
        if(connection == null)
        {
            return null;
        }
        
        //setting up the command
        String command;
        command = "SELECT "
                + columns
                + " FROM ";
        
        //check if you have the right amount of attributes
        if(comparedAttributes.length == tables.length - 1)
        {
            //start the left outer join 
            for(int i = 0; i < tables.length; i++)
            {
                if(i < 1)
                {
                    command = command.concat(tables[i]);
                }
                else
                {
                    command = command.concat(" RIGHT OUTER JOIN ")
                            .concat(tables[i]);
                    //left side of on
                    command = command.concat(" ON ")
                            .concat(tables[i-1])
                            .concat(".")
                            .concat(comparedAttributes[i-1])
                            .concat(" = ");
                    //right side of on
                    command = command.concat(tables[i])
                            .concat(".")
                            .concat(comparedAttributes[i-1]);
                    
                }
                
            }
        }
        else
        {
            System.out.println("wrong amount of attributes! needs to be one less than the table length");
        }
        command += whereClause;
        
        //execute command
        Statement statement = null;
        statements.add(statement);
        try
        { 
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println(command);
            ResultSet result = statement.executeQuery(command);
            return result;
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " getRowsROJ");
        }
        closeStatement(statement);
        return null;
    }
    
    /**
     * Checks if a row exists. 
     * Looks like this:"SELECT " + columns + " FROM " + tableName + " " + whereClause;
     * @param tableName
     * @param columns
     * @param whereClause
     * @return 
     */
    protected static Boolean checkForRow(String tableName, String columns, String whereClause)
    {
        if(connection == null)
        {
            return false;
        }
        
        // Create a Statement object.
        Statement statement = null;
        statements.add(statement);
        //set up the command
        String command = "SELECT "
                + columns
                + " FROM "
                + tableName
                + " "
                + whereClause;
        
        //execute command
        try
        { 
            statement = connection.createStatement();
            System.out.println(command);
            ResultSet result = statement.executeQuery(command);
            boolean foundRow = result.next();
            closeStatement(statement);
            return foundRow;
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " checkForRow could not find row");
        }
        closeStatement(statement);
        return false;
    }
    
    /**
     * Displays a table's attribute names (column names) in console. 
     * Best to use at the start of a result and only once for each table
     * @param result 
     */
    protected static void displayColumnNames(ResultSet result)
    {
        if(connection == null)
        {
            return;
        }
        
        try
        {
            int currentColumnCount = 1;
            int columnCount;
            ResultSetMetaData metaData = result.getMetaData();
            columnCount = metaData.getColumnCount();
           
            while(currentColumnCount <= columnCount)
            {
                System.out.print(metaData.getColumnName(currentColumnCount) + ", ");
                currentColumnCount++;
                
            }
            System.out.println("");
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " displayColumnNames");
        }
    }
    
    /**
     * Attempts to display your result in the console. 
     * @param result 
     */
    protected static void displayResult(ResultSet result)
    {
        if(connection == null)
        {
            return;
        }
        
        try
        {
            int currentColumnCount = 1;
            int columnCount;
            ResultSetMetaData metaData = result.getMetaData();
            columnCount = metaData.getColumnCount();
            System.out.println("");
            
            //display names for columns one time at the start
            try
            {
                if(result.getRow() < 2)
                {
                    displayColumnNames(result);
                }
            }
            catch(SQLException sqlE)
            {
                System.out.println("ERROR: " + sqlE.getMessage() + " could not get column names in displayResult");
                System.out.println("Make sure statment allows ResultSet.TYPE_SCROLL_SENSITIVE");
            }
            
            //diplays values while they exist
            while(currentColumnCount <= columnCount)
            {
                System.out.print(result.getString(currentColumnCount) + ", ");
                currentColumnCount++;
                
            }
            System.out.println("");
        }
        catch(SQLException sqlE)
        {
            System.out.println("ERROR: " + sqlE.getMessage() + " displayResult");
        }
    }
}
