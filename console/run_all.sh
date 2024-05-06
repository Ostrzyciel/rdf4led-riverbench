#!/bin/bash

EXP_DIR="$1"
DATASET="$2"

ENGINES=(
  'jena JenaTdbInput eu.ostrzyciel.rb_load_time.jenatdb'
  'rdf4j_native RDF4JInput eu.ostrzyciel.rb_load_time.rdf4j'
  'rdf4j_lmdb RDF4JInput eu.ostrzyciel.rb_load_time.rdf4j'
  'virtuoso VirtuosoInput eu.ostrzyciel.rb_load_time.virtuoso'
  'rdf4led RDF4LedInput eu.ostrzyciel.rb_load_time.rdf4led'
)

docker pull ghcr.io/ostrzyciel/rdf4led-riverbench:main

for engine in "${ENGINES[@]}"; do
  IFS=' ' read -r -a engine <<< "$engine"
  docker run -it --rm \
    -v "$(realpath "$EXP_DIR")/data:/exp/data" \
    -v "$(realpath "$EXP_DIR")/result:/exp/result" \
    ghcr.io/ostrzyciel/rdf4led-riverbench:main \
    java -cp "./${engine[2]}/*:./${engine[2]}/libs/*" \
      ${engine[2]}."${engine[1]}" \
      "config/${engine[0]}_${DATASET}.json"
done
