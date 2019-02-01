#!/bin/bash
trap 'kill -TERM $PID' TERM INT
echo Options: $GXB_OPTS
java $GXB_OPTS -jar /opt/GXB/GXB.jar /opt/GXB/template.conf &
PID=$!
wait $PID
trap - TERM INT
wait $PID
EXIT_STATUS=$?
>>>>>>> d6fc9c1852d38efd766c0e4c855b24458edcc1a2
