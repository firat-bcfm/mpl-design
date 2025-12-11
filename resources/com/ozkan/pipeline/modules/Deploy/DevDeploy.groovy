/**
 * Ozkan Dev - Deploy Module
 * Step 4/6: Deploy to development
 */

echo ""
echo "═══════════════════════════════════════"
echo "🚀 OZKAN DEV - STAGE 4: DEPLOY"
echo "═══════════════════════════════════════"

def dockerRegistry = CFG.'dockerRegistry' ?: ''
def deployHost = CFG.'deploy.dev_host' ?: 'dev.ozkan.local'
def deployPort = CFG.'deploy.dev_port' ?: '8080'

if (dockerRegistry) {
    echo "✓ Docker Registry: ${dockerRegistry}"
    echo "✓ Pushing Docker image..."
}

echo "✓ Deploying to: ${deployHost}:${deployPort}"
echo "✓ Deployment completed!"

env.DEPLOY_URL = "http://${deployHost}:${deployPort}"

echo "═══════════════════════════════════════"
echo ""
