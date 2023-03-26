#!/bin/bash

if !./gradlew server:application distZip distTar installDist; then
  exit 1
fi

./server/build/install/server/application/bin/server
