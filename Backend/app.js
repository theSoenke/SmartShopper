'use strict'

const express = require('express')
const config = require('./config')
const app = express()

app.use(require('./controllers'))

let port = config.web.port
let server = app.listen(port, onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
