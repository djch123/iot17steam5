var express = require('express')
  , router = express.Router()
  , get_mood = require('../lib/get_mood')
  , restraunts = require('../recommendations/restraunts')
  , songs = require('../recommendations/songs');

router.post('/restraunt', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(restraunts[mood]);
});

router.post('/music', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	var scores = req.body;
	var mood = get_mood(scores);
	res.send(songs[mood]);
});


module.exports = router;