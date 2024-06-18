# RDF load time benchmarking with RiverBench

This repository contains the code used to evaluate load times of several RDF stores (Apache Jena TDB2, RDF4J Native Store, RDF4J LMDB, Virtuoso Open Source, RDF4Led).

The code here is an updated and heavily modified version of the code this GitHub repository: [https://github.com/anhlt18vn/sensors2020](https://github.com/anhlt18vn/sensors2020). It accompanied the 2020 paper ["Pushing the Scalability of RDF Engines on IoT Edge Devices"](https://doi.org/10.3390/s20102788) by Anh Le-Tuan et al.

## Running the benchmark

The benchmark is pre-packaged into a Docker container, and this repository additionally contains ready-to-use Bash scripts that automate the process of running the benchmark for all RDF engines and datasets. The Docker image is multi-architecture, built for both x86-64 and ARM64 CPUs. There is no official support for 32-bit architectures.

You can find the Docker image here: https://github.com/Ostrzyciel/rdf4led-riverbench/pkgs/container/rdf4led-riverbench

Pull it with `docker pull ghcr.io/ostrzyciel/rdf4led-riverbench:main`

### System requirements

- x86-64 or ARM64 CPU architecture
- A Linux operating system with Bash shell
- Docker
- `tar` command-line utility

The scripts were tested on Linux 6.6.16, Bash 5.2.12 and GNU tar 1.35, but they should work on any other reasonably recent version of these tools.

### Preparing the directory structure

The benchmarking scripts require you to create a directory to hold the data, triple storages, and the results of the experiments. You can create it in your current directory by running the following commands:

```sh
mkdir -p exp/data
mkdir -p exp/store
mkdir -p exp/results
```

### Preparing the datasets

The `python` directory contains the `prepare_datasets.ipynb` Jupyter notebook that automatically downloads the datasets from RiverBench and re-splits them. You can also download the already prepared datasets from [Zenodo](https://doi.org/10.5281/zenodo.12073223) – just download the `.tar.gz` files and place them in the `exp/data` directory.

### Benchmarking

To run the benchmark for all RDF engines and all datasets, execute the following command from the root of this repository:

```sh
./console/run_all.sh [PATH-TO-EXP-DIRECTORY] [MEMORY-LIMIT]
```

The `[MEMORY-LIMIT]` parameter is passed to Docker and should be in the format like `128M` (for 128 megabytes) or `16G` (for 16 gigabytes).

You can also run the benchmark for a single dataset:

```sh
./console/run_one_dataset.sh [PATH-TO-EXP-DIRECTORY] [MEMORY-LIMIT] [DATASET-NAME]
```

The `[DATASET-NAME]` parameter should be the same as in RiverBench, for example `assist-iot-weather` or `muziekweb`.

## Benchmark results

Benchmark results are available on Zenodo: [https://doi.org/10.5281/zenodo.12087112](https://doi.org/10.5281/zenodo.12087112)

## Citation

A preprint of this work will be published soon.

## Authors

- [Piotr Sowiński](https://github.com/Ostrzyciel) ([ORCID](https://orcid.org/0000-0002-2543-9461))
- [Anh Le-Tuan](https://github.com/anhlt18vn) ([ORCID](https://orcid.org/0000-0003-2458-607X)) – the original version of the code from 2020
