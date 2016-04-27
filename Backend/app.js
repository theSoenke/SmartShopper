var express = require('express')
var app = express()

app.use(require('./controllers'))

var port = process.env.PORT || 3000
var server = app.listen(port, onListening)

function onListening () {
  var addr = server.address()
  console.log('Listening on port %s', addr.port)
}
