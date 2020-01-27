CSCI-652 Distributed Systems
For more info, please visit my [teaching](https://www.cs.rit.edu/~ph/teaching) page.

For RIT students, you can find more information on myCourses.

Run ```mvn package``` inside the folder of each module to compile and generate a .jar file.


If you need to sync your forked repo with upstream repo, check [here](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/syncing-a-fork)


# Docker container
Build image with
```
docker build -t csci652:latest .
```

Verify the new image with 
```
docker images
```

Start a container
```
docker run -it csci652 /bin/bash
```


# Docker-compose
Build and start docker containers
```
docker-compose up
```
use `--build` to rebuild docker image

Attach to the running docker containers
```
docker exec -it <CONTAINER-NAME> bash
```
where `<CONTAINER-NAME>` should be replaced with your targeted container name.