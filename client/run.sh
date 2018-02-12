#!/bin/bash

hostTomcat=http://127.0.0.1:8080
hostApache=http://127.0.0.1:9191

#1. connect direct
./echoClient -serverHost $hostTomcat

#2. connect via proxy
./echoClient -serverHost $hostApache
