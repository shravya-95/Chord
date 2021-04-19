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
            finger.add(new Finger(this.nodeUrl, id+ (int) Math.pow(2,i) ));
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
                predecessor = this.nodeUrl;
            }
            return true;
        }finally {
            return true;
        }

    }

    @Override
    public boolean joinFinished(String nodeURL) throws RemoteException {
        try{
            lock.unlock();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

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


        for (int i=0;i<m;i++){
            String pURL = findPredecessor(modOf31(this.id - (int) Math.pow(2,i) + 1));
            //Add RMI object p from pURL
            p.updateFingerTable(this.nodeUrl,i);
        }

    }

    private void updateFingerTable(String nodeURL, int i) {
        //Add RMI object node from nodeURL
        String fingerIdUrl = finger.get(i).node;
        //Add RMI object fingerIdNode from fingerIdUrl
        id = node.getNodeId();
        int fingerId = fingerIdNode.getNodeId();
        if (id>= finger.get(i).start && id<fingerId){
            finger.get(i).node=nodeURL;
            String pUrl = predecessor;
            //Add RMI object p from pUrl
            p.updateFingerTable(node,i);
        }
    }

    private void initFingerTable(String nodeURL) throws RemoteException {
        //Add RMI object node from nodeURL
        finger.get(0).node = node.findSuccessor(finger.get(0).start, false);
        // Add RMI object nodeSuccessor from this.sucessor
        this.predecessor=nodeSuccessor.predecessor();
//        this.successor.predecessor=this;
        nodeSuccessor.setPredecessor(this.nodeUrl);
        for(int i =0;i<m-1;i++){
            //Add RMI object fingerNodei from finger.get(i).node
            if(finger.get(i+1).start>this.id && finger.get(i+1).start<=fingerNodei.getNodeId()){
                finger.get(i+1).node=finger.get(i).node;
            }
            else {
                //Add RMI object fingerNodeiPlusOne from finger.get(i+1).node
                finger.get(i+1).node=node.findSuccessor(fingerNodeiPlusOne.getNodeid(),false);
            }
        }
    }

    public String findSuccessor (int key, boolean traceFlag) throws RemoteException{
        String node = findPredecessor(key);
        return node;

    }
    public String  findPredecessor (int key) throws RemoteException{
        String nodeURL = this.nodeUrl;
        String nodeSuccessorURL = this.successor;
        //Add RMI objects node from nodeURL and
        // Add RMI object nodeSuccessor from nodeSuccessorURL
        while (key<node.getNodeId() && key>nodeSuccessor.getNodeId()){
            nodeURL = node.closestPrecedingFinger(key);
            //Add RMI object node from nodeURL (update below)
            node = //RMI Object??;
        }
        return node;
    }
    public String  closestPrecedingFinger (int key) throws RemoteException{
        for (int i =0;i<m;i++){
            //Add RMI object fingerNodei from finger.get(i).node
            if(fingerNodei.getNodeId()>this.id && fingerNodei.getNodeId()<key )
                return finger.get(i).node;
        }
        return this.nodeUrl;
    }

    @Override
    public String successor() throws RemoteException {
        return this.successor;
    }

    @Override
    public String predecessor() throws RemoteException {
        return this.predecessor;
    }

    public int getNodeId() throws RemoteException{
        return this.id;
    }


}
