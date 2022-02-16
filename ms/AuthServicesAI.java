import java.rmi.*;

public interface AuthServicesAI extends java.rmi.Remote 
{
    String CreateUser(UserCredentials credentials) throws RemoteException;
    Boolean AuthenticateUser(UserCredentials credentials) throws RemoteException;
}
