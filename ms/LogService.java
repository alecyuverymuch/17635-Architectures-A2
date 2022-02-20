import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import java.io.*;

public class LogService extends UnicastRemoteObject implements LogServiceAI
{
    FileWriter myWriter;

    public LogService() throws RemoteException
    {
        try
        {
            File myObj = new File("/logs/logs.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            myWriter = new FileWriter("/logs/logs.txt");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        // What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
        // RMI port. Note that I use rebind rather than bind. This is better as it lets you start
        // and restart without having to shut down the rmiregistry.

        try
        {
            LogService obj = new LogService();

            Registry registry = Configuration.createRegistry();
            registry.bind("LogService", obj);

            String[] boundNames = registry.list();
            System.out.println("Registered services:");
            for (String name : boundNames) {
                System.out.println("\t" + name);
            }
            // Bind this object instance to the name AuthServices in the rmiregistry
            // Naming.rebind("//" + Configuration.getRemoteHost() + ":1099/CreateServices", obj);

        } catch (Exception e) {

            System.out.println("LogServices binding err: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public String writeFile(String log)
    {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            myWriter.write(date + "---" + log+"\n");
            myWriter.flush();
            return "Log entered";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Failed to log";

        }
    }
}