'use strict'

const express = require('express')
const mongoose = require('mongoose')
const config = require('../config')
const router = express.Router()

mongoose.connect(config.database.url)

let importer = require('./importer')
let search = require('./search')
let lists = require('./lists')

importer.use(errorHandler)
search.use(errorHandler)
lists.use(errorHandler)

router.use(errorHandler)
router.use(lists)
router.use(search)
router.use(importer)

router.get('/', function (req, res) {
  res.send('Server running')
})

function errorHandler (err, req, res, next) {
  if (req.app.get('env') !== 'development') {
    delete err.stack
  }

  res.status(err.statusCode || 400).json(err)
}

module.exports = router
