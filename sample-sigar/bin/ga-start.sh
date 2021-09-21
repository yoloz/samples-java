#!/usr/bin/env bash
#如果环境中未配置JAVA_HOME,可在下面单引号里添加jdk的路径
JAVA_HOME=${JAVA_HOME:=''}
if [ "x$JAVA_HOME" = "x" ]; then
   echo "JAVA_HOME is not configured, please configure and then execute again!"
   exit 1
else
   JAVA=${JAVA_HOME}/bin/java
fi
java_version=`${JAVA} -version 2>&1 |awk 'NR==1{gsub(/"/,""); print $3}'`
java_version=${java_version:0:3}
if [ ${java_version//[_|.]/} -lt 18 ]; then
   echo "java version need 1.8+"
   exit 1;
fi
base_dir=$(dirname $0)/..
for f in ${base_dir}/lib/*.jar; do
    test -f ${f} && CLASSPATH=${CLASSPATH}:${f}
done
if [ -f ${base_dir}/config/log4j.properties ]; then
   GA_LOG4J_OPTS="-Dlog4j.configuration=file:$base_dir/config/log4j.properties"
else
   echo "can't find file log4j.properties..."
   exit 1
fi
test -d ${base_dir}/logs || mkdir -p ${base_dir}/logs
test -d ${base_dir}/data || mkdir -p ${base_dir}/data
GA_BASE_DIR="-Dga.base.dir=$base_dir"
#LIBRARY_PATH="-Djava.library.path=${base_dir}/lib/sigar"
if [ "x$1" = "x-daemon" ]; then
  nohup ${JAVA} ${GA_BASE_DIR} ${GA_LOG4J_OPTS} -cp ${CLASSPATH}  com.yoloz.sample.sigar.Main  > "${base_dir}/logs/gather.out" 2>&1 < /dev/null &
else
  exec ${JAVA} ${GA_BASE_DIR} ${GA_LOG4J_OPTS} -cp ${CLASSPATH} com.yoloz.sample.sigar.Main
fi


