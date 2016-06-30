'use strict'

const Market = require('../models/market')

exports.getMarkets = function (req, res, next) {
  Market
    .find()
    .populate('products.product')
    .exec(function (err, docs) {
      if (err) return next(err)
      res.json(docs)
    })
}

exports.uploadMarketData = function (req, res, next) {
  let market = new Market(req.body.market)
  market.save(function (err, doc) {
    if (err) return next(err)
    res.json(doc)
  })
}
