#!/bin/sh
cd `dirname $0`
rm -f pid
n=`sudo ps -ef | grep looping.py`
if [ $n -eq 1 ]
then
	exit
fi

if [ ! -f "pid" ]
then
    
    python looping.py  >>/home/pi/yuhao/looping.log 2>>/home/pi/yuhao/looping.log &
    echo $! > pid
fi




