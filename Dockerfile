# Ubuntu 18.04 with JDK 11
# Build image with:  docker build -t csci652:latest .

FROM ubuntu:18.04
MAINTAINER Peizhao Hu, http://cs.rit.edu/~ph
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y net-tools iputils-ping maven gradle nmap wget git vim build-essential && \
    apt-get clean


RUN mkdir /csci652

COPY basic_word_count /csci652/basic_word_count
COPY distributed_hash_table /csci652/distributed_hash_table
COPY distributed_consensus /csci652/distributed_consensus
COPY pubsub /csci652/pubsub
COPY remote_procedure_call /csci652/remote_procedure_call
COPY socket_programming /csci652/socket_programming
COPY pom.xml /csci652/
COPY README.md /csci652/
COPY LICENSE /csci652/

WORKDIR /csci652

RUN cd /csci652 && mvn package