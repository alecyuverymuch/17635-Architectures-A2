import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log
{
    String serviceName;
    FileWriter fileWriter;

    public Log(String service)
    {
        this.serviceName = service;
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
            this.fileWriter = new FileWriter("/var/logs/logs.txt",true);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public String writeFile(String logMessage, String username)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            //writing logs to file
            this.fileWriter.write("\n"+date + " | Log Service:" + this.serviceName + " | " + logMessage+" | USERNAME:"+username);
            this.fileWriter.flush();
            return "Log entered";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "Failed to log";

        }
    }


}