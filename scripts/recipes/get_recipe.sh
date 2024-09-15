#!/bin/bash

set -e

curl -v http://localhost:8080/recipe/$1 | jq