'use strict'

const basicAuth = require('basic-auth')
const List = require('../models/list')
const User = require('../models/user')

// Get all lists from user
exports.getLists = function (req, res, next) {
  let credentials = basicAuth(req)

  User.findOne({name: credentials.name}, function (err, doc) {
    if (err) return err

    let query = {owner: doc._id}
    List
      .find(query)
      .populate('owner', 'name')
      .populate('participants', '-password')
      .populate('products.product', 'name')
      .exec(function (err, docs) {
        if (err) return next(err)
        res.json(docs)
      })
  })
}

// Create a new list
exports.uploadList = function (req, res, next) {
  let credentials = basicAuth(req)

  User.findOne({name: credentials.name}, function (err, doc) {
    if (err) return err

    let list = req.body
    list.owner = doc._id

    List.create(list, function (err, docs) {
      if (err) return next(err)

      // return and poulate new list
      List.findById(docs._id)
        .populate('owner', 'name')
        .populate('participants', '-password')
        .populate('products.product', 'name')
        .exec(function (err, doc) {
          if (err) return next(err)
          res.json(doc)
        })
    })
  })
}

// Update existing list
exports.updateList = function (req, res, next) {
  let properties = {upsert: true, runValidators: true, new: true}

  List
    .findByIdAndUpdate(req.params.id, req.body, properties)
    .populate('owner', 'name')
    .populate('participants', 'name fcmToken')
    .populate('products.product', 'name')
    .exec(function (err, doc) {
      if (err) return next(err)
      return res.json(doc)
    })
}

// Delete a list
exports.deleteList = function (req, res, next) {
  List.findByIdAndRemove(req.params.id, function (err) {
    if (err) return next(err)
    res.json({message: 'deleted list', _id: req.params.id})
  })
}
