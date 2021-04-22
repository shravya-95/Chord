public class Finger implements java.io.Serializable {
    public String node;
    public int start;

    public Finger(String nodeURL, int start){
        this.node = nodeURL;
        this.start=start;
    }


}
