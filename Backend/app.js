var express = require('express')
var app = express()

app.use(require('./controllers'))

app.listen(3000)
