var mood_vector = {
	"anger" : ["anger","contempt","disgust"],
	"happiness" : ["happiness","surprise"],
	"fear" : ["fear","neutral","sadness"]
};


// anger, contempt, disgust: need composure, cold
// happiness, surprise: happy, funny
// fear, sadness, neutral: need comfort, warm

module.exports = function(scores) {
	var max = 0;
	var mood_now = "";
	for (var mood in mood_vector) {
		var sum = 0;
		for (var index in mood_vector[mood]) {
			var emotion = mood_vector[mood][index];
			sum += scores[emotion];
		}
		var avg = sum / mood_vector[mood].length;
		if (avg > max) {
			max = avg;
			mood_now = mood;
		}
	}
	return mood_now;
};