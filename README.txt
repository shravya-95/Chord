Chord
Authors:
Divya Nairy (5589575) - shrin020@umn.edu
Krishna Shravya Gade (5592616) - gade0030@umn.edu

Instructions to run:

1. Start rmiregistry
	rmiregistry &
2. Compile the NodeImpl.java file
	javac NodeImpl.java

3. Run node 0 and wait for it to join (this will be notified by the following message being printed in console: Join was successful)
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl

4. Run the other 7 nodes
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl

5. Compile and run the dictionary loader
	javac DictionaryLoader.java
	java -Djava.security.policy=mySecurityPolicyfile DictionaryLoader <someChordNodeURL> <dictionaryFile>
	eg: java -Djava.security.policy=mySecurityPolicyfile DictionaryLoader localhost:1099/node0 sample-dictionary-file.txt

6. Compile and run client:
	javac Client.java
	java -Djava.security.policy=mySecurityPolicyfile Client <someChordNodeURL>
	eg: java -Djava.security.policy=mySecurityPolicyfile Client localhost:1099/node0
	
	The Client program is interactive. Press 1 for lookup, 2 to print the structure of the DHT and 3 to exit. Incase you press 1, you will be prompted to enter the work you want to look for. This will return the meaning of the looked up word. 
	eg:     Enter 1 to lookup, 2 for printing DHT structure, 2 to exit
		Enter your choice: 1
		Enter your word:
		answer
		Result: a reply, a solution

-----------------

Names of the logfiles will be <url>_logfile. eg: node0_logfile




