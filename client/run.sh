#!/bin/bash

#host=127.0.0.1
host=104.198.231.2
hostTomcat=http://$host:8080
hostApache=http://$host:9191

#1. connect direct
#./echoClient --serverHost $hostTomcat

#2. connect via proxy
./echoClient --serverHost $hostApache
