var config = {}

config.mongodb = {}
config.web = {}

config.mongodb.url = 'mongodb://localhost/test'
config.web.port = process.env.PORT || 3000

module.exports = config
