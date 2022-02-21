import java.rmi.*;

public interface AuthServicesAI extends java.rmi.Remote 
{
    String createUser(UserCredentials credentials) throws RemoteException;
    String authenticateUser(UserCredentials credentials) throws RemoteException;
    String authenticateToken(String token) throws RemoteException;
    void expireToken(String token) throws RemoteException;
}
