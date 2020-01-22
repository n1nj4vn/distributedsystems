# Ubuntu 18.04 with JDK 8
# Build image with:  docker build -t csci652:latest .

FROM ubuntu:18.04
MAINTAINER Peizhao Hu, http://cs.rit.edu/~ph
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y net-tools iputils-ping maven gradle nmap wget git vim build-essential && \
    apt-get clean


####### This part is needed for access private repository while building a docker image ##########
RUN mkdir -p /root/.ssh/

# You need to create a key for accessing the repo, $> ssh-keygen -q -t rsa -N '' -f csci652_id_rsa
# then put csci652_id_rsa in your ~/.ssh/ folder, and add the content of csci652_id_rsa.pub to the gitlab
# repository for SSH access the buildDockerImage.bash script will temporally copy this key into the docker
# image and remove it after the build.

# Copy over private key, and set permissions
ADD csci652_id_rsa /root/.ssh/id_rsa

# Create known_hosts
RUN touch /root/.ssh/known_hosts

# Add gitlab key
RUN ssh-keyscan gitlab.com >> /root/.ssh/known_hosts
########################################################

## replace the following URL to your private gitlab repository
RUN git clone https://gitlab.com/SpiRITlab/DistributedSystems.git /csci652/.


# remove key after git
RUN rm /root/.ssh/id_rsa


RUN mkdir /csci652
WORKDIR /csci652