'use strict'

const express = require('express')

const router = express.Router()
const List = require('../models/list')

router
  .get('/lists', function (req, res, next) {
    List.find(function (err, docs) {
      if (err) {
        return next(err)
      }

      res.json(docs)
    })
  })
  .post('/list', function (req, res, next) {
    let list = List()
    list.name = req.body.name
    list.products = req.body.products

    list.save(function (err) {
      if (err) {
        return next(err)
      }
      res.json(list)
    })
  })
  .put('/list/:name', function (req, res) {
    let name = req.params.name
    res.json('update: ' + name)
  })
  .delete('/list/:name', function (req, res) {
    let name = req.params.name
    res.json('delete: ' + name)
  })

module.exports = router
