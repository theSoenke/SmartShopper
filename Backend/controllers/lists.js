'use strict'

const express = require('express')
const bodyParser = require('body-parser')

const router = express.Router()
const jsonParser = bodyParser.json()
const List = require('../models/list')
const Product = require('../models/product')

router
  .post('/list/:name', jsonParser, function (req, res) {
    let products = req.body.products
    let name = req.params.name
    if (!products) {
      return res.sendStatus(400)
    }

    createList(name, products)
    res.json({'name': name, 'products': products})
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
  let productList = []

  products.forEach(function (obj) {
    Product.findOne({'name': obj.id}, function (err, docs) {
      if (err) {
        throw err
      }
      console.log(docs)
    // productList.push(docs)
    })
  })

  // console.log(productList)

  let list = new List({
    name: listName,
    products: productList
  })

  list.save(function (err) {
    if (err) {
      throw err
    }

    console.log('list created')
  })
}

module.exports = router
