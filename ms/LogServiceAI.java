import java.rmi.*;

public interface LogServiceAI extends java.rmi.Remote
{
    String writeFile(String log) throws RemoteException;
}
