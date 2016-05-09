'use strict'

const express = require('express')

const router = express.Router()
const Product = require('../models/product')

router
  .post('/products/import', function (req, res, next) {
    Product.insertMany(req.body.products, function (err, docs) {
      if (err) {
        return next(err)
      }
      res.json(docs)
    })
  })

module.exports = router
