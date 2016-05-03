'use strict'

const express = require('express')
const mongoose = require('mongoose')
const router = express.Router()
const config = require('../config')

mongoose.connect(config.mongodb.url)

router.use(require('./lists'))
router.use(require('./products'))
router.use(require('./search'))

router.get('/', function (req, res) {
  res.send('Server running')
})

module.exports = router
