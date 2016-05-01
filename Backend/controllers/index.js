'use strict'

var express = require('express')

var router = express.Router()
router.use(require('./lists'))

router.get('/', function (req, res) {
  res.send('Server running6')
})

// products
router.get('/products/:market', function (req, res) {
  var market = req.params.market
  res.json(market)
})

// search
router.get('/search/:market/:query', function (req, res) {
  // var market = req.params.market
  var query = req.params.query
  res.json(query)
})

module.exports = router
