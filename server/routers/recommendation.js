var express = require('express')
  , router = express.Router()
  , get_mood = require('../lib/get_mood')
  , restraunts = require('../recommendations/restraunts')
  , songs = require('../recommendations/songs')
  , sports = require('../recommendations/sports');

router.post('/restraunt', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(restraunts[mood]);
});

router.post('/sports', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(sports[mood]);
});

router.post('/music', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(songs[mood]);
});


module.exports = router;