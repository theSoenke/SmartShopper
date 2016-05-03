'use strict'

const express = require('express')
const app = express()

app.use(require('./controllers'))

let port = process.env.PORT || 3000
let server = app.listen(port, onListening)

function onListening () {
  let addr = server.address()
  console.log('Listening on port %s', addr.port)
}
