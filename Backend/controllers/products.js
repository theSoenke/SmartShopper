'use strict'

const Product = require('../models/product')
const Market = require('../models/market')

exports.getProducts = function (req, res, next) {
  Product
    .find()
    .exec(function (err, docs) {
      if (err) return next(err)
      res.json(docs)
    })
}

exports.uploadProducts = function (req, res, next) {
  Product.insertMany(req.body.products, function (err, docs) {
    if (err) return next(err)
    res.json(docs)
  })
}

exports.uploadMarketProducts = function (req, res, next) {
  let market = new Market(req.body.market)
  market.save(function (err, doc) {
    if (err) return next(err)
    res.json(doc)
  })
}
