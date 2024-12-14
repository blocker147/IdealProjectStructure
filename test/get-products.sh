#!/bin/bash

# Variables
BASE_URL="http://localhost:8080/products/e205b22a-2c62-461f-a800-8ef93be1b378"

# Send GET request
curl -X GET "$BASE_URL"

echo -e "\nGet products!"
