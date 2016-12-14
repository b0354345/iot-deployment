#!/bin/bash
args=("$@")
cd $HOME
ssh ${args[0]} "echo ${args[@]} > nodes.txt"
ssh ${args[0]} << 'HERE'
IN=$(hostname -I)
set -- "${IN}"
IFS=" "; declare -a tokens=($*)
echo ${tokens[0]} > hostip.txt
docker network rm spark-net
docker swarm init --advertise-addr ${tokens[0]} &&
docker network create --driver overlay spark-net
docker swarm join-token --quiet worker > worker-join.txt
docker swarm join-token --quiet manager > master-join.txt
nodes=`cat nodes.txt`
set -- "${nodes}"
IFS=" "; declare -a nodes=($*)
for ((i=1; i<${#nodes[@]}; i++)); do
        echo ${nodes[i]}
        scp worker-join.txt hostip.txt ${nodes[i]}:
        scp master-join.txt hostip.txt ${nodes[i]}:
done
exit
HERE