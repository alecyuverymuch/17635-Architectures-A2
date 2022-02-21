import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log
{
    String serviceName;

    public Log(String service)
    {
        this.serviceName = service;
    }

    public FileWriter createLogWriter()
    {
        FileWriter fileWriter = null;
        try
        {
            //creating the log file
            File myObj = new File("/var/logs/logs.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            //opening the file writer
            fileWriter = new FileWriter("/var/logs/logs.txt",true);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            return fileWriter;
        }
    }

    public String writeFile(FileWriter fw,String logMessage, String username)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            //writing logs to file
            fw.write("\n"+date + " | Log Service:" + this.serviceName + " | " + logMessage+" | USERNAME:"+username);
            fw.flush();
            return "Log entered";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Failed to log";

        }
    }


}