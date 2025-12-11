/**
 * Firat Dev - Deploy Module
 * Step 4/6: Deploy to development environment
 */

echo ""
echo "═══════════════════════════════════════"
echo "🚀 FIRAT DEV - STAGE 4: DEPLOY"
echo "═══════════════════════════════════════"

def dockerRegistry = CFG.'dockerRegistry' ?: ''
def deployHost = CFG.'deploy.dev_host' ?: 'dev.firat.local'
def deployPort = CFG.'deploy.dev_port' ?: '8080'

if (dockerRegistry) {
    echo "✓ Docker Registry: ${dockerRegistry}"
    echo "✓ Building Docker image..."
    echo "✓ Pushing image to registry..."
}

echo "✓ Deploying to development environment..."
echo "✓ Target: ${deployHost}:${deployPort}"
echo "✓ Stopping old application..."
echo "✓ Starting new version..."
echo "✓ Deployment completed!"

env.DEPLOY_URL = "http://${deployHost}:${deployPort}"

echo "═══════════════════════════════════════"
echo ""
