var config = {}

config.database = {}
config.web = {}

config.database.url = 'mongodb://localhost/test'
config.web.port = process.env.PORT || 3000

module.exports = config
