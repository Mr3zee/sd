#!/bin/bash

./gradlew server:profiler:distZip server:profiler:distTar server:profiler:installDist --configuration-cache

./server/profiler/build/install/profiler/bin/profiler
