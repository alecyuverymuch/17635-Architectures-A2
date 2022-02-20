import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.sql.*;

public class DeleteServices extends UnicastRemoteObject implements DeleteServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    // Do nothing constructor
    public DeleteServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
        // What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
        // RMI port. Note that I use rebind rather than bind. This is better as it lets you start
        // and restart without having to shut down the rmiregistry. 

        try 
        { 
            DeleteServices obj = new DeleteServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("DeleteServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }

        } catch (Exception e) {

            System.out.println("DeleteServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 

    } // main

    public String deleteOrder(String orderid) throws RemoteException
    {
      	// Local declarations

        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = "";	// Return string. 

        try
        {
            // TODO: Logging Function

            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            // System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            String sql;
            sql = "DELETE FROM orders where order_id=" + orderid;
            stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {

            ReturnString = e.toString();

        } 

        return(ReturnString);

    } //retrieve order by id
}