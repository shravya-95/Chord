import java.util.Scanner;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.util.*;
import java.io.*;
import java.rmi.RemoteException;

public class Client{
    public static void main(String[] args) throws Exception{
        if ( args.length != 1 ) {
            throw new RuntimeException( "Syntax: java Client someChordNodeURL \n URL should be sent in the form alpha.umn.edu:1099 where 1099 is the port" );
        }
        System.setSecurityManager (new SecurityManager ());
        String url = "//"+args[0].split("/")[0]+"/";
        String nodeUrl = Integer.toString(FNV1aHash.hash32("//" + args[0]));

        Node node = (Node) Naming.lookup (url + nodeUrl);

        System.out.println("Enter 1 to lookup, 2 for printing DHT structure, 2 to exit");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your choice: ");
        int choice = in.nextInt();
        while(choice!=3){
            if(choice==1) {
                System.out.println("Enter your word: ");
                String word = in.next();//Assuming no space in word
                int wordHash = FNV1aHash.hash32(word);//assuming this returns correct without
                String successorUrl = node.findSuccessor(wordHash, true);
                Node successor = (Node) Naming.lookup(url+successorUrl);
                String meaning = successor.lookup(word);
                System.out.println("Result: " + meaning);
                System.out.print("Enter your choice: ");
                choice = in.nextInt();
            } else if(choice==2){
                System.out.println(node.printStructure());
                System.out.print("Enter your choice: ");
                choice = in.nextInt();
            }
        }
    }
}
