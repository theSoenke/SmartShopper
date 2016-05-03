'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Product = require('./product')

let listSchema = new Schema({
  name: String,
  products: [Product],
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
