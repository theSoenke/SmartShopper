'use strict'

var basicAuth = require('basic-auth')
var User = require('../models/user')

/* checks whether basic auth exists
needs to be called before registering or checking user*/
exports.checkAuthHeader = function (req, res, next) {
  let credentials = basicAuth(req)

  if (!credentials || !credentials.name || !credentials.pass) {
    let err = new Error('Invalid credentials')
    err.statusCode = 401
    return next(err)
  }

  next()
}

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

    res.json({
      username: user.username,
      status: 'User registered'
    })
  })
}

exports.requireAuthentication = function (req, res, next) {
  let credentials = basicAuth(req)

  User.findOne({username: credentials.name}, function (err, doc) {
    if (err) {
      return next(err)
    }

    if (!doc) {
      let error = new Error('User does not exist')
      return next(error)
    }

    doc.comparePasswords(credentials.pass, function (err, isMatch) {
      if (err) {
        return next(err)
      }

      if (isMatch) {
        next()
      } else {
        let error = new Error('Passwords do not match')
        next(error)
      }
    })
  })
}
