from flask import Flask, url_for, jsonify, Response, render_template, send_from_directory
import config
import json
from capture import capture
import requests
from random import randint
import os, time
import subprocess


app = Flask(__name__, static_url_path="", static_folder="./")

fi = open('config.json', 'r')
config.conf = json.loads(fi.read())
conf = config.conf
fi.close()

global cur_emotion
cur_emotion = conf['default_emotion']

global weekly_emotion
def setup():
	global weekly_emotion
	weekly_emotion = {"data":[]}
	for i in range(0, 6):
		weekly_emotion['data'].append({
				"anger": randint(0,20),
	      		"contempt": randint(0,5),
	      		"disgust": randint(0,5),
	      		"fear": randint(0,10),
	      		"happiness": randint(0,20),
	      		"neutral": randint(0,30),
	      		"sadness": randint(0,10),
	      		"surprise": randint(0,5)
			})
	#today's data
	weekly_emotion['data'].append({
				"anger": 0,
	      		"contempt": 0,
	      		"disgust": 0,
	      		"fear": 0,
	      		"happiness": 0,
	      		"neutral": 0,
	      		"sadness": 0,
	      		"surprise": 0
			})

APP_ROOT = os.path.dirname(os.path.abspath(__file__))
setup()

def capture_helper():
	stop_motion()

	image_path = conf['image_path']
	anaylze_url = "http://" + conf['anaylze_ip'] + ":" + conf['anaylze_port'] + "/analyze"
	print anaylze_url
	capture(image_path)
	image = open(image_path)
	data = image.read()
	image.close()
	res = requests.post(url=anaylze_url,
				data=data,
				headers={'Content-Type': 'application/octet-stream'})
	res.raise_for_status()
	j = res.json()
	
	global cur_emotion
	
	if len(j) > 0 and "happiness" in j:
		cur_emotion = j
	

	

@app.route('/takeaphoto')
def takeaphoto():
	try:
		capture_helper()
		global weekly_emotion, cur_emotion
		max_emotion_type = max(cur_emotion, key=lambda k: cur_emotion[k])
		today_emotion = weekly_emotion['data'][6]
		if max_emotion_type in today_emotion: today_emotion[max_emotion_type] += 1
		
		return "OK", 200
	except Exception as e:
		print str(e)
		return str(e), 500

@app.route('/emotion')
def getCurEmotion():
	global cur_emotion
	return json.dumps(cur_emotion), 200


@app.route('/emotion/week')
def getWeeklyEmotion():
	global weekly_emotion
	return json.dumps(weekly_emotion), 200

@app.route("/stream")
def stream():
	start_motion()
	return render_template('stream.html', ip = conf['pi_ip']) 

def start_motion():
	
	p = subprocess.Popen(['sudo', 'motion', '-m'])
	p.wait()
	while True:
		r = requests.get("http://" + conf['pi_ip'] + ":8080")
		if r.status_code == 200: break
		else: time.sleep(0.5)


def stop_motion():
	p = subprocess.Popen(['sudo', 'pkill', 'motion'])
	p.wait()



@app.route("/captureinstream")
def captureinstream():
	# try:
	# 	r = requests.get("http://" + conf['pi_ip'] + ":8080/0/action/snapshot")
	# 	r.raise_for_status()
	# except Exception as e:
	# 	print str(e)
	# 	return str(e), 500

	# try:

	# 	anaylze_url = "http://" + conf['anaylze_ip'] + ":" + conf['anaylze_port'] + "/analyze"
	# 	# image = open(conf["stream_snap_path"]) 
	# 	cmd = "cp " + conf['stream_snap_path'] + " " + os.path.join(APP_ROOT, "image.jpg")
	# 	print cmd
	# 	# os.system(cmd)
	# 	p = subprocess.Popen(cmd.split(), stdout = subprocess.PIPE,stderr = subprocess.STDOUT)
	# 	p.wait()


	# 	image = open(conf["image_path"])
		
	# 	data = image.read()
	# 	image.close()
	# 	res = requests.post(url=anaylze_url,
	# 			data=data,
	# 			headers={'Content-Type': 'application/octet-stream'})
		

	# 	res.raise_for_status()


	# 	j = res.json()
	# 	return render_template('snap.html', ip=conf['pi_ip'], port=str(conf['pi_port']), data=j)
	# except requests.exceptions.HTTPError as e:
	# 	print str(e)
	# 	return render_template('snap.html', ip=conf['pi_ip'], port=str(conf['pi_port']), error="Can't find a face:)")
	# except Exception as e:
	# 	print str(e)
	# 	return str(e), 500


	
	try:
		stop_motion()

		time.sleep(10)
		capture_helper()
		return render_template('snap.html', ip=conf['pi_ip'], port=str(conf['pi_port']), data=json.dumps(cur_emotion))
	except requests.exceptions.HTTPError as e:
		print str(e)
		return render_template('snap.html', ip=conf['pi_ip'], port=str(conf['pi_port']), error="Can't find a face:)")
	except Exception as e:
		print str(e)
		return str(e), 500
		


	

@app.route("/lastsnp")
def lastsnp():	
	return send_from_directory(os.path.dirname(__file__), "image.jpg")
	


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8888, debug=True, threaded=True)


