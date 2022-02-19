import java.rmi.*;

public interface AuthServicesAI extends java.rmi.Remote 
{
    String CreateUser(UserCredentials credentials) throws RemoteException;
    String AuthenticateUser(UserCredentials credentials) throws RemoteException;
    String AuthenticateToken(String token) throws RemoteException;
    void ExpireToken(String token) throws RemoteException;
}
