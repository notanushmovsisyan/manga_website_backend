#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api/v1/auth"
TEST_USER="user_$(date +%s)"
TEST_PASS="password123"

# Helper function for JSON POST requests
post_json() {
    curl -s -X POST "$1" \
        -H "Content-Type: application/json" \
        -d "$2"
}

# Helper function for Authenticated GET requests
get_auth() {
    curl -s -X GET "$1" \
        -H "Authorization: Bearer $2"
}

echo "Starting authentication test flow..."

# 1. Registration
echo "Step 1: Registering user [$TEST_USER]"
REG_DATA="{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\",\"role\":\"ROLE_USER\"}"
REG_RESPONSE=$(post_json "$BASE_URL/register" "$REG_DATA")

if [[ $REG_RESPONSE != *"accessToken"* ]]; then
    echo "Error: Registration failed"
    echo "$REG_RESPONSE"
    exit 1
fi
echo "Registration: Success"

# 2. Login
echo "Step 2: Logging in"
LOGIN_DATA="{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}"
LOGIN_RESPONSE=$(post_json "$BASE_URL/login" "$LOGIN_DATA")

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken')
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.refreshToken')

if [ "$ACCESS_TOKEN" == "null" ] || [ -z "$ACCESS_TOKEN" ]; then
    echo "Error: Login failed, no token received"
    exit 1
fi
echo "Login: Success"

# 3. Validation
echo "Step 3: Validating session"
VALIDATE_RESPONSE=$(get_auth "$BASE_URL/validate" "$ACCESS_TOKEN")

STATUS=$(echo "$VALIDATE_RESPONSE" | jq -r '.status')
USERNAME=$(echo "$VALIDATE_RESPONSE" | jq -r '.username')

if [ "$STATUS" == "Valid" ] && [ "$USERNAME" == "$TEST_USER" ]; then
    echo "Validation: Success (User: $USERNAME)"
else
    echo "Error: Token validation failed"
    echo "$VALIDATE_RESPONSE"
    exit 1
fi

echo "Auth flow test completed successfully."