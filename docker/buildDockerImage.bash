#!/usr/bin/env bash

# You need to create a key for accessing the repo, $> ssh-keygen -q -t rsa -N '' -f csci652_id_rsa
# then put csci652_id_rsa in your ~/.ssh/ folder, and add csci652_id_rsa.pub to the github for SSH access
# the buildDockerImage.bash script will temporally copy this key into the docker image and remove it after the build

function build_new_image() {
    cp ~/.ssh/csci652_id_rsa .
    [[ -z `docker image ls | grep csci652` ]] && echo "'csci652' does not exist" || docker rmi csci652
    docker build -t csci652 .
    rm csci652_id_rsa
}



build_new_image