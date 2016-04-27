var express = require('express')
var router = express.Router()
var mongoose = require('mongoose')
mongoose.connect('mongodb://localhost/test')
var Product = require('../models/product')

router.get('/', function (req, res, next) {
  res.json('test')
  createProduct()
})

function createProduct () {
  var kaese = new Product({
    name: 'gouda'
  })

  kaese.save(function (err) {
    if (err) {
      throw err
    }

    console.log('product saved')
  })
}

module.exports = router
