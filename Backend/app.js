'use strict'

var fs = require('fs')
var express = require('express')
var bodyParser = require('body-parser')
var morgan = require('morgan')
var path = require('path')
var helmet = require('helmet')

var config = require('./config')
var app = express()

// prevents possible security leaks
app.use(helmet.hidePoweredBy())
app.use(helmet.noSniff())
app.use(helmet.xssFilter())
app.use(helmet.noCache())
app.use(helmet.frameguard())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended: false}))

let accessLogStream = fs.createWriteStream(path.join(__dirname, '/access.log'), {flags: 'a'})
app.use(morgan('combined', {stream: accessLogStream}))

app.use(require('./controllers'))

let port = config.web.port
let server = app.listen(port, onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
