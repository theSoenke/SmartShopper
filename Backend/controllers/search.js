'use strict'

const Product = require('../models/product')
const User = require('../models/user')

exports.findProducts = function (req, res, next) {
  let query = req.params.query
  let limit = parseInt(req.query.limit, 10)
  // let market = req.query.market
  // TODO filter by market

  Product
    .find({$text: {$search: query}}, {score: {$meta: 'textScore'}})
    .sort({score: {$meta: 'textScore'}})
    .limit(limit)
    .exec(function (err, docs) {
      if (err) return next(err)
      res.json(docs)
    })
}

exports.findUser = function (req, res, next) {
  let query = req.params.query
  let limit = parseInt(req.query.limit, 10)

  User
    .find({$text: {$search: query}}, {score: {$meta: 'textScore'}})
    .sort({score: {$meta: 'textScore'}})
    .limit(limit)
    .exec(function (err, docs) {
      if (err) return next(err)
      res.json(docs)
    })
}
