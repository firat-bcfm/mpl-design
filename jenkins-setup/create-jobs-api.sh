#!/bin/bash

#################################################
# Jenkins Job Creator - Direct API Method
#################################################

JENKINS_URL="http://localhost:8080"
JENKINS_USER="admin"
JENKINS_PASS="admin"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================="
echo "Creating Jenkins Jobs via API"
echo "========================================="
echo ""

# Get crumb
echo "Getting crumb..."
CRUMB=$(curl -s -u $JENKINS_USER:$JENKINS_PASS "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
echo "Crumb: $CRUMB"
echo ""

# Delete old jobs if exist
echo "Cleaning up old jobs..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS -H "$CRUMB" "$JENKINS_URL/job/FIRAT/doDelete" 2>/dev/null || true
curl -X POST -u $JENKINS_USER:$JENKINS_PASS -H "$CRUMB" "$JENKINS_URL/job/OZKAN/doDelete" 2>/dev/null || true
sleep 2

# Create FIRAT folder
echo "Creating FIRAT folder..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/createItem?name=FIRAT" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ FIRAT folder created"
else
    echo "✗ Failed to create FIRAT folder"
fi

sleep 1

# Create FIRAT/DEV
echo "Creating FIRAT/DEV..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/FIRAT/createItem?name=DEV" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/jobs/DEV/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ FIRAT/DEV created"
else
    echo "✗ Failed to create FIRAT/DEV"
fi

sleep 1

# Create FIRAT/PROD
echo "Creating FIRAT/PROD..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/FIRAT/createItem?name=PROD" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/jobs/PROD/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ FIRAT/PROD created"
else
    echo "✗ Failed to create FIRAT/PROD"
fi

sleep 1

# Create OZKAN folder
echo "Creating OZKAN folder..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/createItem?name=OZKAN" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ OZKAN folder created"
else
    echo "✗ Failed to create OZKAN folder"
fi

sleep 1

# Create OZKAN/DEV
echo "Creating OZKAN/DEV..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/OZKAN/createItem?name=DEV" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/jobs/DEV/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ OZKAN/DEV created"
else
    echo "✗ Failed to create OZKAN/DEV"
fi

sleep 1

# Create OZKAN/PROD
echo "Creating OZKAN/PROD..."
curl -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/OZKAN/createItem?name=PROD" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/jobs/PROD/config.xml"

if [ $? -eq 0 ]; then
    echo "✓ OZKAN/PROD created"
else
    echo "✗ Failed to create OZKAN/PROD"
fi

echo ""
echo "========================================="
echo "✓ Done!"
echo "========================================="
echo ""
echo "Refresh Jenkins: http://localhost:8080"
echo ""
