#!/usr/bin/python
# -*- coding: utf-8 -*-
import config
import json
import time

import requests


fi = open('../config.json', 'r')
config.conf = json.loads(fi.read())
conf = config.conf
fi.close()

url = "http://localhost:8888/takeaphoto"

while True:
	try:
		print "looping once..."
		time.sleep(int(conf["anaylze_duration"]))
		r=requests.get(url)
		print r
	except Exceptioas e:
		print str(e)
