import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
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
    public HashMap<String,String> dictionary = new HashMap<>();

    public NodeImpl(String nodeURL, int id){
        this.id = id;
        this.nodeUrl=nodeURL;
        createFingerTable();
    }

    public boolean isInRange(int start, int end, int key){
//        if (key>start){
//            if (key<end)
//                return true;
//            else {
//                if (start > end)
//                    return true;
//                else
//                    return false;
//            }
//        }
//        else if (key<start){
//            if(start>end){
//                if(key<end)
//                    return true;
//                else
//                    return false;
//            }
//            else
//                return false;
//
//        }
//        return false;
        if (start<end){
            if(key>start && key<end)
                return true;
        }
        if (start>end){ //eg: start=5, end=2
            if (key<start && key<end)//eg: key=0
                return true;
            if (key>start)//eg: key=6
                return true;
        }
        if (start==end && start!=key && end!=key)
            return true;
        return false;

    }
    public boolean isInRangeIncStart(int start, int end, int key){
        if (start<end){
            if(key>start && key<=end)
                return true;
        }
        if (start>end){
            if (key<start && key<=end)
                return true;
            if (key>start)
                return true;
        }
        if (start==end && (end==key))//shouldnt it be opposite?
            return true;
        return false;

    }

    public boolean isInRangeIncEnd(int start, int end, int key){
        if (start<end){
            if(key>start && key<end)
                return true;
        }
        if (start>end){
            if (key<start && key<end)
                return true;
            if (key>start)
                return true;
        }
        if (start==end && start==key)//shouldnt it be opposite?
            return true;
        return false;

    }


    private void createFingerTable() {
        for(int i=0;i<m;i++) {
            finger.add(new Finger(this.nodeUrl, id + (int) Math.pow(2, i)));//here also need mod
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
                System.out.println("Not node-0, joining "+this.nodeUrl+" to the DHT.......");
                //RMI object for node URL
                initFingerTable(nodeURL);
                updateOthers();
                //move keys in (predecessor,n] from successor
            }
            else{
                System.out.println("Node-0 joining to the DHT.......");
                predecessor = this.nodeUrl;
                successor=this.nodeUrl;
            }
            System.out.println("Joined successfully!!!");
            return true;

        }catch (Exception e){
            System.out.println("Exception occured while joining....");
            e.printStackTrace();
            lock.unlock();
            return false;
        }
        finally {
            return true;
        }

    }
    public int getCounter() throws RemoteException{
        counterLock.lock();
        try {
            this.counter++;//do we need to check m here?
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

    public void updateOthers() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println(this.nodeUrl+" ------ updating others finger tables");

        for (int i=0;i<m;i++){
            System.out.println("Calling  findPredecessor from node"+this.nodeUrl+" for node key --- " + modOf31(this.id - (int) Math.pow(2,i) + 1) +"in updateOthers");
                                                                                                                                                                                   Node p = (Node) Naming.lookup(pURL);
            p.updateFingerTable(this.nodeUrl,i);
        }

    }

    public void updateFingerTable(String nodeURL, int i) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("Updating finger table for ---- "+ this.nodeUrl);
        Node node = (Node) Naming.lookup(nodeURL);
        String fingerIdUrl = finger.get(i).node;
        Node fingerIdNode = (Node) Naming.lookup(fingerIdUrl);
        id = node.getNodeId();
        int fingerId = fingerIdNode.getNodeId();
        if (isInRangeIncStart(finger.get(i).start,fingerId,id)){
            finger.get(i).node=nodeURL;
            String pUrl = predecessor;
            Node p = (Node) Naming.lookup(pUrl);
            p.updateFingerTable(nodeURL,i);
        }
        System.out.println("Finished updating finger table for ---- "+ this.nodeUrl + "in updateFingerTable");
        printFingerTable();
    }

    public void initFingerTable(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Running initFingerTable for node --- " + nodeURL);
        Node node0 = (Node) Naming.lookup(nodeURL);
        System.out.println("Node "+this.nodeUrl+" finger table size is "+finger.size());
        finger.get(0).node = node0.findSuccessor(finger.get(0).start, false);
        this.successor = finger.get(0).node;
        Node nodeSuccessor = (Node) Naming.lookup(this.successor);
        this.predecessor=nodeSuccessor.predecessor();
//        this.successor.predecessor=this;
        nodeSuccessor.setPredecessor(this.nodeUrl);
        for(int i =0;i<m-1;i++){
            Node fingerNodei = (Node) Naming.lookup(finger.get(i).node);
            if (isInRangeIncEnd(id,fingerNodei.getNodeId(),finger.get(i+1).start)){
                finger.get(i+1).node=finger.get(i).node;

            }
            else {
                System.out.println("finger.get(i+1).node is "+ finger.get(i+1).node);
                Node fingerNodeiPlusOne = (Node) Naming.lookup(finger.get(i+1).node);
                finger.get(i+1).node=node0.findSuccessor(fingerNodeiPlusOne.getNodeId(),false);
            }
        }
        System.out.println("Completed initfingerTable for "+this.nodeUrl);
        printFingerTable();
    }

    public String findSuccessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Running FINDSUCCESSOR for node key --- " + key);
        System.out.println("Calling  findPredecessor from node"+this.nodeUrl+" for node key --- " + key +"from findsuccessor");
        String node = findPredecessor(key);
        System.out.println("The successor for ---"+ key+"--- is ---" + node);

        return node;

    }
    public String  findPredecessor (int key) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Running FINDPREDECESSOR in node " + this.nodeUrl+ "for key --- " + key);
        String nodeURL = this.nodeUrl;
        String nodeSuccessorURL = this.successor;
        System.out.println(this.successor);
        //Add RMI objects node from nodeURL and
        Node node = (Node) Naming.lookup(nodeURL);
        // Add RMI object nodeSuccessor from nodeSuccessorURL
        Node nodeSuccessor = (Node) Naming.lookup(nodeSuccessorURL);
        while (!isInRangeIncEnd(node.getNodeId(),nodeSuccessor.getNodeId(),key)){
            //Add RMI object node from nodeURL (update below)

            nodeURL = node.closestPrecedingFinger(key);
            node = (Node) Naming.lookup(nodeURL);

            nodeSuccessorURL = node.successor();
            nodeSuccessor = (Node) Naming.lookup(nodeSuccessorURL);
        }
        System.out.println("PREDECESSOR for node key --- " + key+" --- is "+nodeURL);

        return nodeURL;
    }


    public String  closestPrecedingFinger (int key) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Finding  closestPrecedingFinger in node ---"+this.nodeUrl+" for key --- "+key);
        for (int i =m-1;i>=0;i--){
            //Add RMI object fingerNodei from finger.get(i).node
            Node fingerNodei = (Node) Naming.lookup(finger.get(i).node);
            if(isInRange(this.id,key,fingerNodei.getNodeId())){
                System.out.println("ClosestPrecedingFinger in node ---" + this.nodeUrl + " for key --- " + key +"is ---"+finger.get(i).node);
                return finger.get(i).node;
            }
        }
        System.out.println("ClosestPrecedingFinger in node ---" + this.nodeUrl + " for key --- " + key +"is ---"+this.nodeUrl);
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
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        System.setSecurityManager (new SecurityManager ());
        Registry registry = null;
        String url;
        int port;
        if ( args.length ==2 ) {
            //if you send 2 arguments, uses them for registry to check node0 and for creating new node.
            url=args[0];
            port=Integer.parseInt(args[1]);
            registry = LocateRegistry.getRegistry(url,port);
        }
        else if ( args.length ==3){
            //if you send 2 arguments, uses args[0] and args[1] as url and port for registry to check node0 and args[1] and args[2] as url and port for creating new node.
            url=args[2];
            port=Integer.parseInt(args[1]);
            registry = LocateRegistry.getRegistry(args[0],port);
        }
        else if ( args.length ==4){
            //if you send 2 arguments, uses args[0] and args[1] as url and port for registry to check node0 and args[2] and args[3] as url and port for creating new node.
            url=args[2];
            port=Integer.parseInt(args[3]);
            registry = LocateRegistry.getRegistry(args[0],Integer.parseInt(args[1]));
        }
        else {
            //if you send no arguments, uses the default arguments for creating new node and for registry to check node0
            url = "localhost";
            port=1099;
            registry = LocateRegistry.getRegistry();
        }
        int nodeId;
        try{
            Node node0 = (Node)registry.lookup("node0");
            nodeId = node0.getCounter();
            String nodeUrl = "//"+url+":"+port+"/node"+nodeId;
            System.out.println("This is not node-0 ---- "+ nodeUrl);
            NodeImpl newNode = new NodeImpl("node"+nodeId, nodeId);
            Node nodeStub = (Node) UnicastRemoteObject.exportObject(newNode, nodeId);
            registry.bind("node"+nodeId,nodeStub);
            boolean res = newNode.join("node0");
            if (res)
                newNode.joinFinished("node0");
        } catch (NotBoundException e){
            //node0 not created
            nodeId =0;
            String nodeUrl = "//"+url+":"+port+"/node"+nodeId;
            System.out.println("This is node-0 ---"+nodeUrl);
            NodeImpl newNode = new NodeImpl("node0",nodeId);
            Node nodeStub = (Node) UnicastRemoteObject.exportObject(newNode, 0);
            registry.bind("node"+nodeId,nodeStub);
            boolean res = newNode.join(null);
            if (res)
                newNode.joinFinished("node0");
        }
    }


}
