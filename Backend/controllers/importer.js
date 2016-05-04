'use strict'

const express = require('express')
const bodyParser = require('body-parser')

const router = express.Router()
const jsonParser = bodyParser.json()
const Product = require('../models/product')

// router.use(errorHandler2)

router
  .post('/products/import', jsonParser, function (req, res, next) {
    let products = req.body.products

    if (!products) {
      let error = new Error('products empty')
      return next(error)
    }

    importProducts(res, next, products)
  // res.json(products)
  })

function importProducts (res, next, products) {
  products.forEach(function (obj) {
    let product = new Product({
      name: obj.name
    })

    product.save(function (err) {
      if (err) {
        if (err.code === 11000) { // check for duplicates
          console.log('duplicate: ' + product.name)
        } else {
          return next(err)
        }
      }
    })
  })
}

module.exports = router
