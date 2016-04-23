#!/bin/bash

apt -y update
curl -sL https://deb.nodesource.com/setup_5.x | sudo -E bash -
apt install -y nodejs
