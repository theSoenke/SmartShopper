var express = require('express')
var mongoose = require('mongoose')

var router = express.Router()
mongoose.connect('mongodb://localhost/test')

var Product = require('../models/product')
// var Market = require('../models/market')
// var Shelf = require('../models/shelf')

router.get('/', function (req, res) {
  res.json('test')
})

router.get('/products/:market', function (req, res) {
  var market = req.params.market
  res.json(market)
})

router.get('/search/:market/:query'), function (req, res) {
  var market = req.params.market
  var query = req.params.query

  res.json(query)
}

function createProduct (productName) {
  var kaese = new Product({
    name: productName
  })

  kaese.save(function (err) {
    if (err) {
      throw err
    }

    console.log('product saved')
  })
}

module.exports = router
