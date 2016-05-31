'use strict'

const fs = require('fs')
const express = require('express')
const bodyParser = require('body-parser')
const morgan = require('morgan')
const path = require('path')
const helmet = require('helmet')
const config = require('./config')

const app = express()

// prevents possible security leaks
app.use(helmet())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended: false}))

let accessLogStream = fs.createWriteStream(path.join(__dirname, '/access.log'), {flags: 'a'})
app.use(morgan('combined', {stream: accessLogStream}))

app.use(require('./controllers'))

let port = config.PORT
let server = app.listen(port, onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
