#!/bin/bash
args=("$@")
MASTERNODE_PUBLIC_DNS="${args[0]}"
echo "export SPARK_PUBLIC_DNS=${MASTERNODE_PUBLIC_DNS}" | tee -a $SPARK_HOME/conf/spark-env.sh
for ((i=0; i<${#args[@]}; i++)); do
        echo ${args[i]} | tee -a $SPARK_HOME/conf/slaves
done
wget $URL -P Downloads
set -- "${URL}"
IFS="/"; declare -a tokens=($*)
length="${#tokens[@]}"
JAR="${tokens[${length}-1]}"
echo $JAR
start-master.sh
spark-submit --class ${MAIN} --master spark://${HOSTNAME}:7077 --deploy-mode client --name mqqt-spark Downloads/${JAR} ${BROKER} ${TOPIC} ${BATCH}