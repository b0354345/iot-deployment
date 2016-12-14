#!/bin/bash
cd $HOME
args=("$@")
docker service rm master
url=${args[0]}
main=${args[1]}
broker=${args[2]}
topic=${args[3]}
batch=${args[4]}
echo $url
echo $main
echo $broker
echo $topic
echo $batch
nodes=`cat nodes.txt`
docker service create --name master --network spark-net --replicas 1 -p 7077:7077 -p 8080:8080 -p 4040:4040 --env MAIN=$main --env URL=$url --env BROKER=$broker --env TOPIC=$topic --env BATCH=$batch cebren/spark-master-0.3 $nodes