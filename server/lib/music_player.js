var player = new (require('stupid-player'));


var callback = function(buffer) {
}

player.on(player.EVENT_PLAY, callback);
player.on(player.EVENT_STOP, callback);
player.on(player.EVENT_ERROR, callback);


module.exports.play = function(path) {	 
	return player.play(path);
}

module.exports.pause = function() {
	player.pause();
}

module.exports.resume = function() {
	player.resume();
}

module.exports.stop = function() {
	player.stop();
}
