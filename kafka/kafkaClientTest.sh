#!/usr/bin/env bash

CLASSPATH=$(echo ./lib/*.jar | tr ' ' ':')
#echo 'classpath='${CLASSPATH}
java -cp ${CLASSPATH} kafkaClientTest $@
