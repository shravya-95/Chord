import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Node {
    public List<Finger> finger = new ArrayList<>();
    public int id;
    public String nodeUrl;
    public Node predecessor;
    public Node successor;
    public int m =31;

    public Node(String nodeURL, int id){
        this.id = id;
        this.nodeUrl=nodeURL;
        createFingerTable();
    }

    private void createFingerTable() {
        for(int i=0;i<m;i++){
            finger.add(new Finger(this, id+ (int) Math.pow(2,i) ));
        }
    }

    public boolean join (Node nodeURL) throws RemoteException{

        if (nodeURL!=null){
            initFingerTable(nodeURL);
            updateOthers();
            //move keys in (predecessor,n] from successor
        }
        else{
            for(int i = 0; i<m;i++){
                finger.get(i).node = this;
            }
            predecessor = this;
        }
        return true;
    }

    private void updateOthers() {
    }

    private void initFingerTable(Node nodeURL) throws RemoteException {
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

    public Node findSuccessor (int key, boolean traceFlag) throws RemoteException{
        node = 

        return node;
    }

}
