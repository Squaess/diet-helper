#!/bin/bash

set -e 

if [[ $2 == "Vegetables" ]]
then
  echo "OK"
elif [[ $2 == "Others" ]]
then
  echo "OK"
elif [[ $2 == "Fridge" ]]
then
  echo "OK"
else
  echo "Wrong Category"
  exit 1
fi

curl -X POST http://localhost:8080/product -d "{\"name\":\"$1\",\"category\":{\"$2\":{}}}"