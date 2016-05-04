'use strict'

var fs = require('fs')
const express = require('express')
var morgan = require('morgan')
const config = require('./config')
const app = express()

var accessLogStream = fs.createWriteStream(__dirname + '/access.log', {flags: 'a'})
app.use(morgan('combined', {stream: accessLogStream}))

app.use(require('./controllers'))

let port = config.web.port
let server = app.listen(port, onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
