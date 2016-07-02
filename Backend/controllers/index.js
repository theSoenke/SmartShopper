'use strict'

const express = require('express')
const mongoose = require('mongoose')
const config = require('../config')
const auth = require('./auth')
const products = require('./products')
const markets = require('./markets')
const search = require('./search')
const lists = require('./lists')

const router = express.Router()

mongoose.connect(config.DB.URL)
mongoose.connection.on('error', function () {
  console.log('Error connecting to MongoDB')
})

router.get('/', function (req, res) {
  res.send('Server running')
})

// API Routes

router.use(auth.checkAuthHeader)
router.post('/user/register', auth.registerUser)
router.post('/user/token', auth.registerFcmToken)

router.use(auth.requireAuthentication)
router.get('/lists', lists.getLists)
router.post('/lists', lists.uploadList)
router.put('/lists/:id', lists.updateList)
router.delete('/lists/:id', lists.deleteList)
router.get('/search/products/:query', search.findProducts)
router.get('/search/user/:query', search.findUser)
router.get('/products', products.getProducts)
router.get('/markets', markets.getMarkets)
router.post('/import/products', products.uploadProducts)
router.post('/import/markets', markets.uploadMarketData)

router.use(function (err, req, res, next) {
  if (req.app.get('env') !== 'development') {
    delete err.stack
  }

  res.status(err.statusCode || 400).json({error: err.message})
})

module.exports = router
