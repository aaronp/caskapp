#!/usr/bin/env bash

#
# Good, old-school bash script used to build stuff :-)
#
# You can use this build file locally, provided you have https://scala-cli.virtuslab.org/
#
# ... to just compile/run locally (and browse to localhost:8080 to test the REST app)
# source build.sh && debug
#
# ... to build a fat-jar and run:
# source build.sh && run
#
#

# just for convenience in local development:
# source ./build.sh && clean
function clean() {
  rm *.jar
  rm -rf target
}

# used by Dockerfile to create the app.jar fat jar
function fatJar() {
  [[ -f app.jar ]] || scala-cli package App.scala -o app.jar --assembly
}

# just for convenience/documentation in how to go about local development:
# source ./build.sh && debug
function debug() {
  scala-cli App.scala
}

# just for convenience in running the instrumented fat-jar locally
# source ./build.sh && run
function run() {
  fatJar
  java -jar app.jar
}
