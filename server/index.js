var express = require('express');
var app = express();
var bodyParser = require('body-parser');


app.use(bodyParser.json());

app.use('/recommendation', require('./routers/recommendation'));


app.listen(3000, function() {
	console.log('listening on port: 3000');
});
