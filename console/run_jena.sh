#!/bin/bash

docker run -it --rm \
  -v "$(realpath "$1")/data:/exp/data" \
  -v "$(realpath "$1")/result:/exp/result" \
  ghcr.io/ostrzyciel/rdf4led-riverbench:main \
  java -cp "./eu.ostrzyciel.rb_load_time.jenatdb/*:./eu.ostrzyciel.rb_load_time.jenatdb/libs/*" \
    eu.ostrzyciel.rb_load_time.benchmark.jenatdb.JenaTdbInput \
    "config/jena_test.json"
