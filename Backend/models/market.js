var mongoose = require('mongoose')
var Schema = mongoose.Schema

var marketSchema = new Schema({
  name: String,
  created_at: Date,
  updated_at: Date
})

marketSchema.pre('save', function (next) {
  var currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

var Market = mongoose.model('Market', marketSchema)

module.exports = Market
