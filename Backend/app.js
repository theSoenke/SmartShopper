'use strict'

const express = require('express')
const bodyParser = require('body-parser')
const helmet = require('helmet')
const config = require('./config')

const app = express()

// prevents possible security leaks
app.use(helmet())

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended: false}))

app.use(require('./controllers'))

let port = config.PORT
let server = app.listen(port, 'localhost', onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
