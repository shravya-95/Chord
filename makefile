node:
	javac NodeImpl.java
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
	java -Djava.security.policy=mySecurityPolicyfile NodeImpl
dictionary:
	javac DictionaryLoader.java
	java -Djava.security.policy=mySecurityPolicyfile DictionaryLoader localhost:1099/node0 sample-dictionary-file.txt
client:
	javac Client.java
	java -Djava.security.policy=mySecurityPolicyfile Client localhost:1099/node0