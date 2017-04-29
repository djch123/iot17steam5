var express = require('express')
  , router = express.Router()
  , player = require('../lib/music_player')
  , list = require('../music/list');

var index = 0;


router.get('/', function() {
	player.play(__dirname + '/../music/Cloud/' + list.happy[index]);
});

router.get('/pause', function() {
	player.pause();
});

router.get('/resume', function() {
	player.resume();
});

router.get('/next', function(req, res) {
	index = index + 1 >= list.happy.length ?  0 : index + 1;
	res.send(index.toString());
	player.play(__dirname + '/../music/Cloud/' + list.happy[index]);
});

module.exports = router;