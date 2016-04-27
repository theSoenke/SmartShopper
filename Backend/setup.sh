#!/bin/bash

curl -sL https://deb.nodesource.com/setup_5.x | sudo -E bash -
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list

apt update
apt install -y nodejs mongodb-org

# create new user to run app
useradd -mrU web
mkdir -p /var/www
chown web /var/www
chgrp web /var/www
cd /var/www/
su web


#git clone https://github.com/theSoenke/SmartShopper
#cd SmartShopper/Backend
#npm install
#npm install pm2 -g

#pm2 start app.js -i 0 --name "api"
#pm2 startup systemd

# pm2 reload api
# pm2 stop api
