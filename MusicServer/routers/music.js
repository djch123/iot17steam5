var express = require('express')
  , router = express.Router()
  , player = require('../lib/music_player');



router.get('/', function(req, res) {
	console.log(req.song);
	player.play(__dirname + '/../music/Cloud/' + req.query.song);
});

router.get('/pause', function() {
	player.pause();
});

router.get('/resume', function() {
	player.resume();
});

router.get('/stop', function() {
	player.stop();
});

module.exports = router;