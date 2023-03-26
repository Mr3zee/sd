#!/bin/bash

if !./gradlew server:stock distZip distTar installDist; then
  exit 1
fi

./server/build/install/server/stock/bin/server
