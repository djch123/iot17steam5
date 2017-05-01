var express = require('express')
  , router = express.Router()
  , player = require('../lib/music_player');



router.get('/', function(req, res) {
	console.log(req.query.song);
	player.play(__dirname + '/../music/server/' + req.query.song);
	res.status(200).send("music played!");
});

router.get('/pause', function(req, res) {
	player.pause();
	res.status(200).send("music paused!");
});

router.get('/resume', function(req, res) {
	player.resume();
	res.status(200).send("music resumed!");
});

router.get('/stop', function(req, res) {
	player.stop();
	res.status(200).send("music stopped!");
});

module.exports = router;