'use strict'

var mongoose = require('mongoose')
var Schema = mongoose.Schema

let listSchema = new Schema({
  name: { type: String, required: true, text: true },
  products: [{ name: { type: String, required: true } }],
  owner: { type: String, required: true },
  created_at: Date,
  updated_at: Date
})

listSchema.pre('save', function (next) {
  let currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

let List = mongoose.model('List', listSchema)

module.exports = List
