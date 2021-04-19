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
        Node node = (Node) Naming.lookup ("//" + args[0]);

        System.out.println("Enter 1 to lookup, 2 to exit");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your choice: ");
        int choice = in.nextInt();
        while(choice!=2){
            System.out.println("Enter your word: ");
            String word = in.next();//Assuming no space in word
            int wordHash = FNV1aHash.hash32(word);//assuming this returns correct without
            String successorUrl = node.findSuccessor(wordHash,false);
            Node successor = (Node) Naming.lookup(successorUrl);
            String meaning = successor.lookup(word);
            System.out.println("Result: "+meaning);
            System.out.print("Enter your choice: ");
            choice = in.nextInt();
        }
    }
}
