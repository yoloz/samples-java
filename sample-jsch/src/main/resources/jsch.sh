#!/usr/bin/env bash
dir=$(cd `dirname $0`/..;pwd)
JAVA=${dir}'/jdk'/bin/java
if [ ! -x ${JAVA:=''} ]; then
    echo "java command error..."
    exit 1
fi
CLASSPATH=${CLASSPATH}:${dir}'/functions/jsch.jar'
${JAVA} "-Dcii.root.dir=$dir" -cp ${CLASSPATH} com.unimas.jsch.Main $@
