'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema

let marketSchema = new Schema({
  name: String,
  created_at: Date,
  updated_at: Date
})

marketSchema.pre('save', function (next) {
  let currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

let Market = mongoose.model('Market', marketSchema)

module.exports = Market
