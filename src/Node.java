import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {

    public String  findSuccessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException;
    public String  findPredecessor (int key) throws RemoteException, MalformedURLException, NotBoundException;
    public int getNodeId() throws RemoteException;
    public String  closestPrecedingFinger (int key) throws RemoteException, MalformedURLException, NotBoundException;
    public String  successor () throws RemoteException;
    public String  predecessor  () throws RemoteException;
    public boolean join (String nodeURL) throws RemoteException;
    public boolean joinFinished (String nodeURL) throws RemoteException;
    public boolean insert (String word, String definition) throws RemoteException;
    public String  lookup (String word) throws RemoteException;
    public void  printFingerTable() throws RemoteException;
    public String  printDictionary() throws RemoteException;
    public void setPredecessor(String nodeUrl) throws RemoteException;
    public void setSuccessor(String nodeUrl) throws RemoteException;
    public int getCounter() throws RemoteException;

    public void updateFingerTable(String nodeUrl, int i) throws RemoteException, NotBoundException, MalformedURLException;

    void initFingerTable(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException;

    void updateOthers() throws RemoteException, MalformedURLException, NotBoundException;

    void addToNodeList(int id) throws RemoteException;

    String getPredecessorOf(int id) throws RemoteException;

    String getSuccessorOf(int id) throws RemoteException;
}