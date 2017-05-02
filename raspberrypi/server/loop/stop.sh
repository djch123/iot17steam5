#!/bin/sh
cd `dirname $0`

if [ -f "pid" ]
then
    kill $(tr -d '\r\n' < pid)
    rm pid
fi
