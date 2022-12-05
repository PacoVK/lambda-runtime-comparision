#!/bin/bash

echo "Building Groovy"
docker run --rm -u gradle -v $PWD/groovy:/app -w /app gradle:7.6 gradle build
echo "Building Java"
docker run --rm -u gradle -v $PWD/java:/app -w /app gradle:7.6 gradle build
echo "Building Typescript"
docker run --rm --interactive --tty -v $PWD/typescript:/app -w /app node:18 yarn build
echo "Building Php"
docker run --rm --interactive --tty -v $PWD/php:/app composer install --classmap-authoritative --no-dev --no-scripts
echo "Building Go"
docker run --rm -v $PWD/golang:/app -w /app -e GOARCH=amd64 -e GOOS=linux -e CGO_ENABLED=0 golang:1.19 go build -o main -v .
echo "Building Quarkus native"
cd quarkus && ./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true && cd ..
