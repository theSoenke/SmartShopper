'use strict'

const express = require('express')

const router = express.Router()
const Product = require('../models/product')

router.get('/search/:query', function (req, res) {
  let query = req.params.query
  // let limit = req.query.limit
  // let market = req.query.market

  // TODO limit results, filter by market

  Product.find({'name': query}, function (err, docs) {
    if (err) {
      throw err
    }
    res.json(docs)
  })
})

module.exports = router
