'use strict'

const express = require('express')
const bodyParser = require('body-parser')

const router = express.Router()
const jsonParser = bodyParser.json()
const Product = require('../models/product')

router.get('/products/:market', function (req, res) {
  let market = req.params.market
  res.json(market)
})

router
  .post('/products/import', jsonParser, function (req, res) {
    let products = req.body.products

    if (!products) {
      return res.sendStatus(400)
    }

    importProducts(products)
    res.json(products)
  })

function importProducts (products) {
  products.forEach(function (obj) {
    // TODO check for duplicates and empty objects
    let product = new Product({
      name: obj.name
    })
    createProduct(product)
  })
}

function createProduct (product) {
  product.save(function (err) {
    if (err) {
      throw err
    }
  })
}

module.exports = router
