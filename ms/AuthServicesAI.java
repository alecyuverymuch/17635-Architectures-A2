import java.rmi.*;

public interface AuthServicesAI extends java.rmi.Remote 
{
    String CreateUser(UserCredentials credentials) throws RemoteException;
    String AuthenticateUser(UserCredentials credentials) throws RemoteException;
    Boolean AuthenticateToken(String token) throws RemoteException;
    void ExpireToken(String token) throws RemoteException;
}
