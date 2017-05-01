var express = require('require')
  , router = express.Router()
  , get_brightness = require('../lib/get_brightness')

router.post('/', function(req, res) {
	var brightness = get_brightness(req.body);
	res.send(brightness.toString());
});