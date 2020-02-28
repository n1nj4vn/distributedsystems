# **Project 1**
The following are client-server programs for CSCI-652-01 Distributed Systems Project 1. 

Client in one Docker container:
- Read the .csv file and print result
- Send the dataset to the server
    
Server in another Docker container:
- Read the dataset from the socket 
- Perform word count and produce a result
- Send the result back to the client

# **Docker-compose**
Build docker containers:
```
docker-compose -f docker-compose-project1.yml build
```

Start docker containers:
```
docker-compose -f docker-compose-project1.yml up
```

Attach to the running docker containers
```
docker exec -it <CONTAINER-NAME> bash
```
where `<CONTAINER-NAME>` should be replaced with your targeted container name.