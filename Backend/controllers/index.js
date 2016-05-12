'use strict'

var express = require('express')
var mongoose = require('mongoose')
var config = require('../config')
var importer = require('./importer')
var search = require('./search')
var lists = require('./lists')
var auth = require('./auth')

var router = express.Router()

mongoose.connect(config.database.url)
mongoose.connection.on('error', function () {
  console.log('Error connecting to MongoDB')
})

router.get('/', function (req, res) {
  res.send('Server running')
})

// API Routes
router.use(auth.requireAuthentication)

router.get('/lists', lists.findLists)
router.post('/lists', lists.uploadList)
router.put('/lists/:id', lists.updateList)
router.delete('/lists/:id', lists.deleteList)

router.get('/search/:query', search.findProducts)

router.post('/products/import', importer.uploadProducts)

router.use(function (err, req, res, next) {
  if (req.app.get('env') !== 'development') {
    delete err.stack
  }

  res.status(err.statusCode || 400).json({error: err.message})
})

module.exports = router
