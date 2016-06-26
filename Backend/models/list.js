'use strict'

const mongoose = require('mongoose')

const Schema = mongoose.Schema
const ObjectId = Schema.ObjectId

let listSchema = new Schema({
  name: {type: String, required: true, text: true},
  owner: {type: ObjectId, required: true, ref: 'User'},
  products: [{
    _id: false,
    product: {type: ObjectId, ref: 'Product'},
    amount: Number
  }],
  participants: [{type: ObjectId, ref: 'User'}],
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
