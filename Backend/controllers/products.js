'use strict'

const Product = require('../models/product')

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
