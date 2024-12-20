#!/bin/bash

# Variables
BASE_URL="http://localhost:8080/products/search"
PRODUCT_ID="3bf9e201-4d79-4d63-903e-5a41acd5fa8b"

# Send GET request
curl -X GET "$BASE_URL?id=$PRODUCT_ID"

echo -e "\nSearch request sent!"
