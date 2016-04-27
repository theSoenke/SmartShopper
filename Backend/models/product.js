var mongoose = require('mongoose')
var Schema = mongoose.Schema

var productSchema = new Schema({
  name: String,
  created_at: Date,
  updated_at: Date
})

productSchema.pre('save', function(next) {
  var currentDate = new Date()
  this.updated_at = currentDate

  if (!this.created_at)  {
    this.created_at = currentDate
  }

  next()
})

var Product = mongoose.model('Product', productSchema)

module.exports = Product
