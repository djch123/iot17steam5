module.exports = function(scores) {
	var sum = 0;
	for (mood in scores) {
		sum += scores[mood];
	}
	var ratio = scores["happiness"] / sum;
	return Math.ceil(ratio * 254);
}