#!/bin/bash
args=("$@")
cd $HOME
echo ${args[@]} > nodes.txt
IN=$(hostname -I)
set -- "${IN}"
IFS=" "; declare -a tokens=($*)
echo ${tokens[0]} > hostip.txt
docker network rm spark-net
docker swarm init --advertise-addr ${tokens[0]} &&
docker network create --driver overlay spark-net
docker swarm join-token --quiet worker > token-worker-join.txt
docker swarm join-token --quiet manager > token-master-join.txt
nodes=`cat nodes.txt`
set -- "${nodes}"
IFS=" "; declare -a nodes=($*)
for ((i=1; i<${#nodes[@]}; i++)); do
        echo ${nodes[i]}
        scp token-worker-join.txt hostip.txt ${nodes[i]}:
        scp token-master-join.txt hostip.txt ${nodes[i]}:
done