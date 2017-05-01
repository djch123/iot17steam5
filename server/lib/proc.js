module.exports = function(list) {

	var ret = [];

	var ipPort = "172.29.93.218:3000"

	for (var i = 0; i < list.length; i++) {
		ret[i] = {};
		for (para in list[i]) {
			if (para == "img") {
				ret[i][para] = ipPort + list[i]["img"];
			} else {
				ret[i][para] = list[i][para];
			}
		}
	}

	return ret;
}