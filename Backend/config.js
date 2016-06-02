const config = {
  PORT: 3000,
  DB: {
    USER: '',
    PASS: '',
    PORT: 27017,
    HOST: 'localhost',
    DATABASE: 'smartshopper'
  }
}

config.PORT = process.env.PORT | config.PORT

if (config.DB.USER) {
  config.DB.URL = 'mongodb://' + config.DB.USER + ':' + config.DB.PASS + '@' + config.DB.HOST + ':' + config.DB.PORT + '/' + config.DB.DATABASE
} else {
  config.DB.URL = 'mongodb://' + config.DB.HOST + ':' + config.DB.PORT + '/' + config.DB.DATABASE
}

module.exports = config
