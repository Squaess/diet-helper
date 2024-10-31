#!/bin/bash

set -e

curl -v http://localhost:8080/product/$1 | jq