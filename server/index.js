var express = require('express');
var app = express();
var bodyParser = require('body-parser');

app.use(express.static(__dirname + '/public'));

app.use(bodyParser.json());

app.use('/analyze', require('./routers/analyze'));

app.use('/recommendation', require('./routers/recommendation'));


app.listen(3000, function() {
	console.log('listening on port: 3000');
});
