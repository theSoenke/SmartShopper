var mongoose = require('mongoose')
var Schema = mongoose.Schema

var listSchema = new Schema({
  name: String,
  id: String,
  created_at: Date,
  updated_at: Date
})

listSchema.pre('save', function (next) {
  var currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at) {
    this.created_at = currentDate
  }

  next()
})

var List = mongoose.model('List', listSchema)

module.exports = List
