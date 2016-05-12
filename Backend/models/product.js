'use strict'

var mongoose = require('mongoose')
var Schema = mongoose.Schema

let productSchema = new Schema({
  name: { type: String, required: true, text: true },
  created_at: Date,
  updated_at: Date
},
  {
    versionKey: false
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
