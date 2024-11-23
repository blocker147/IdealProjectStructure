#!/bin/bash

# Variables
URL="http://localhost:8080/customers"
CONTENT_TYPE="application/json"

# Request Payload
read -r -d '' PAYLOAD << EOF
{
  "name": "Bob",
  "age": 23,
  "productIds": []
}
EOF

# Send POST request
curl -X POST "$URL" \
     -H "Content-Type: $CONTENT_TYPE" \
     -d "$PAYLOAD"

echo -e "\nCustomer creation request sent!"
