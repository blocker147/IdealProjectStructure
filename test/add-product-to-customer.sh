#!/bin/bash

# Variables
CUSTOMER_ID="d46af006-686c-434b-854f-31747f3f14ee"
PRODUCT_ID="3bf9e201-4d79-4d63-903e-5a41acd5fa8b"
URL="http://localhost:8080/customers/$CUSTOMER_ID"
CONTENT_TYPE="application/json"

# Send PUT request
curl -X PUT "$URL?productId=$PRODUCT_ID" \
     -H "Content-Type: $CONTENT_TYPE" \

echo -e "\nProduct added to a customer request sent!"
