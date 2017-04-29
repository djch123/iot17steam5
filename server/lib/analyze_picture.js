var request = require('request');

var parameter = {
	url: "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize",
	method: "POST",
	headers: {
		'Content-Type':'application/octet-stream'
	},
	encoding: null
};

parameter.headers["Ocp-Apim-Subscription-Key"]
	= require('../keys/microsoft').emotion;



module.exports = function(filename)
{
	var fs = require("fs");

	fs.readFile(filename, function(err, data) {
	  if (err) throw err;
	  // Encode to base64
	  var encodedImage = new Buffer(data, 'binary');

	  parameter["body"] = encodedImage;


	  request(parameter, function(err, response, body) {
			if (err) {
		       console.log('Error sending message: ', error)
		    } else {
		      console.log('Response: ', JSON.parse(response.body));
		    }
	  });
	});
}