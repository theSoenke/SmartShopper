'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema
const ObjectId = Schema.ObjectId

let marketSchema = new Schema({
  name: {type: String, required: true, text: true},
  products: [ObjectId],
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
