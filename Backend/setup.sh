#!/bin/bash

curl -sL https://deb.nodesource.com/setup_5.x | sudo -E bash -
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list

apt update
apt install -y nodejs mongodb-org
npm install pm2 -g

#git clone https://github.com/theSoenke/SmartShopper
mv /root/SmartShopper /var/www/
cd /var/www/SmartShopper/Backend
npm install

# create new user to run app
useradd -mrU web
mkdir -p /var/www
chown web /var/www
chgrp web /var/www
su web

pm2 start app.js -i 0 --name "api"
pm2 startup systemd

# pm2 reload api
# pm2 stop api
