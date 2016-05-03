'use strict'

const express = require('express')
const mongoose = require('mongoose')
const router = express.Router()

mongoose.connect('mongodb://localhost/test')

router.use(require('./lists'))
router.use(require('./products'))

router.get('/', function (req, res) {
  res.send('Server running')
})

// search
router.get('/search/:query', function (req, res) {
  // var market = req.params.market
  let query = req.params.query
  res.json(query)
})

module.exports = router
