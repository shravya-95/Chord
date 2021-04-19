import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {

    public String  findSuccessor (int key, boolean traceFlag) throws RemoteException;
    public String  findPredecessor (int key) throws RemoteException;
    public int getNodeId(String nodeURL) throws RemoteException;
    public String  closestPrecedingFinger (int key) throws RemoteException;
    public String  successor () throws RemoteException;
    public String  predecessor  () throws RemoteException;
    public boolean join (String nodeURL) throws RemoteException;
    public boolean joinFinished (String nodeURL) throws RemoteException;
    public boolean insert (String word, String definition) throws RemoteException;
    public String  lookup (String word) throws RemoteException;
    public String  printFingerTable() throws RemoteException;
    public String  printDictionary() throws RemoteException;
}