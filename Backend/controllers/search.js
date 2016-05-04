'use strict'

const express = require('express')

const router = express.Router()
const Product = require('../models/product')

router.get('/search/:query', function (req, res, next) {
  let query = req.params.query
  let limit = parseInt(req.query.limit, 10)
  console.log(limit)
  // let market = req.query.market
  // TODO filter by market

  Product
    .find({ $text: { $search: query } },
      { score: { $meta: 'textScore' } })
    .sort({ score: { $meta: 'textScore' } })
    .limit(limit)
    .exec(function (err, results) {
      if (err) {
        return next(err)
      }
      res.json(results)
    })
})

module.exports = router
