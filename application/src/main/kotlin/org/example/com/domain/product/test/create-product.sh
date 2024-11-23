#!/bin/bash

# Variables
URL="http://localhost:8080/products"
CONTENT_TYPE="application/json"

# Request Payload
read -r -d '' PAYLOAD << EOF
{
  "title": "Banana",
  "count": 1
}
EOF

# Send POST request
curl -X POST "$URL" \
     -H "Content-Type: $CONTENT_TYPE" \
     -d "$PAYLOAD"

echo -e "\nProduct creation request sent!"
