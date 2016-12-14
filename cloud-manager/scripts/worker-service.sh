#!/bin/bash
cd $HOME
args=("$@")
nodes=`cat nodes.txt`
set -- "${nodes}"
IFS=" "; declare -a nodes=($*)
docker service create --mode global --name worker --network spark-net -p 8081:8081 cebren/spark-worker:latest ${nodes[1]} ${nodes[0]}