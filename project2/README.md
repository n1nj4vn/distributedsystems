# **Project 2**
The following are publisher, subscriber, event manager programs for CSCI-652-01 Distributed Systems Project 2. 

# **Docker-compose**
Build docker containers:
```
docker-compose -f docker-compose-project2.yml build
```

Start docker containers:
```
docker-compose -f docker-compose-project2.yml up
```

Attach to the running docker containers
```
docker exec -it <CONTAINER-NAME> bash
```
where `<CONTAINER-NAME>` should be replaced with your targeted container name.

# **Diagrams**
For an overview of the system please review the diagrams under `project2/diagrams`.

General Overview:
![General Overview](diagrams/general_overview.png)

Detailed Overview:
![Detailed Overview](diagrams/detailed_overview.png)

Class/Entity Overview:
![Class/Entity Overview](diagrams/class_entity_overview.png)

Example Scenarios:
![Example Scenarios](diagrams/example_scenarios.png)