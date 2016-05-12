'use strict'

var mongoose = require('mongoose')
var Schema = mongoose.Schema

let shelfSchema = new Schema({
  name: { type: String, required: true },
  created_at: Date,
  updated_at: Date
})

shelfSchema.pre('save', function (next) {
  let currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

let Shelf = mongoose.model('Shelf', shelfSchema)

module.exports = Shelf
