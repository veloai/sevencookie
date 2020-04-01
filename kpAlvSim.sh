#! /bin/sh
SERVICE_NAME=AGNG_KEEPALIVE_SIMULATOR
PATH_TO_JAR=./agngVhcl-kpAlvSim-2.0.2.jar

CHECK_OS="`uname -s`"
case "$CHECK_OS" in     
Darwin*)   PID_PATH_NAME=tmp/AGNG_KEEPALIVE_SIMULATOR-pid;;
Linux*)   PID_PATH_NAME=tmp/AGNG_KEEPALIVE_SIMULATOR-pid ;;
MINGW32*) PID_PATH_NAME=c:/tmp/AGNG_KEEPALIVE_SIMULATOR-pid;;
MINGW64*) PID_PATH_NAME=c:/tmp/AGNG_KEEPALIVE_SIMULATOR-pid;; 
CYGWIN*) PID_PATH_NAME=c:/tmp/AGNG_KEEPALIVE_SIMULATOR-pid;; 
esac

case "$1" in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -jar $PATH_TO_JAR >> kpAlvSim.log &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -jar $PATH_TO_JAR >> kpAlvSim.log &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac 
