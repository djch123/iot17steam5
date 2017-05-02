#!/bin/sh
sudo modprobe bcm2835-v4l2

cd `dirname $0`
rm -f pid

if [ ! -d ../log ]
then
        mkdir ../log
fi

if [ ! -f "pid" ]
then
    cd ..
    python app.py >>log/server.log 2>>log/server.log &
    echo $! > deploy/pid
fi
