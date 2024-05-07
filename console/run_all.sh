#!/bin/bash

EXP_DIR="$1"

DATASETS=(
  'assist-iot-weather'
  'citypulse-traffic'
  'dbpedia-live'
  'digital-agenda-indicators'
  'linked-spending'
  'lod-katrina'
  'muziekweb'
  'politiquices'
)

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

for dataset in "${DATASETS[@]}"; do
  bash "${__dir}/run_one_dataset.sh" "$EXP_DIR" "$dataset"
done
