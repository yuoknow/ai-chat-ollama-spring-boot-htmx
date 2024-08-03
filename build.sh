#!/bin/bash
set -e

imageTag=$1
if [ -z "$1" ]
then
  echo "no image tag provided, latest will be used"
  imageTag=latest
fi
imageFullName=yuknow/chat-ai:$imageTag
echo "building jar..."
./gradlew build bootJar --no-daemon
mkdir -p build/result/ && mv build/libs/*.jar build/result/
echo "building docker image..."
docker build build/result/ -f building/package/Dockerfile -t $imageFullName
echo "image was successfully built $imageFullName"