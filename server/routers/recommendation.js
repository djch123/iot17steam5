var express = require('express')
  , router = express.Router()
  , get_mood = require('../lib/get_mood')
  , restraunts = require('../recommendations/restraunts')
  , songs = require('../recommendations/songs')
  , sports = require('../recommendations/sports')
  , proc = require('../lib/proc');

router.post('/restaurant', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(proc(restraunts[mood]));
});

router.post('/sports', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(proc(sports[mood]));
});

router.post('/music', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	console.log(scores);
	var mood = get_mood(scores);
	console.log(mood);
	res.send(proc(songs[mood]));
});


module.exports = router;