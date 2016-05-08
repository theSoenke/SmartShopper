'use strict'

const express = require('express')
const bodyParser = require('body-parser')

const router = express.Router()
const jsonParser = bodyParser.json()
const Product = require('../models/product')

router
  .post('/products/import', jsonParser, function (req, res, next) {
    Product.insertMany(req.body.products, function (err, docs) {
      if (err) {
        return next(err)
      }
      res.json(docs)
    })
  })

module.exports = router
