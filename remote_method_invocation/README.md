
### Start RMI server
```bash
java -cp target/remote_method_invocation-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy  edu.rit.cs.EmployeeListServer
```

### Start RMI client
```bash
java -cp target/remote_method_invocation-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy  edu.rit.cs.EmployeeListClient
```


More information, please see https://docs.oracle.com/javase/tutorial/rmi/TOC.html