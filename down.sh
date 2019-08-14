#! /bin/sh

export DOCKER_HOST_IP=127.0.0.1
docker-compose -f zk-single-kafka-single.yml down 
