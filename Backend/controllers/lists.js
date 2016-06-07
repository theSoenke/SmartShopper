'use strict'

const basicAuth = require('basic-auth')
const List = require('../models/list')

// get all lists from user
exports.getLists = function (req, res, next) {
  let username = basicAuth(req).name
  let query = {owner: username}

  List
    .find(query)
    .populate('participants')
    .exec(function (err, docs) {
      if (err) {
        return next(err)
      }

      res.json(docs)
    })
}

// Create a new list
exports.uploadList = function (req, res, next) {
  let username = basicAuth(req).name
  let list = req.body
  list.owner = username

  List.create(list, function (err, docs) {
    if (err) {
      return next(err)
    }

    List.findById(docs._id)
      .populate('participants', 'username')
      .exec(function (err, doc) {
        if (err) return next(err)
        res.json(doc)
      })
  })
}

// Update existing list
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

// delete a list
exports.deleteList = function (req, res, next) {
  List.findByIdAndRemove(req.params.id, function (err) {
    if (err) {
      return next(err)
    }
    res.json({message: 'deleted list', _id: req.params.id})
  })
}
