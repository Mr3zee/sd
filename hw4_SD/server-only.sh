#!/bin/bash

./gradlew server:application:distZip server:application:distTar server:application:installDist --configuration-cache

./server/application/build/install/application/bin/application
