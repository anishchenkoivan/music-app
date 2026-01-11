#!/bin/sh

# Generate env-config.js with runtime environment variables
cat <<EOF > /usr/share/nginx/html/env-config.js
window.ENV = {
  API_URL: "${API_URL:-http://localhost:8080}"
};
EOF

echo "Environment configuration generated:"
cat /usr/share/nginx/html/env-config.js
