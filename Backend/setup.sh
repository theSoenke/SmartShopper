#!/bin/bash

set -ex

apt update
apt install -y curl git
curl -sL https://deb.nodesource.com/setup_5.x | bash -
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.2 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.2.list

apt update
apt install -y nodejs
apt install -y mongodb-org
npm install pm2 -g


# setup mongodb
echo '[Unit]
Description=High-performance, schema-free document-oriented database

[Service]
User=mongod
Group=mongod
OPTIONS=--quiet -f /etc/mongod.conf
ExecStart=/usr/bin/mongod $OPTIONS run
PIDFile=/var/run/mongodb/mongod.pid

[Install]
WantedBy=multi-user.target' > /lib/systemd/system/mongod.service

systemctl daemon-reload
systemctl enable mongod
systemctl start mongod


# create new user to run app
useradd -mrU web
mkdir -p /var/www
chown web /var/www
chgrp web /var/www

mv ~/SmartShopper /var/www/
cd /var/www/SmartShopper/Backend
npm install

su web
pm2 start app.js -i 0 --name "api"
pm2 startup systemd

# pm2 reload api
# pm2 stop api
