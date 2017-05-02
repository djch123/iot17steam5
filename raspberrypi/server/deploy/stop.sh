#!/bin/sh
cd `dirname $0`

if [ -f "pid" ]
then
    kill $(tr -d '\r\n' < pid)
    rm pid
fi


# kill the childprocess created by Tika Lib
kill -9 $(lsof -nt -i4TCP:8888)
