#!/bin/bash

set -e 

curl -X POST http://localhost:8080/recipe -d "$(cat $1)"