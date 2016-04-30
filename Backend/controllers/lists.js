'use strict'

var express = require('express')
var bodyParser = require('body-parser')

var router = express.Router()
var jsonParser = bodyParser.json()

var List = require('../models/list')

router
  .post('/list/:name', jsonParser, function (req, res) {
    if (!req.body.products) {
      return res.sendStatus(400)
    }

  // var name = req.params.name
  // var products = req.body.products
  // createList(name, items)
  })
  .put('/list:name', jsonParser, function (req, res) {
    var name = req.params.name
    res.json('update: ' + name)
  })
  .delete('/list:name', function (req, res) {
    var name = req.params.name
    res.json('delete: ' + name)
  })

function createList (listName, products) {
  var list = new List({
    name: listName
  })

  list.save(function (err) {
    if (err) {
      throw err
    }

    console.log('list created')
  })
}

module.exports = router
