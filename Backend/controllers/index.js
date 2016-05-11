'use strict'

const express = require('express')
const mongoose = require('mongoose')

const config = require('../config')
const router = express.Router()

const importer = require('./importer')
const search = require('./search')
const lists = require('./lists')

mongoose.connect(config.database.url)
mongoose.connection.on('error', function () {
  console.log('Error connecting to MongoDB')
})

router.get('/', function (req, res) {
  res.send('Server running')
})

router.get('/lists', lists.findLists)
router.post('/lists', lists.uploadList)
router.put('/lists/:id', lists.updateList)
router.delete('/lists/:id', lists.deleteList)

router.get('/search/:query', search.findProducts)

router.post('/products/import', importer.uploadProducts)

router.use(function errorHandler (err, req, res, next) {
  if (req.app.get('env') !== 'development') {
    delete err.stack
  }

  res.status(err.statusCode || 400).json(err)
})

module.exports = router
