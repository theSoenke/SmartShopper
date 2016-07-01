'use strict'

const mongoose = require('mongoose')
const bcrypt = require('bcrypt')

const Schema = mongoose.Schema
const SALT_WORK_FACTOR = 10

let userSchema = new Schema({
  name: {type: String, required: true, unique: true, text: true},
  password: {type: String, required: true},
  fcmToken: String,
  admin: Boolean,
  created_at: Date,
  updated_at: Date
})

userSchema.pre('save', function (next) {
  let currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  if (!this.isModified('password')) {
    return next()
  }

  // override the cleartext password with the hashed one
  let user = this
  bcrypt.genSalt(SALT_WORK_FACTOR, function (err, salt) {
    if (err) {
      return next(err)
    }

    bcrypt.hash(user.password, salt, function (err, hash) {
      if (err) {
        return next(err)
      }
      user.password = hash
      next()
    })
  })
})

userSchema.methods.comparePasswords = function (candidatePassword, cb) {
  bcrypt.compare(candidatePassword, this.password, function (err, isMatch) {
    if (err) {
      return cb(err)
    }
    cb(null, isMatch)
  })
}

let User = mongoose.model('User', userSchema)

module.exports = User
