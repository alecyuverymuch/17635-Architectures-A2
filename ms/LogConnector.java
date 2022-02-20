import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LogConnector{

    private LogServiceAI logger;
    private String serviceName;

    public LogConnector(String serviceName){
        //Code to instantiate logger
        //Return
        try{
            // Get the registry entry for AuthServices service
            Properties registry = new Properties();
            registry.load(new FileReader("registry.properties"));
            String entry = registry.getProperty("LogService");
            String host = entry.split(":")[0];
            String port = entry.split(":")[1];
            // Get the RMI registry
            Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            LogServiceAI obj = (LogServiceAI)reg.lookup("LogService");
            logger = obj;
            serviceName = serviceName;
        }catch (Exception e){
            //handle
        }
    }

    private String buildLogString(String api, String user){
        String separator= "--";
        //format the time
        return new StringBuilder().append(serviceName).append(separator).append(System.currentTimeMillis()).append(separator).append(api).append(separator).append(user).toString();
    }

    public void log(String logString){
        try{
            logger.writeFile(logString);
        }catch (Exception e){

        }
    }

    public void log(String apicall, String user){
        //Make the string and call logger.write(...
        log(buildLogString(apicall,user));
    }
}