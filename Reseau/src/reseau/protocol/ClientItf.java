package reseau.protocol;

/**
 * Created by nathan on 09/12/15.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientItf extends Remote
{
    void afficherTexte(String texte) throws RemoteException;
    String getId() throws RemoteException;
    boolean getHistoriqueCharge() throws RemoteException;
    void historiqueLocal(List<String> historiqueLocal, int nombreHistoriqueLocal) throws RemoteException;
    void historiqueGlobal(String nomFichier) throws RemoteException;
}
