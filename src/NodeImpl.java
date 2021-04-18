import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeImpl extends Remote {

    public Node  findSuccessor (int key, boolean traceFlag) throws RemoteException;
    public Node  findPredecessor (int key) throws RemoteException;
    public Node  closestPrecedingFinger (int key) throws RemoteException;
    public Node  successor () throws RemoteException;
    public Node  predecessor  () throws RemoteException;
    public boolean join (Node nodeURL) throws RemoteException;
    public boolean joinFinished (String nodeURL) throws RemoteException;
    public boolean insert (String word, String definition) throws RemoteException;
    public String  lookup (String word) throws RemoteException;
    public String  printFingerTable() throws RemoteException;
    public String  printDictionary() throws RemoteException;
}