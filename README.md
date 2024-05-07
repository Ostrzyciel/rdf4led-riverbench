

## Running the benchmark

The benchmark is pre-packaged into a Docker container, and this repository additionally contains ready-to-use Bash scripts that automate the process of running the benchmark for all RDF engines and datasets. The Docker image is multi-architecture, built for both x86-64 and ARM64 CPUs. There is no official support for 32-bit architectures.

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

The `python` directory contains the `prepare_datasets.ipynb` Jupyter notebook that automatically downloads the datasets from RiverBench and re-splits them. You can also download the already prepared datasets from [GitHub releases of this repo](https://github.com/Ostrzyciel/rdf4led-riverbench/releases/tag/datasets) – just download the `.tar.gz` files and place them in the `exp/data` directory.

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
