var express = require('express')
  , router = express.Router()
  , analyze = require('../lib/analyze_picture');

router.post('/', function(req, res) {
	analyze(req.body, function(result) {
		res.send(result[0]["scores"]);
	});
});

module.exports = router;