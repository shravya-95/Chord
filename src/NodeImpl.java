import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
    private ReentrantLock lock = new ReentrantLock();
    public int counter=0;
    public ReentrantLock counterLock =  new ReentrantLock();

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
    public int getCounter() throws RemoteException{
        counterLock.lock();
        try {
            this.counter++;
            return this.counter;
        }finally {
            counterLock.unlock();
        }
    }

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

    public boolean insert(String word, String definition) throws RemoteException {
        return false;
    }

    public String lookup(String word) throws RemoteException {
        return null;
    }

    public String printFingerTable() throws RemoteException {
        return finger.toString();
    }

    public String printDictionary() throws RemoteException {
        return null;
    }

    @Override
    public void setPredecessor(String nodeUrl) throws RemoteException {

    }

    @Override
    public void setSuccessor(String nodeUrl) throws RemoteException {

    }

    private void updateOthers() throws RemoteException, MalformedURLException, NotBoundException {


        for (int i=0;i<m;i++){
            String pURL = findPredecessor(modOf31(this.id - (int) Math.pow(2,i) + 1));
            Node p = (Node) Naming.lookup(pURL);
            p.updateFingerTable(this.nodeUrl,i);
        }

    }

    public void updateFingerTable(String nodeURL, int i) throws RemoteException, NotBoundException, MalformedURLException {
        Node node = (Node) Naming.lookup(nodeURL);
        String fingerIdUrl = finger.get(i).node;
        Node fingerIdNode = (Node) Naming.lookup(fingerIdUrl);
        id = node.getNodeId();
        int fingerId = fingerIdNode.getNodeId();
        if (id>= finger.get(i).start && id<fingerId){
            finger.get(i).node=nodeURL;
            String pUrl = predecessor;
            Node p = (Node) Naming.lookup(pUrl);
            p.updateFingerTable(nodeURL,i);
        }
    }

    private void initFingerTable(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException {
        Node node = (Node) Naming.lookup(nodeURL);
        finger.get(0).node = node.findSuccessor(finger.get(0).start, false);
        Node nodeSuccessor = (Node) Naming.lookup(this.successor);
        this.predecessor=nodeSuccessor.predecessor();
//        this.successor.predecessor=this;
        nodeSuccessor.setPredecessor(this.nodeUrl);
        for(int i =0;i<m-1;i++){
            Node fingerNodei = (Node) Naming.lookup(finger.get(i).node);
            if(finger.get(i+1).start>this.id && finger.get(i+1).start<=fingerNodei.getNodeId()){
                finger.get(i+1).node=finger.get(i).node;
            }
            else {
                Node fingerNodeiPlusOne = (Node) Naming.lookup(finger.get(i+1).node);
                finger.get(i+1).node=node.findSuccessor(fingerNodeiPlusOne.getNodeId(),false);
            }
        }
    }

    public String findSuccessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException {
        String node = findPredecessor(key);
        return node;

    }
    public String  findPredecessor (int key) throws RemoteException, MalformedURLException, NotBoundException {
        String nodeURL = this.nodeUrl;
        String nodeSuccessorURL = this.successor;
        //Add RMI objects node from nodeURL and
        Node node = (Node) Naming.lookup(nodeURL);
        // Add RMI object nodeSuccessor from nodeSuccessorURL
        Node nodeSuccessor = (Node) Naming.lookup(nodeSuccessorURL);
        while (key<node.getNodeId() && key>nodeSuccessor.getNodeId()){
            nodeURL = node.closestPrecedingFinger(key);
            //Add RMI object node from nodeURL (update below)
            node = (Node) Naming.lookup(nodeURL);
        }
        return nodeURL;
    }

    public int getNodeId(String nodeURL) throws RemoteException {
        return 0;
    }

    public String  closestPrecedingFinger (int key) throws RemoteException, MalformedURLException, NotBoundException {
        for (int i =0;i<m;i++){
            //Add RMI object fingerNodei from finger.get(i).node
            Node fingerNodei = (Node) Naming.lookup(finger.get(i).node);
            if(fingerNodei.getNodeId()>this.id && fingerNodei.getNodeId()<key )
                return finger.get(i).node;
        }
        return this.nodeUrl;
    }

    public String successor() throws RemoteException {
        return this.successor;
    }

    public String predecessor() throws RemoteException {
        return this.predecessor;
    }

    public int getNodeId() throws RemoteException{
        return this.id;
    }


}
