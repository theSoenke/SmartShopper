'use strict'

var basicAuth = require('basic-auth')
var List = require('../models/list')

exports.findLists = function (req, res, next) {
  let username = basicAuth(req).name
  let query = {owner: username}

  List
    .find(query)
    .exec(function (err, docs) {
      if (err) {
        return next(err)
      }

      res.json(docs)
    })
}

exports.uploadList = function (req, res, next) {
  let username = basicAuth(req).name

  let list = List({
    name: req.body.name,
    products: req.body.products,
    owner: username
  })

  list.save(function (err) {
    if (err) {
      return next(err)
    }
    res.json(list)
  })
}

exports.updateList = function (req, res, next) {
  let query = {'_id': req.params.id}
  let properties = {upsert: true, runValidators: true, new: true}
  List.findOneAndUpdate(query, req.body, properties, function (err, doc) {
    if (err) {
      return next(err)
    }
    return res.json(doc)
  })
}

exports.deleteList = function (req, res, next) {
  List.findByIdAndRemove(req.params.id, function (err) {
    if (err) {
      return next(err)
    }
    res.json({message: 'deleted list', _id: req.params.id})
  })
}
