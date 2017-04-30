var express = require('express');
var app = express();

app.use('/music', require('./routers/music'));

app.listen(8080, function() {
	console.log('listening on port: 3000');
});
