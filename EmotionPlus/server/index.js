var express = require('express');
var app = express();

app.use('/music', require('./routers/music'));

app.listen(3000, function() {
	console.log('listening on port: 3000');
});