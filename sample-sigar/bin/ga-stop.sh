#!/usr/bin/env bash
PID=$(ps ax | grep -i 'gatherdata' | grep java | grep -v grep | awk '{print $1}')
if [ -z ${PID} ]; then
   echo "no gather to stop..."
   exit 0
else
   kill -15 ${PID}
fi
#wait for 5 second
for((i=1;i<=5;i++)); do
   PID=$(ps ax | grep -i 'gatherdata' | grep java | grep -v grep | awk '{print $1}')
   if [ -n "$PID" ]; then
      echo "stopping......"
      sleep 1s
   else
      break
   fi
done
PID=$(ps ax | grep -i 'gatherdata' | grep java | grep -v grep | awk '{print $1}')
#direct kill -9
if [ -n "$PID" ]; then
   kill -9 ${PID}
fi
echo "gather has stopped..."
