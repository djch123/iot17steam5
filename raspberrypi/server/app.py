from flask import Flask, url_for, jsonify
import config
import json
from capture import capture
import requests

fi = open('conf.json', 'r')
config.conf = json.loads(fi.read())
fi.close()
global cur_emotion
cur_emotion = config['default_emotion']

@app.route('/capture')
def capture():
	image_path = config['image_path']
	anaylze_url = "http://" + config['anaylze_ip'] + ":3000/anaylze"
	try:
		capture(image_path)
		image = open(image_path)
		data = image.read()
		image.close()
		res = requests.post(url=anaylze_url,
					data=data}
					# headers={'Content-Type': 'application/octet-stream'})
		res.raise_for_status()
		j = res.json()
		print "res" + str(j)
		if len(j) > 0:
			cur_emotion = j


	except Exception e:
		print str(e)
		return str(e), 500

@app.route('/emotion')
def getCurEmotion():
	return cur_emotion



if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8888, debug=True, threaded=True)