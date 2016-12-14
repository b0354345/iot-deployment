#!/bin/bash
args=("$@")
WORKERNODE_PUBLIC_DNS="${args[0]}"
MASTERNODE_PUBLIC_DNS="${args[1]}"
echo "export SPARK_PUBLIC_DNS=${WORKERNODE_PUBLIC_DNS}" | tee -a $SPARK_HOME/conf/spark-env.sh
start-slave.sh spark://${MASTERNODE_PUBLIC_DNS}:7077
while true; do sleep 1000; done