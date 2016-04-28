var mongoose = require('mongoose')
var Schema = mongoose.Schema

var shelfSchema = new Schema({
  location: String,
  created_at: Date,
  updated_at: Date
})

shelfSchema.pre('save', function (next) {
  var currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

var Shelf = mongoose.model('Shelf', shelfSchema)

module.exports = Shelf
