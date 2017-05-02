#!/bin/sh
cd `dirname $0`
rm -f pid
if [ ! -f "pid" ]
then
    
    python looping.py  >>/home/pi/yuhao/looping.log 2>>/home/pi/yuhao/looping.log &
    echo $! > pid
fi




