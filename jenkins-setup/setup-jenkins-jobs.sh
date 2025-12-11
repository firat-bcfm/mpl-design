#!/bin/bash
set -e

#################################################
# Jenkins Jobs Setup Script with Authentication
# Creates folder structure with jobs using Jenkins API
#################################################

JENKINS_CONTAINER="jenkins"
JENKINS_URL="http://localhost:8080"
JENKINS_USER="admin"
JENKINS_PASS="admin"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================="
echo "Jenkins Folder & Jobs Setup Script"
echo "========================================="
echo ""

# Jenkins container kontrolü
echo "Checking Jenkins container..."
if ! docker ps | grep -q "$JENKINS_CONTAINER"; then
    echo "❌ Error: Jenkins container '$JENKINS_CONTAINER' is not running!"
    echo "Start Jenkins with: docker start jenkins"
    exit 1
fi

echo "✓ Jenkins container is running"
echo ""

# Jenkins hazır mı kontrol et
echo "Waiting for Jenkins to be ready..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s "$JENKINS_URL" > /dev/null 2>&1; then
        echo "✓ Jenkins is ready"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "Attempt $RETRY_COUNT/$MAX_RETRIES - waiting..."
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "❌ Error: Jenkins is not responding after $MAX_RETRIES attempts"
    exit 1
fi

echo ""
echo "Jenkins Login:"
echo "  URL: $JENKINS_URL"
echo "  Username: $JENKINS_USER"
echo "  Password: $JENKINS_PASS"
echo ""

echo "========================================="
echo "Creating Folders & Jobs via Jenkins API"
echo "========================================="
echo ""

# Crumb almak (CSRF protection için)
echo "Getting Jenkins crumb..."
CRUMB=$(curl -s -u $JENKINS_USER:$JENKINS_PASS "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
echo "✓ Crumb obtained"

# FIRAT folder oluştur
echo ""
echo "Creating FIRAT folder..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/createItem?name=FIRAT" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/config.xml" > /dev/null 2>&1 || echo "  (folder may already exist)"

echo "✓ FIRAT folder created"

# FIRAT/DEV job oluştur
echo "Creating FIRAT/DEV job..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/FIRAT/createItem?name=DEV" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/jobs/DEV/config.xml" > /dev/null 2>&1 || echo "  (job may already exist)"

echo "✓ FIRAT/DEV job created"

# FIRAT/PROD job oluştur
echo "Creating FIRAT/PROD job..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/FIRAT/createItem?name=PROD" \
  --data-binary @"$SCRIPT_DIR/jobs/FIRAT/jobs/PROD/config.xml" > /dev/null 2>&1 || echo "  (job may already exist)"

echo "✓ FIRAT/PROD job created"
echo ""

# OZKAN folder oluştur
echo "Creating OZKAN folder..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/createItem?name=OZKAN" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/config.xml" > /dev/null 2>&1 || echo "  (folder may already exist)"

echo "✓ OZKAN folder created"

# OZKAN/DEV job oluştur
echo "Creating OZKAN/DEV job..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/OZKAN/createItem?name=DEV" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/jobs/DEV/config.xml" > /dev/null 2>&1 || echo "  (job may already exist)"

echo "✓ OZKAN/DEV job created"

# OZKAN/PROD job oluştur
echo "Creating OZKAN/PROD job..."
curl -s -X POST -u $JENKINS_USER:$JENKINS_PASS \
  -H "$CRUMB" \
  -H "Content-Type: application/xml" \
  "$JENKINS_URL/job/OZKAN/createItem?name=PROD" \
  --data-binary @"$SCRIPT_DIR/jobs/OZKAN/jobs/PROD/config.xml" > /dev/null 2>&1 || echo "  (job may already exist)"

echo "✓ OZKAN/PROD job created"
echo ""

echo "========================================="
echo "✓ Setup completed successfully!"
echo "========================================="
echo ""
echo "🎉 Jenkins is ready to use!"
echo ""
echo "Login Info:"
echo "  🌐 URL: http://localhost:8080"
echo "  👤 Username: admin"
echo "  🔑 Password: admin"
echo ""
echo "Created Structure:"
echo "  📁 FIRAT/"
echo "    ├── 📄 DEV   → http://localhost:8080/job/FIRAT/job/DEV"
echo "    └── 📄 PROD  → http://localhost:8080/job/FIRAT/job/PROD"
echo ""
echo "  📁 OZKAN/"
echo "    ├── 📄 DEV   → http://localhost:8080/job/OZKAN/job/DEV"
echo "    └── 📄 PROD  → http://localhost:8080/job/OZKAN/job/PROD"
echo ""
echo "Next Steps:"
echo "  1. Open: http://localhost:8080"
echo "  2. Login with admin/admin"
echo "  3. You'll see FIRAT and OZKAN folders!"
echo "  4. Configure Shared Library (see JENKINS_SETUP.md)"
echo "  5. Run a build from FIRAT/DEV!"
echo ""
echo "========================================="
