#Remote Procedure Call (RPC)

To compile and build a .jar
```
mvn package
```

Start RPC server
```
java -cp target/remote_procedure_call-1.0-SNAPSHOT.jar edu.rit.cs.JsonRPCServer
```

Start RPC client
```
java -cp target/remote_procedure_call-1.0-SNAPSHOT.jar edu.rit.cs.JsonRPCClient localhost:9091
```

Server Output
```
The server is running.
{"method":"getDate","id":0,"jsonrpc":"2.0"}
```

Client outputs the date on the server. For example,
```
Sep 19, 2019  
```