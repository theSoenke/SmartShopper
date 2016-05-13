'use strict'

var basicAuth = require('basic-auth')
var User = require('../models/user')

exports.registerUser = function (req, res, next) {
  let credentials = basicAuth(req)

  let user = User({
    username: credentials.name,
    password: credentials.pass
  })

  user.save(function (err) {
    if (err) {
      return next(err)
    }

    res.json(user)
  // res.json({status: 'User registered'})
  })
}

exports.requireAuthentication = function (req, res, next) {
  let credentials = basicAuth(req)

  if (!credentials || !credentials.name || !credentials.pass) {
    let err = new Error('Invalid credentials')
    err.statusCode = 401
    return next(err)
  }

  next()
}
