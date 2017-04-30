import config
import json

import requests


fi = open('config.json', 'r')
config.conf = json.loads(fi.read())
conf = config.conf
fi.close()

url = "http://localhost:8888/takeaphoto"

while True:
	try:
		time.sleep(int(conf["anaylze_duration"]))
		requests.get(url)
	except Exception as e:
		print str(e)
