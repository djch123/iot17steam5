var get_mood = require('../lib/get_mood');
var scores = {
      "anger": 0,
      "contempt": 13,
      "disgust": 1.02152783e-11,
      "fear": 100,
      "happiness": 0.9999999,
      "neutral": 1.31694478e-7,
      "sadness": 6.04054263e-12,
      "surprise": 3.92249462e-11
}
console.log(get_mood(scores));