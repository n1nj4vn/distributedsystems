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