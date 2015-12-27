package reseau.protocol;

/**
 * Created by nathan on 09/12/15.
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerItf extends Remote
{
    boolean connexion(ClientItf refClient) throws RemoteException;
    void distribuerMsg(String message, String id) throws RemoteException;
    void deconnexion( ClientItf refClient ) throws RemoteException;
}
