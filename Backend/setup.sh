#!/bin/bash

apt -y update
curl -sL https://deb.nodesource.com/setup_5.x | sudo -E bash -
apt install -y nodejs
npm install pm2 -g

# create new user to run app
useradd -mrU web
mkdir -p /var/www
chown web /var/www
chgrp web /var/www
cd /var/www/
su web

git clone https://github.com/theSoenke/SmartShopper
cd SmartShopper/Backend

pm2 start app.js -i 0 --name "api"
pm2 startup systemd

# pm2 reload api
# pm2 stop api
