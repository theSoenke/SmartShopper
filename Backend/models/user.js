'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema

let userSchema = new Schema({
  username: String,
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

  next()
})

let User = mongoose.model('User', userSchema)

module.exports = User
