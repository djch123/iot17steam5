var express = require('express'),
router = express.Router();

router.post('/restraunt', function(req, res) {
	if (!req.body) return res.sendStatus(400);
	res.send(req.body);
});


module.exports = router;