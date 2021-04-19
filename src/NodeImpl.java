import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class NodeImpl implements Node{
    public List<Finger> finger = new ArrayList<>();
    public int id;
    public String nodeUrl;
    public NodeImpl predecessor;
    public NodeImpl successor;
    public int m =31;

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

    public boolean join (NodeImpl nodeURL) throws RemoteException{

        if (nodeURL!=null){
            initFingerTable(nodeURL);
            updateOthers();
            //move keys in (predecessor,n] from successor
        }
        else{
            predecessor = this;
        }
        return true;
    }

    @Override
    public boolean joinFinished(String nodeURL) throws RemoteException {
        return false;
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
        return null;
    }

    @Override
    public String printDictionary() throws RemoteException {
        return null;
    }

    private void updateOthers() throws RemoteException {
        NodeImpl p;
        for (int i=0;i<m;i++){
            p = findPredecessor(modOf31(this.id - (int) Math.pow(2,i-1) + 1));
            p.updateFingerTable(this,i);
        }

    }

    private void updateFingerTable(NodeImpl node, int i) {
        if (node.id>= finger.get(i).start && node.id<finger.get(i).node.id){
        }
    }

    private void initFingerTable(NodeImpl nodeURL) throws RemoteException {
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

    public NodeImpl findSuccessor (int key, boolean traceFlag) throws RemoteException{
        NodeImpl node = findPredecessor(key);
        return node.successor;

    }
    public NodeImpl  findPredecessor (int key) throws RemoteException{
        NodeImpl node = this;
        while (key<node.id && key>node.successor.id){
            node = node.closestPrecedingFinger(key);
        }
        return node;
    }
    public NodeImpl  closestPrecedingFinger (int key) throws RemoteException{
        for (int i =0;i<m;i++){
            if(finger.get(i).node.id>this.id && finger.get(i).node.id<key )
                return finger.get(i).node;
        }
        return this;
    }

    @Override
    public NodeImpl successor() throws RemoteException {
        return null;
    }

    @Override
    public NodeImpl predecessor() throws RemoteException {
        return null;
    }

}
