'use strict'

var Product = require('../models/product')

exports.uploadProducts = function (req, res, next) {
  Product.insertMany(req.body.products, function (err, docs) {
    if (err) {
      return next(err)
    }
    res.json(docs)
  })
}
