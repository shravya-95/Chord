import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Node extends Remote {

    public String  findSuccessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException;
    public String  findPredecessor (int key) throws RemoteException, MalformedURLException, NotBoundException;
    public int getNodeId() throws RemoteException;
    public String getFullUrl() throws RemoteException;
    public String getNodeUrl() throws RemoteException;
    public String  closestPrecedingFinger (int key) throws RemoteException, MalformedURLException, NotBoundException;
    public String  successor () throws RemoteException;
    public String  predecessor  () throws RemoteException;
    public boolean join (String nodeURL) throws RemoteException;
//    public boolean joinFinished (String nodeURL) throws RemoteException;
    public boolean insert (String word, String definition) throws RemoteException;
    public String  lookup (String word) throws RemoteException;
    public void  printFingerTable() throws RemoteException;
    public List<Finger> getFingerTable() throws RemoteException;
    public int getEntriesCount() throws RemoteException;

    public boolean joinLock(String url) throws RemoteException;
    public String  printDictionary() throws RemoteException;
    public void setPredecessor(String nodeUrl) throws RemoteException;
    public void setSuccessor(String nodeUrl) throws RemoteException;
    public int getCounter() throws RemoteException;
    public String printStructure() throws RemoteException, NotBoundException, MalformedURLException;

    public void updateFingerTable(String nodeUrl, int i) throws RemoteException, NotBoundException, MalformedURLException;

    public void initFingerTable(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException;

    public void updateOthers() throws RemoteException, MalformedURLException, NotBoundException;

    public void addToNodeList(int id) throws RemoteException;

    public String getPredecessorOf(int id) throws RemoteException;

    public String getSuccessorOf(int id) throws RemoteException;

    public boolean joinLockRelease(String nodeUrl) throws RemoteException;

    public List<Integer> getNodeList() throws RemoteException;
}