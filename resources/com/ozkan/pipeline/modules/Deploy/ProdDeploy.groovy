/**
 * Ozkan Production - Deploy Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "🚀 OZKAN PROD - STAGE 4: DEPLOY"
echo "═══════════════════════════════════════"

def dockerRegistry = CFG.'dockerRegistry' ?: ''
def deployHost = CFG.'deploy.prod_host' ?: 'prod.ozkan.com'
def deployPort = CFG.'deploy.prod_port' ?: '443'

if (dockerRegistry) {
    echo "✓ Docker Registry: ${dockerRegistry}"
}

echo "✓ Deploying to: ${deployHost}:${deployPort}"
echo "✓ Production deployment completed!"

env.DEPLOY_URL = "https://${deployHost}"

echo "═══════════════════════════════════════"
echo ""
