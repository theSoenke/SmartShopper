'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema

let productSchema = new Schema({
  name: String,
  created_at: Date,
  updated_at: Date
})

productSchema.pre('save', function (next) {
  let currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

let Product = mongoose.model('Product', productSchema)

module.exports = Product
