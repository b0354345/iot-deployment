#!/bin/bash
cd $HOME
token=`cat token-worker-join.txt`
manager=`cat hostip.txt`
echo $token
echo $manager
docker swarm join --token ${token} ${manager}:2377 