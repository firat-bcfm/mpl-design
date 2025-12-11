#!/bin/bash
set -e

JENKINS_CONTAINER="jenkins"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================="
echo "Creating Jenkins Jobs (No Folders)"
echo "========================================="
echo ""

# Copy job configs directly
echo "Creating FIRAT-DEV..."
docker exec $JENKINS_CONTAINER mkdir -p /var/jenkins_home/jobs/FIRAT-DEV
docker cp "$SCRIPT_DIR/jobs-no-folder/FIRAT-DEV/config.xml" $JENKINS_CONTAINER:/var/jenkins_home/jobs/FIRAT-DEV/config.xml

echo "Creating FIRAT-PROD..."
docker exec $JENKINS_CONTAINER mkdir -p /var/jenkins_home/jobs/FIRAT-PROD
docker cp "$SCRIPT_DIR/jobs-no-folder/FIRAT-PROD/config.xml" $JENKINS_CONTAINER:/var/jenkins_home/jobs/FIRAT-PROD/config.xml

echo "Creating OZKAN-DEV..."
docker exec $JENKINS_CONTAINER mkdir -p /var/jenkins_home/jobs/OZKAN-DEV
docker cp "$SCRIPT_DIR/jobs-no-folder/OZKAN-DEV/config.xml" $JENKINS_CONTAINER:/var/jenkins_home/jobs/OZKAN-DEV/config.xml

echo "Creating OZKAN-PROD..."
docker exec $JENKINS_CONTAINER mkdir -p /var/jenkins_home/jobs/OZKAN-PROD
docker cp "$SCRIPT_DIR/jobs-no-folder/OZKAN-PROD/config.xml" $JENKINS_CONTAINER:/var/jenkins_home/jobs/OZKAN-PROD/config.xml

echo ""
echo "Restarting Jenkins..."
docker restart $JENKINS_CONTAINER

echo ""
echo "========================================="
echo "✓ Jobs created successfully!"
echo "========================================="
echo ""
echo "Wait 20 seconds and refresh: http://localhost:8080"
echo ""
echo "You will see:"
echo "  📄 FIRAT-DEV"
echo "  📄 FIRAT-PROD"
echo "  📄 OZKAN-DEV"
echo "  📄 OZKAN-PROD"
echo ""
echo "========================================="
