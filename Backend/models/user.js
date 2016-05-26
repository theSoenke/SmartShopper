'use strict'

var mongoose = require('mongoose')
var bcrypt = require('bcrypt')

var Schema = mongoose.Schema
const SALT_WORK_FACTOR = 10

let userSchema = new Schema({
  username: { type: String, required: true, unique: true, text: true },
  password: { type: String, required: true },
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
  let hash = bcrypt.hashSync(this.password, SALT_WORK_FACTOR)
  this.password = hash
  next()
})

userSchema.methods.comparePasswords = function (candidatePassword, cb) {
  bcrypt.compare(candidatePassword, this.password, function(err, isMatch){
    if(err) {
      return cb(err)
    }
    cb(null, isMatch)
  })
}

let User = mongoose.model('User', userSchema)

module.exports = User
