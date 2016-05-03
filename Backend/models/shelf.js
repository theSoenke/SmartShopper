'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema

let shelfSchema = new Schema({
  location: String,
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
