
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class DictionaryLoader {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new RuntimeException("Syntax: java DictionaryLoader someChordNodeURL dictionaryFile \n URL should be sent in the form alpha.umn.edu:1099 where 1099 is the port");
        }
        System.setSecurityManager(new SecurityManager());
        String nodeUrl = Integer.toString(FNV1aHash.hash32("//" + args[0]));
        String url = "//"+args[0].split("/")[0]+"/";
        System.out.println(url);
        Node node = (Node) Naming.lookup(url+nodeUrl);


        File file = new File(args[1]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] wordMeaning = line.split(":");
            int wordHash = FNV1aHash.hash32(wordMeaning[0].trim());
            String wordNodeUrl = node.findSuccessor(wordHash, true); //check
            Node wordNode = (Node) Naming.lookup(url+wordNodeUrl);
            wordNode.insert(wordMeaning[0].trim(), wordMeaning[1].trim()); //needs to overwrite if same key
        }
    }
}

