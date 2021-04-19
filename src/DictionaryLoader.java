
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
        Node node = (Node) Naming.lookup("//" + args[0]);

        File file = new File(args[1]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] wordMeaning = line.split(" ");
            int wordHash = FNV1aHash.hash32(wordMeaning[0]);
            String wordNodeUrl = node.findSuccessor(wordHash, false); //check
            Node wordNode = (Node) Naming.lookup(wordNodeUrl);
            wordNode.insert(wordMeaning[0], wordMeaning[1]); //needs to overwrite if same key
        }
    }
}

