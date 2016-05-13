'use strict'

var config = {}

config.database = {}
config.web = {}

config.database.url = 'mongodb://localhost/smartshopper'
config.web.port = process.env.PORT || 3000

module.exports = config
