'use strict'

const List = require('../models/list')

exports.findLists = function (req, res, next) {
  List.find(function (err, docs) {
    if (err) {
      return next(err)
    }

    res.json(docs)
  })
}

exports.uploadList = function (req, res, next) {
  let list = List()
  list.name = req.body.name
  list.products = req.body.products

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
