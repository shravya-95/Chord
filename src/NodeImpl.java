import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class NodeImpl implements Node{
    public List<Finger> finger = new ArrayList<>();
    public int id;
    public String nodeUrl;
    public String predecessor;
    public String successor;
    public int m =31;
    ReentrantLock lock = new ReentrantLock();

    public NodeImpl(String nodeURL, int id){
        this.id = id;
        this.nodeUrl=nodeURL;
        createFingerTable();
    }

    private void createFingerTable() {
        for(int i=0;i<m;i++){
            finger.add(new Finger(this, id+ (int) Math.pow(2,i) ));
        }
    }

    private int modOf31(int num){
        if (num>=0)
            return (num%(int)Math.pow(2,31));
        else
            return (num%(int)Math.pow(2,31)+(int)Math.pow(2,31));
    }

    public boolean join (String nodeURL) throws RemoteException{
        lock.lock();
        try {
            if (nodeURL!=null){
                initFingerTable(nodeURL);
                updateOthers();
                //move keys in (predecessor,n] from successor
            }
            else{
                predecessor = this;
            }
            return true;
        }finally {
            joinFinished(nodeURL);
        }

    }

    @Override
    public boolean joinFinished(String nodeURL) throws RemoteException {
        lock.unlock();
        return true;
    }

    @Override
    public boolean insert(String word, String definition) throws RemoteException {
        return false;
    }

    @Override
    public String lookup(String word) throws RemoteException {
        return null;
    }

    @Override
    public String printFingerTable() throws RemoteException {
        return finger.toString();
    }

    @Override
    public String printDictionary() throws RemoteException {
        return null;
    }

    private void updateOthers() throws RemoteException {
        String p;
        for (int i=0;i<m;i++){
            p = findPredecessor(modOf31(this.id - (int) Math.pow(2,i) + 1));
            p.updateFingerTable(this,i);
        }

    }

    private void updateFingerTable(String node, int i) {
        if (node.id>= finger.get(i).start && node.id<finger.get(i).node.id){
            finger.get(i).node=node;
            String p = predecessor;
            p.updateFingerTable(node,i);
        }
    }

    private void initFingerTable(String nodeURL) throws RemoteException {
        finger.get(0).node = nodeURL.findSuccessor(finger.get(0).start, false);
        this.predecessor=this.successor.predecessor;
        this.successor.predecessor=this;
        for(int i =0;i<m-1;i++){
            if(finger.get(i+1).start>this.id && finger.get(i+1).start<=finger.get(i).node.id){
                finger.get(i+1).node=finger.get(i).node;
            }
            else {
                finger.get(i+1).node=nodeURL.findSuccessor(finger.get(i+1).node.id,false);
            }
        }
    }

    public String findSuccessor (int key, boolean traceFlag) throws RemoteException{
        String node = findPredecessor(key);
        return node.successor;

    }
    public String  findPredecessor (int key) throws RemoteException{
        String node = this;
        while (key<node.id && key>node.successor.id){
            node = node.closestPrecedingFinger(key);
        }
        return node;
    }
    public String  closestPrecedingFinger (int key) throws RemoteException{
        for (int i =0;i<m;i++){
            if(finger.get(i).node.id>this.id && finger.get(i).node.id<key )
                return finger.get(i).node;
        }
        return this;
    }

    @Override
    public String successor() throws RemoteException {
        return null;
    }

    @Override
    public String predecessor() throws RemoteException {
        return null;
    }


}
