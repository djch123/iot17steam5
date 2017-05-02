var proc = require('../lib/proc')
  , list = require('../recommendations/restraunts')["happiness"];

console.log(proc(list));