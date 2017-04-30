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



module.exports = function(encodedImage, callback)
{
	parameter["body"] = encodedImage;
	
	request(parameter, function(err, response, body) {
		if (err) {
	       console.log('Error sending message: ', error)
	    } else {
	      console.log('Response: ', JSON.parse(response.body));
	      callback(JSON.parse(response.body));
	    }
	});
}