import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class AuthServices extends UnicastRemoteObject implements AuthServicesAI {
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = Configuration.getJDBCConnection();

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = Configuration.MYSQL_PASSWORD;

    private static Map<String, UserCredentials> sessions = new HashMap<String, UserCredentials>();
    LogConnector logConnector = new LogConnector("AuthService");
    // Do nothing constructor
    public AuthServices() throws RemoteException {}

    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 

        try 
        { 
            AuthServices obj = new AuthServices();

            Registry registry = Configuration.createRegistry();
            registry.bind("AuthServices", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
            // Bind this object instance to the name AuthServices in the rmiregistry 
            // Naming.rebind("//" + Configuration.getRemoteHost() + ":1099/CreateServices", obj);
        } catch (Exception e) {

            System.out.println("AuthServices binding err: " + e.getMessage()); 
            e.printStackTrace();
        } 

    } // main

    public String createUser(UserCredentials credentials) throws RemoteException 
    {
        // Local declarations

        Connection conn = null;		                 // connection to the orderinfo database
        Statement stmt = null;		                 // A Statement object is an interface that represents a SQL statement.
        String ReturnString = "User Created";	     // Return string. If everything works you get an 'OK' message
        							                 // if not you get an error string
        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            stmt = conn.createStatement();
            
            String sql = "INSERT INTO users(user_name, password) VALUES (\""+credentials.getUsername()+"\",\""+credentials.getPassword()+"\")";

            // execute the update

            stmt.executeUpdate(sql);
            logConnector.log("User signed up:"+credentials.getUsername());
            // clean up the environment

            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {
            ReturnString = e.toString();
        } 
        
        return(ReturnString);

    }

    public String authenticateUser(UserCredentials credentials) throws RemoteException 
    {
        // Local declarations
        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        Boolean authenticated = false;	// Return boolean. True when authenticated
        String token = null;

        try
        {
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
            sql = "SELECT * FROM users where user_name='" + credentials.getUsername()+"'";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set. Note there should only be one for this method.
            // I used a while loop should there every be a case where there might be multiple
            // orders for a single ID.

            while(rs.next())
            {
                //Retrieve by column name
                int id  = rs.getInt("user_id");
                String username = rs.getString("user_name");
                String password = rs.getString("password");

                if (password.equals(credentials.getPassword()))
                {
                    authenticated = true;
                    token = CreateToken(credentials);
                    sessions.put(token, credentials);
                    logConnector.log("User logged in:"+credentials.getUsername());
                }
            }

            //Clean-up environment

            rs.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {
            // TODO: Logging
            authenticated = false;
        } 

        return authenticated ? token : null;
    }

    public String authenticateToken(String token) throws RemoteException 
    {
        if (sessions.containsKey(token))
            return sessions.get(token).getUsername();
        
        return null;
    }

    public void expireToken(String token) throws RemoteException
    {
        if (sessions.containsKey(token))
            sessions.remove(token);
    }

    private String CreateToken(UserCredentials credentials) throws Exception
    {
        String sessionString = credentials.getUsername() + credentials.getPassword() + System.currentTimeMillis();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(sessionString.getBytes());
        return new String(messageDigest.digest());
    }
}
