import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {

    public NodeImpl  findSuccessor (int key, boolean traceFlag) throws RemoteException;
    public NodeImpl  findPredecessor (int key) throws RemoteException;
    public NodeImpl  closestPrecedingFinger (int key) throws RemoteException;
    public NodeImpl  successor () throws RemoteException;
    public NodeImpl  predecessor  () throws RemoteException;
    public boolean join (NodeImpl nodeURL) throws RemoteException;
    public boolean joinFinished (String nodeURL) throws RemoteException;
    public boolean insert (String word, String definition) throws RemoteException;
    public String  lookup (String word) throws RemoteException;
    public String  printFingerTable() throws RemoteException;
    public String  printDictionary() throws RemoteException;
}