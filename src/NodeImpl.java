import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class NodeImpl implements Node{
    public List<Finger> finger = new ArrayList<>();
    public int id;
    public String nodeUrl;
    public String predecessor;
    public String successor;
    public int m =31;
    private ReentrantLock lock = new ReentrantLock();
    private ReentrantLock lockToJoin = new ReentrantLock();
    public int counter=0;
    public ReentrantLock counterLock =  new ReentrantLock();
    public HashMap<String,String> dictionary = new HashMap<>();
    public String fullUrl;
    public List<Integer> nodeList = new ArrayList<>();
    public String bootstrapHashValue=null;
    public String logfile = "";
    public Registry registry =null;

    /**
     *
     * @param nodeURL hashed url in string for rmi use
     * @param id hashed url
     * @param fullUrl node url without hashing
     * @param bootstrapHashValue node0's hashed value
     */
    public NodeImpl(String nodeURL, int id, String fullUrl, String bootstrapHashValue, Registry registry){
        this.nodeUrl=nodeURL;
        this.fullUrl = fullUrl;
        this.id = id;
        this.bootstrapHashValue = bootstrapHashValue;
        this.registry=registry;
        this.logfile=this.fullUrl.substring(this.fullUrl.indexOf("node"))+"_logfile";
        createFingerTable();

    }



    public boolean isInRange(int start, int end, int key){
        if (start<end){
            if(key>start && key<end){
//                System.out.println("1. Inside isInRange"+start+"   "+end+"    "+key+" ---- True");
                return true;}
        }
        if (start>end){
            if (key<start && key<end){
//                System.out.println("2. Inside isInRange"+start+"   "+end+"    "+key+" ---- True");
                return true;
            }


            if (key>start){
//                System.out.println("3. Inside isInRange"+start+"   "+end+"    "+key+" ---- True");
                return true;
            }
        }
        if (start==end && start!=key)
            return true;
//        System.out.println("4. Inside isInRange"+start+"   "+end+"    "+key+" ---- False");
        return false;

    }
    public boolean isInRangeIncStart(int start, int end, int key){
//        if (start<end){
//            if(key>start && key<=end)
//                return true;
//        }
//        if (start>end){
//            if (key<start && key<=end)
//                return true;
//            if (key>start)
//                return true;
//        }
//        if (start==end)
//            return true;
//        return false;
        boolean ans = isInRange(start,end,key) || (start==key);
        System.out.println("Inside isInRangeIncStart"+start+"   "+end+"    "+key+" ----"+ans);
        return ans;

    }


    public boolean isInRangeIncEnd(int start, int end, int key){
//        if (start<end){
//            if(key>start && key<end)
//                return true;
//        }
//        if (start>end){
//            if (key<start && key<end)
//                return true;
//            if (key>start)
//                return true;
//        }
//        if (start==end)
//            return true;
//        return false;
        boolean ans = isInRange(start,end,key) || (key==end );
        System.out.println("Inside isInRangeIncEnd"+start+"   "+end+"    "+key+" ----"+ans);
        return ans;


    }


    private void createFingerTable() {
        finger.add(new Finger(null, -1));
        for(int i=1;i<=m;i++) {
//
//            System.out.println("modOf31("+this.id +"," +(int) Math.pow(2, i-1 )+") --- "+modOf31(this.id ,(int) Math.pow(2, i-1)));
            finger.add(new Finger(this.nodeUrl, modOf31(this.id ,(int) Math.pow(2, i-1))));
        }
    }


    public String getFullUrl(){
        return this.fullUrl;
    }

    public String getNodeUrl(){
        return this.nodeUrl;
    }

    public List<Finger> getFingerTable(){
        return this.finger;
    }

    public int getEntriesCount() throws RemoteException{
        return this.dictionary.size();
    }

    public String printStructure() throws RemoteException, NotBoundException, MalformedURLException {
//        Registry registry = LocateRegistry.getRegistry();
        Node node0 = (Node) registry.lookup(this.bootstrapHashValue);
        List<Integer> currNodeList = node0.getNodeList();
        int numNodes = node0.getCounter();//check if it needs -1
        String structure = "";
        for(int i=0;i<this.nodeList.size();i++){
            Node currentNode = (Node)registry.lookup(Integer.toString(nodeList.get(i)));
            Node successorNode = (Node)registry.lookup(currentNode.successor());
            Node predecessorNode = (Node)registry.lookup(currentNode.predecessor());

            structure += "For NODE ID: "+currentNode.getNodeUrl()+"\n-----------------------\n   Key: "+currentNode.getNodeId();
            structure += "\n   Successor: "+successorNode.getFullUrl()+"\n   Predecessor: "+predecessorNode.getFullUrl();
            structure += "\n   Finger table contents: ";
            for (Finger a:currentNode.getFingerTable()){
                structure += "      Finger start: "+a.start+", Finger node: "+a.node;
            }
            structure+="    Number of entries it stores: "+currentNode.getEntriesCount();
            structure+="    Full URL: "+currentNode.getFullUrl();
        }
        return structure;
    }

    private int modOf31(int num1, int num2){
        long sum = num1 + num2;
        if (sum<0)
            sum+= Math.pow(2,31);
        return (int)(sum % Math.pow(2,31));
    }

    public void writeToLog(String rawContent) {
        String fileName = this.logfile;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String content = "CurrentTime : "+ formatter.format(date) +"|"+ "Event Message: "+rawContent;

        try {

            File oFile = new File(fileName);
            if (!oFile.exists()) {
                oFile.createNewFile();
            }
            if (oFile.canWrite()) {
                BufferedWriter oWriter = new BufferedWriter(new FileWriter(fileName, true));
                oWriter.write(content);
                oWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean join (String nodeURL) throws RemoteException{
        try {
            if (nodeURL!=null){
                System.out.println("Not node-0, joining "+this.fullUrl+" to the DHT.......");
                writeToLog("Not node-0, joining "+this.fullUrl+" to the DHT.......");
                //RMI object for node URL
                System.out.println("Bootstrap uril in "+this.nodeUrl+" is ---"+this.bootstrapHashValue);
                Node node0 = (Node) registry.lookup(this.bootstrapHashValue);
                node0.addToNodeList(this.id);

                initFingerTable(this.bootstrapHashValue);
                updateOthers();
                //move keys in (predecessor,n] from successor
            }
            else{
                System.out.println("Node-0 joining to the DHT......."+this.id);
                writeToLog("Not node-0, joining "+this.fullUrl+" to the DHT.......");
                predecessor = this.nodeUrl;
                successor=this.nodeUrl;
                this.nodeList.add(this.id);
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
    public boolean joinLockRelease(String nodeUrl) throws RemoteException{
        if(lockToJoin.isLocked())
            lockToJoin.unlock();
        return true;
    }

    @Override
    public List<Integer> getNodeList() throws RemoteException {
        return this.nodeList;
    }

    public boolean joinLock(String nodeUrl) throws RemoteException{
        lockToJoin.lock();
        return true;
    }


    public boolean insert(String word, String definition) throws RemoteException {
        this.dictionary.put(word, definition);
        return true;
    }

    public String lookup(String word) throws RemoteException {
        if(this.dictionary.containsKey(word))
            return this.dictionary.get(word);
        return word+" not found";
    }

    public void printFingerTable() throws RemoteException, MalformedURLException, NotBoundException {
        String fingerTable="Finger Table: \n";
        for (Finger a:finger){
            if (a.node==null)
                continue;
            Node fingerNode = (Node)registry.lookup(a.node);
            fingerTable+=a.start + ", "+fingerNode.getNodeId()+", "+fingerNode.getFullUrl();
        }
    }

    public String printDictionary() throws RemoteException {
        String dictContent="";
        for(String key: this.dictionary.keySet()){
            dictContent+=key+": "+this.dictionary.get(key);
        }
        writeToLog("Dictionary content :\n"+dictContent);
        return dictContent;
    }

    @Override
    public void setPredecessor(String nodeUrl) throws RemoteException {
        this.predecessor = nodeUrl;
    }

    @Override
    public void setSuccessor(String nodeUrl) throws RemoteException {
        this.successor=nodeUrl;
    }

    public void updateOthers() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println(this.nodeUrl+" ------ updating others finger tables");

        for (int i=1;i<=m;i++){
//            System.out.println("Calling  findPredecessor from node"+this.nodeUrl+" for node key --- " + modOf31(this.id - (int) Math.pow(2,i-1) + 1) +"in updateOthers");
//            System.out.println(this.nodeUrl+" ------ is updating pred of "+ modOf31(this.id - (int) Math.pow(2,i-1) + 1));

            String pURL = findPredecessor(modOf31(this.id, 1 - (int) Math.pow(2,i-1)), false);
            System.out.println(this.nodeUrl+" ------ is updating pred of mod of -- "+this.id+"  "+(int) Math.pow(2,i-1)+" is -----"+ modOf31(this.id , 1 - (int) Math.pow(2,i-1))+" whihch is ----"+pURL);
//            String pURL = findPredecessor(modOf31(this.id - (int) Math.pow(2,i-1) + 1));
            Node p = (Node) registry.lookup(pURL);

            p.updateFingerTable(this.nodeUrl,i);
        }

    }

    public void addToNodeList(int id) throws RemoteException {
        this.nodeList.add(id);
        Collections.sort(this.nodeList);
    }

    public String getPredecessorOf(int id) throws RemoteException {
        int index = nodeList.indexOf(id);
        if (index==0)
            return (Integer.toString(this.nodeList.get(this.nodeList.size()-1)));
        return (Integer.toString(this.nodeList.get(index-1)));
    }

    @Override
    public String getSuccessorOf(int id) throws RemoteException {
        int index = nodeList.indexOf(id);
        if (index==this.nodeList.size()-1)
            return (Integer.toString(this.nodeList.get(0)));
        return (Integer.toString(this.nodeList.get(index+1)));
    }

    public void updateFingerTable(String nodeURL, int i) throws RemoteException, NotBoundException, MalformedURLException {
        Node node0 = (Node) registry.lookup(this.bootstrapHashValue);
        this.predecessor = node0.getPredecessorOf(this.id);
        this.successor = node0.getSuccessorOf(this.id);
        System.out.println("Updating finger table for ---- "+ this.nodeUrl+"----- called by "+nodeURL);
        Node node = (Node) registry.lookup(nodeURL);
        String fingerIdUrl = finger.get(i).node;
        Node fingerIdNode = (Node) registry.lookup(fingerIdUrl);
        int s_id = node.getNodeId();
        int fingerId = fingerIdNode.getNodeId();
//        System.out.println("Calling  isInRangeIncStart for ---- "+ finger.get(i).start +"    "+ fingerId +"    "+s_id);
//        && this.id!=s_id
        if (isInRangeIncStart(finger.get(i).start,fingerId,s_id) && finger.get(i).start!=fingerId ){
            System.out.println("UPDATING FINGER TABLE OF "+this.nodeUrl+" finger("+i+") node is "+nodeURL);
            finger.get(i).node=nodeURL;
            System.out.println("Predecessor of "+this.nodeUrl+" is "+this.predecessor);
            String pUrl = this.predecessor;
            Node p = (Node) registry.lookup(pUrl);
            p.updateFingerTable(nodeURL,i);
        }
        System.out.println("Finished updating finger table for ---- "+ this.nodeUrl + "in updateFingerTable");
//        this.successor=finger.get(1).node;
        printFingerTable();
    }

    public void initFingerTable(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("Running initFingerTable for node --- " + nodeURL);
        Node node0 = (Node) registry.lookup(this.bootstrapHashValue);
        System.out.println("Node "+this.nodeUrl+" finger table size is "+finger.size());
        finger.get(1).node = node0.findSuccessor(finger.get(1).start, false);
        this.successor = finger.get(1).node;
        Node nodeSuccessor = (Node) registry.lookup(this.successor);
        this.predecessor=nodeSuccessor.predecessor();
        System.out.println("PREDECESSOR FOR "+this.nodeUrl+" is ---"+this.predecessor);
//        this.successor.predecessor=this;
        nodeSuccessor.setPredecessor(this.nodeUrl);
        for(int i =1;i<=m-1;i++){
            Node fingerNodei = (Node) registry.lookup(finger.get(i).node);
            if (isInRangeIncEnd(this.id,fingerNodei.getNodeId(),finger.get(i+1).start)){
                finger.get(i+1).node=finger.get(i).node;

            }
            else {
                System.out.println("finger.get(i+1).node is "+ finger.get(i+1).node);
//                Node fingerNodeiPlusOne = (Node) Naming.lookup(finger.get(i+1).node);
                finger.get(i+1).node=node0.findSuccessor(finger.get(i+1).start,false);
            }
        }
        System.out.println("Completed initfingerTable for "+this.nodeUrl + "predecessor is ---"+this.predecessor+"sucessor is ---"+this.successor);
//        printFingerTable();
    }

    public String findSuccessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException {
        if (traceFlag) writeToLog("Running FINDSUCCESSOR for key --- " + key + "in node ID ----"+this.fullUrl);
        if (traceFlag) writeToLog("Calling  findPredecessor from node --- "+this.fullUrl+" for node key --- " + key +"within findSuccessor");
        String node = findPredecessor(key, traceFlag);
        if (traceFlag) writeToLog("The predecessor for ---"+ key+"--- is ---" + node);
        Node nodePred = (Node) registry.lookup(node);
        if (traceFlag) writeToLog("The successor for ---"+ key+"--- is ---" + nodePred.successor());
        return nodePred.successor();
    }


    public String  findPredecessor (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException {
        if (traceFlag) writeToLog("Running FINDPREDECESSOR in node " + this.fullUrl+ "for key --- " + key);
        String nodeURL = this.nodeUrl;
        String nodeSuccessorURL = this.successor;
        //Add RMI objects node from nodeURL and
        Node node = (Node) registry.lookup(nodeURL);
        // Add RMI object nodeSuccessor from nodeSuccessorURL
        Node nodeSuccessor = (Node) registry.lookup(nodeSuccessorURL);
//        System.out.println("calling rangeIncEnd with params: "+node.getNodeId()+", "+nodeSuccessor.getNodeId()+","+key);
        while (!isInRangeIncEnd(node.getNodeId(),nodeSuccessor.getNodeId(),key)){
            if (traceFlag) writeToLog("In FINDPREDECESSOR not found -----"+key+" in range("+node.getNodeId()+","+nodeSuccessor.getNodeId()+"]");
            //Add RMI object node from nodeURL (update below)
            nodeURL = node.closestPrecedingFinger(key, traceFlag);
            node = (Node) registry.lookup(nodeURL);

            nodeSuccessorURL = node.successor();
            nodeSuccessor = (Node) registry.lookup(nodeSuccessorURL);
//            writeToLog("End loop --- now node is "+nodeURL+"--- and node successor is  "+nodeSuccessorURL);
        }
        if (traceFlag) writeToLog("PREDECESSOR for node key --- " + key+" --- is "+nodeURL);

        return nodeURL;
    }


    public String  closestPrecedingFinger (int key, boolean traceFlag) throws RemoteException, MalformedURLException, NotBoundException {
        if (traceFlag) writeToLog("Finding  closestPrecedingFinger in node ---"+this.nodeUrl+" for key --- "+key);
        for (int i =m;i>0;i--){
            //Add RMI object fingerNodei from finger.get(i).node
            Node fingerNodei = (Node) registry.lookup(finger.get(i).node);
            if(isInRange(this.id,key,fingerNodei.getNodeId())){
                if (traceFlag) writeToLog("ClosestPrecedingFinger in node ---" + this.nodeUrl + " for key --- " + key +"is ---"+finger.get(i).node);
                return finger.get(i).node;
            }
        }
        if (traceFlag) writeToLog("ClosestPrecedingFinger in node ---" + this.nodeUrl + " for key --- " + key +"is ---"+this.nodeUrl);
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



    public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException, UnknownHostException {
        System.setSecurityManager (new SecurityManager ());
        Registry registry = null;
        String url;
        int port;
        if ( args.length ==1 ){
            port=Integer.parseInt(args[0]);
            url="localhost";
            registry = LocateRegistry.getRegistry(port);
        }
        else if ( args.length ==2 ) {
            //if you send 2 arguments, uses them for registry to check node0 and for creating new node.
            url=args[0];
            port=Integer.parseInt(args[1]);
            registry = LocateRegistry.getRegistry(url,port);
        }

        else {
            //if you send no arguments, uses the default arguments for creating new node and for registry to check node0
            url = "localhost";
            port=1099;
            registry = LocateRegistry.getRegistry();
        }
        int nodeId;
        try{
            String node0Url = "//"+url+":"+port+"/node0";
            String bootstrapHash = Integer.toString(FNV1aHash.hash32(node0Url));
            Node node0 = (Node) registry.lookup(bootstrapHash);
            nodeId = node0.getCounter();
            String nodeUrl = "//"+url+":"+port+"/node"+nodeId;
            int hashUrl = FNV1aHash.hash32(nodeUrl);
            System.out.println("This is not node-0 ---- "+ Integer.toString(hashUrl));
            if (node0.joinLock(nodeUrl)) {
                NodeImpl newNode = new NodeImpl(Integer.toString(hashUrl), hashUrl, nodeUrl,bootstrapHash,registry);
                System.setProperty("java.rmi.server.hostname",  InetAddress.getLocalHost().getHostName()  );
                Node nodeStub = (Node) UnicastRemoteObject.exportObject(newNode, 0);
                registry.bind(Integer.toString(hashUrl), nodeStub);
                boolean res = newNode.join(bootstrapHash);
                if (res) {
//                    node0.joinFinished(nodeUrl);
                    System.out.println("Join was successful! --- Releasing lock");

                    node0.joinLockRelease(nodeUrl);
                }

            }
        } catch (NotBoundException e){
            //node0 not created
            String nodeUrl = "//"+url+":"+port+"/node"+0;
            int hashUrl =FNV1aHash.hash32(nodeUrl);
            System.out.println("This is node-0 ---"+Integer.toString(hashUrl));
            NodeImpl newNode = new NodeImpl(Integer.toString(hashUrl),hashUrl,nodeUrl,Integer.toString(hashUrl), registry);
            System.setProperty("java.rmi.server.hostname",  InetAddress.getLocalHost().getHostName()  );
            Node nodeStub = (Node) UnicastRemoteObject.exportObject(newNode, 0);
            registry.bind(Integer.toString(hashUrl),nodeStub);
            boolean res = newNode.join(null);
            if (res) {
//                newNode.joinFinished(nodeUrl);
                System.out.println("Join was successful!");
//                newNode.joinLockRelease(nodeUrl);
            }
        }
    }


}
