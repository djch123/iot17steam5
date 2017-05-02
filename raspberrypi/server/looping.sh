#!/bin/sh
cd `dirname $0`
python /home/pi/iot17steam5/raspberrypi/server/looping.py  >>/home/pi/yuhao/looping.log 2>>/home/pi/yuhao/looping.log &
